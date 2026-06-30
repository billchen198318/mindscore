package org.qifu.md.logic.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.ServiceAuthority;
import org.qifu.base.model.ServiceMethodAuthority;
import org.qifu.base.model.ServiceMethodType;
import org.qifu.md.entity.MdActionItem;
import org.qifu.md.entity.MdActionSourceLink;
import org.qifu.md.entity.MdInsight;
import org.qifu.md.entity.MdInsightEvidence;
import org.qifu.md.entity.MdInsightRecommendation;
import org.qifu.md.entity.MdLlmProviderConfig;
import org.qifu.md.entity.MdLlmRunLog;
import org.qifu.md.logic.IInsightRecommendationWorkflowService;
import org.qifu.md.service.IMdActionItemService;
import org.qifu.md.service.IMdActionSourceLinkService;
import org.qifu.md.service.IMdInsightEvidenceService;
import org.qifu.md.service.IMdInsightRecommendationService;
import org.qifu.md.service.IMdInsightService;
import org.qifu.md.service.IMdLlmProviderConfigService;
import org.qifu.md.service.IMdLlmRunLogService;
import org.qifu.util.EncryptorUtils;
import org.qifu.util.LoadResources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.core.type.TypeReference;

@Service
@ServiceAuthority(check = true)
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class InsightRecommendationWorkflowServiceImpl implements IInsightRecommendationWorkflowService {
    private static final String OPENAI = "OPENAI";
    private static final String GEMINI = "GEMINI";
    private static final String STATUS_OPEN = "OPEN";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(60);

    private final IMdInsightService<MdInsight, String> insightService;
    private final IMdInsightEvidenceService<MdInsightEvidence, String> evidenceService;
    private final IMdInsightRecommendationService<MdInsightRecommendation, String> recommendationService;
    private final IMdLlmProviderConfigService<MdLlmProviderConfig, String> providerService;
    private final IMdLlmRunLogService<MdLlmRunLog, String> runLogService;
    private final IMdActionItemService<MdActionItem, String> actionItemService;
    private final IMdActionSourceLinkService<MdActionSourceLink, String> actionSourceLinkService;
    private final HttpClient httpClient;
    private final String encryptionKey;

    public InsightRecommendationWorkflowServiceImpl(
            IMdInsightService<MdInsight, String> insightService,
            IMdInsightEvidenceService<MdInsightEvidence, String> evidenceService,
            IMdInsightRecommendationService<MdInsightRecommendation, String> recommendationService,
            IMdLlmProviderConfigService<MdLlmProviderConfig, String> providerService,
            IMdLlmRunLogService<MdLlmRunLog, String> runLogService,
            IMdActionItemService<MdActionItem, String> actionItemService,
            IMdActionSourceLinkService<MdActionSourceLink, String> actionSourceLinkService,
            @Value("${mindscore.llm.encryption-key:}") String encryptionKey) {
        this.insightService = insightService;
        this.evidenceService = evidenceService;
        this.recommendationService = recommendationService;
        this.providerService = providerService;
        this.runLogService = runLogService;
        this.actionItemService = actionItemService;
        this.actionSourceLinkService = actionSourceLinkService;
        this.encryptionKey = encryptionKey;
        this.httpClient = HttpClient.newBuilder().connectTimeout(REQUEST_TIMEOUT).build();
    }
    @Override
    @ServiceMethodAuthority(type = ServiceMethodType.INSERT)
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    public MdInsightRecommendation generateLlmRecommendation(Map<String, Object> request) throws ServiceException {
        String insightOid = stringParam(request, "insightOid");
        MdInsight insight = loadInsight(insightOid);
        List<MdInsightEvidence> evidenceList = loadEvidence(insightOid);
        MdLlmProviderConfig provider = loadProvider(stringParam(request, "providerOid"));
        String apiKey = decryptApiKey(provider);
        String prompt = buildPrompt(insight, evidenceList, stringParam(request, "promptHint"));
        Date startedAt = new Date();
        long startNanos = System.nanoTime();
        String requestId = null;
        try {
            HttpResponse<String> response = httpClient.send(buildGenerationRequest(provider, apiKey, prompt), HttpResponse.BodyHandlers.ofString());
            requestId = response.headers().firstValue("x-request-id").orElse(null);
            long durationMs = (System.nanoTime() - startNanos) / 1_000_000L;
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                writeRunLog(provider, insightOid, requestId, startedAt, new Date(), durationMs, false,
                        "HTTP_" + response.statusCode(), StringUtils.abbreviate(response.body(), 2000));
                throw new ServiceException("LLM provider returned HTTP " + response.statusCode());
            }
            MdInsightRecommendation recommendation = saveLlmRecommendation(insight, extractGenerationText(provider, response.body()));
            writeRunLog(provider, insightOid, requestId, startedAt, new Date(), durationMs, true, null, null);
            return recommendation;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            long durationMs = (System.nanoTime() - startNanos) / 1_000_000L;
            writeRunLog(provider, insightOid, requestId, startedAt, new Date(), durationMs, false, "INTERRUPTED", "LLM request was interrupted");
            throw new ServiceException("LLM request was interrupted");
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            long durationMs = (System.nanoTime() - startNanos) / 1_000_000L;
            writeRunLog(provider, insightOid, requestId, startedAt, new Date(), durationMs, false, "REQUEST_ERROR", safeMessage(ex));
            throw new ServiceException("Unable to generate LLM recommendation: " + safeMessage(ex));
        }
    }

    @Override
    @ServiceMethodAuthority(type = {ServiceMethodType.INSERT, ServiceMethodType.UPDATE})
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    public MdActionItem createActionFromRecommendation(Map<String, Object> request) throws ServiceException {
        String recommendationOid = stringParam(request, "recommendationOid");
        String planOid = stringParam(request, "planOid");
        if (StringUtils.isAnyBlank(recommendationOid, planOid)) {
            throw new ServiceException("Recommendation OID and action plan OID are required");
        }
        MdInsightRecommendation recommendation = loadRecommendation(recommendationOid);
        if ("Y".equalsIgnoreCase(recommendation.getActionCreatedFlag())) {
            throw new ServiceException("Action has already been created for this recommendation");
        }
        MdInsight insight = loadInsight(recommendation.getInsightOid());
        MdActionItem item = new MdActionItem();
        item.setPlanOid(planOid);
        item.setItemName(StringUtils.abbreviate(StringUtils.defaultIfBlank(recommendation.getTitle(), insight.getTitle()), 300));
        item.setActionStage("DO");
        item.setDescription(StringUtils.abbreviate(buildActionDescription(insight, recommendation), 2000));
        item.setProgressValue(BigDecimal.ZERO);
        item.setStatus("ACTIVE");
        item.setSortNo(recommendation.getPriorityNo() == null ? 0 : recommendation.getPriorityNo());
        MdActionItem saved = actionItemService.insert(item).getValueEmptyThrowMessage();
        createInsightSourceLink(saved.getOid(), insight.getOid());
        recommendation.setActionCreatedFlag("Y");
        if (!"COMPLETED".equalsIgnoreCase(recommendation.getStatus())) {
            recommendation.setStatus("ACCEPTED");
            recommendation.setAcceptedFlag("Y");
        }
        recommendationService.update(recommendation);
        return saved;
    }
    private HttpRequest buildGenerationRequest(MdLlmProviderConfig provider, String apiKey, String prompt) throws ServiceException {
        String providerType = normalizeType(provider.getProviderType());
        try {
            if (OPENAI.equals(providerType)) {
                URI uri = URI.create(stripTrailingSlash(StringUtils.defaultIfBlank(provider.getApiBaseUrl(), defaultBaseUrl(providerType))) + "/chat/completions");
                String body = toJson(Map.of(
                        "model", provider.getDefaultModel(),
                        "temperature", 0.2,
                        "messages", List.of(
                                Map.of("role", "system", "content", "Return only compact JSON with keys recommendationType, title, contentText, priorityNo."),
                                Map.of("role", "user", "content", prompt))));
                return HttpRequest.newBuilder(uri).timeout(REQUEST_TIMEOUT).POST(HttpRequest.BodyPublishers.ofString(body))
                        .header("Content-Type", "application/json").header("Accept", "application/json")
                        .header("Authorization", "Bearer " + apiKey).build();
            }
            URI uri = URI.create(stripTrailingSlash(StringUtils.defaultIfBlank(provider.getApiBaseUrl(), defaultBaseUrl(providerType)))
                    + "/models/" + provider.getDefaultModel() + ":generateContent");
            String body = toJson(Map.of("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))));
            return HttpRequest.newBuilder(uri).timeout(REQUEST_TIMEOUT).POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json").header("Accept", "application/json")
                    .header("x-goog-api-key", apiKey).build();
        } catch (IllegalArgumentException ex) {
            throw new ServiceException("Invalid LLM provider API base URL");
        }
    }

    private MdInsightRecommendation saveLlmRecommendation(MdInsight insight, String text) throws ServiceException {
        Map<String, Object> payload = parseRecommendationJson(text);
        MdInsightRecommendation recommendation = new MdInsightRecommendation();
        recommendation.setTenantOid(StringUtils.defaultIfBlank(insight.getTenantOid(), "DEFAULT"));
        recommendation.setInsightOid(insight.getOid());
        recommendation.setRecommendationType(StringUtils.defaultIfBlank(stringValue(payload, "recommendationType"), "NEXT_STEP").toUpperCase(Locale.ROOT));
        recommendation.setTitle(StringUtils.abbreviate(StringUtils.defaultIfBlank(stringValue(payload, "title"), "LLM recommendation for " + insight.getInsightNo()), 200));
        recommendation.setContentText(StringUtils.abbreviate(StringUtils.defaultIfBlank(stringValue(payload, "contentText"), text), 4000));
        recommendation.setPriorityNo(intValue(payload, "priorityNo", 50));
        recommendation.setStatus(STATUS_OPEN);
        recommendation.setAcceptedFlag("N");
        recommendation.setActionCreatedFlag("N");
        recommendation.setIsDeleted(0);
        return recommendationService.insert(recommendation).getValueEmptyThrowMessage();
    }

    private String extractGenerationText(MdLlmProviderConfig provider, String responseBody) throws ServiceException {
        Map<String, Object> response = parseJson(responseBody, "LLM response");
        if (OPENAI.equals(normalizeType(provider.getProviderType()))) {
            Object choices = response.get("choices");
            if (choices instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof Map<?, ?> choice) {
                Object message = choice.get("message");
                if (message instanceof Map<?, ?> msg && msg.get("content") != null) return String.valueOf(msg.get("content"));
            }
        } else {
            Object candidates = response.get("candidates");
            if (candidates instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof Map<?, ?> candidate) {
                Object content = candidate.get("content");
                if (content instanceof Map<?, ?> contentMap && contentMap.get("parts") instanceof List<?> parts
                        && !parts.isEmpty() && parts.get(0) instanceof Map<?, ?> part && part.get("text") != null) {
                    return String.valueOf(part.get("text"));
                }
            }
        }
        throw new ServiceException("LLM response did not contain generated text");
    }
    private MdLlmProviderConfig loadProvider(String providerOid) throws ServiceException {
        if (StringUtils.isNotBlank(providerOid)) {
            MdLlmProviderConfig key = new MdLlmProviderConfig();
            key.setOid(providerOid);
            MdLlmProviderConfig provider = providerService.selectByEntityPrimaryKey(key).getValue();
            if (provider == null) throw new ServiceException("LLM provider not found");
            return provider;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("enabledFlag", "Y");
        params.put("defaultFlag", "Y");
        params.put("orderBy", "PROVIDER_CODE");
        params.put("sortType", "ASC");
        List<MdLlmProviderConfig> providers = providerService.selectListByParams(params).getValue();
        if (providers == null || providers.isEmpty()) {
            params.remove("defaultFlag");
            providers = providerService.selectListByParams(params).getValue();
        }
        if (providers == null || providers.isEmpty()) throw new ServiceException("No enabled LLM provider is configured");
        return providers.get(0);
    }

    private String decryptApiKey(MdLlmProviderConfig provider) throws ServiceException {
        if (StringUtils.isBlank(encryptionKey)) throw new ServiceException("MINDSCORE_LLM_ENCRYPTION_KEY is not configured");
        try {
            EncryptorUtils.validateGcmKey(encryptionKey);
            return EncryptorUtils.decryptGcm(encryptionKey, provider.getApiKeyEncrypted());
        } catch (RuntimeException ex) {
            throw new ServiceException("Unable to decrypt the provider API key");
        }
    }

    private MdInsight loadInsight(String oid) throws ServiceException {
        if (StringUtils.isBlank(oid)) throw new ServiceException("Insight OID is required");
        MdInsight key = new MdInsight();
        key.setOid(oid);
        MdInsight insight = insightService.selectByEntityPrimaryKey(key).getValue();
        if (insight == null) throw new ServiceException("Insight not found");
        return insight;
    }

    private MdInsightRecommendation loadRecommendation(String oid) throws ServiceException {
        MdInsightRecommendation key = new MdInsightRecommendation();
        key.setOid(oid);
        MdInsightRecommendation recommendation = recommendationService.selectByEntityPrimaryKey(key).getValue();
        if (recommendation == null) throw new ServiceException("Recommendation not found");
        return recommendation;
    }

    private List<MdInsightEvidence> loadEvidence(String insightOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("insightOid", insightOid);
        params.put("orderBy", "SORT_NO");
        params.put("sortType", "ASC");
        List<MdInsightEvidence> evidence = evidenceService.selectListByParams(params).getValue();
        return evidence == null ? List.of() : evidence;
    }

    private void createInsightSourceLink(String actionOid, String insightOid) throws ServiceException {
        MdActionSourceLink link = new MdActionSourceLink();
        link.setActionType("ITEM");
        link.setActionOid(actionOid);
        link.setSourceType("INSIGHT");
        link.setSourceOid(insightOid);
        link.setLinkReason("Created from insight recommendation");
        actionSourceLinkService.insert(link);
    }

    private String buildPrompt(MdInsight insight, List<MdInsightEvidence> evidenceList, String promptHint) {
        StringBuilder sb = new StringBuilder();
        sb.append("Create one practical business recommendation for this performance insight.\n");
        sb.append("Insight No: ").append(insight.getInsightNo()).append('\n');
        sb.append("Title: ").append(insight.getTitle()).append('\n');
        sb.append("Type: ").append(insight.getInsightType()).append('\n');
        sb.append("Severity: ").append(insight.getSeverity()).append('\n');
        sb.append("Summary: ").append(insight.getSummaryText()).append('\n');
        sb.append("Evidence:\n");
        for (MdInsightEvidence evidence : evidenceList) {
            sb.append("- ").append(evidence.getLabel()).append(": ")
              .append(StringUtils.defaultIfBlank(evidence.getValueText(), evidence.getValueNo() == null ? "" : evidence.getValueNo().toPlainString()))
              .append('\n');
        }
        if (StringUtils.isNotBlank(promptHint)) sb.append("Additional instruction: ").append(promptHint).append('\n');
        sb.append("Return JSON only: {\"recommendationType\":\"NEXT_STEP\",\"title\":\"...\",\"contentText\":\"...\",\"priorityNo\":50}");
        return sb.toString();
    }

    private String buildActionDescription(MdInsight insight, MdInsightRecommendation recommendation) {
        return "Created from insight " + insight.getInsightNo() + "\n\nInsight: " + StringUtils.defaultString(insight.getTitle())
                + "\n\n" + StringUtils.defaultString(recommendation.getContentText());
    }
    private void writeRunLog(MdLlmProviderConfig provider, String insightOid, String requestId, Date startedAt, Date finishedAt,
            long durationMs, boolean success, String errorCode, String errorMessage) throws ServiceException {
        MdLlmRunLog log = new MdLlmRunLog();
        log.setProviderOid(provider.getOid());
        log.setProviderType(provider.getProviderType());
        log.setModelName(provider.getDefaultModel());
        log.setRequestType("RECOMMENDATION");
        log.setRefType("INSIGHT");
        log.setRefOid(insightOid);
        log.setRequestId(requestId);
        log.setStatus(success ? "SUCCESS" : "FAILED");
        log.setStartedAt(startedAt);
        log.setFinishedAt(finishedAt);
        log.setDurationMs(durationMs);
        log.setErrorCode(errorCode);
        log.setErrorMessage(errorMessage);
        runLogService.insert(log);
    }

    private Map<String, Object> parseRecommendationJson(String text) {
        String value = StringUtils.defaultString(text).trim();
        int start = value.indexOf('{');
        int end = value.lastIndexOf('}');
        if (start >= 0 && end > start) {
            try {
                return LoadResources.getObjectMapper().readValue(value.substring(start, end + 1), new TypeReference<Map<String, Object>>() {});
            } catch (Exception ex) {
                return Map.of("contentText", text);
            }
        }
        return Map.of("contentText", text);
    }

    private Map<String, Object> parseJson(String json, String name) throws ServiceException {
        try {
            return LoadResources.getObjectMapper().readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            throw new ServiceException("Invalid " + name + " JSON");
        }
    }

    private String toJson(Object value) throws ServiceException {
        try {
            return LoadResources.getObjectMapper().writeValueAsString(value);
        } catch (Exception ex) {
            throw new ServiceException("Unable to build LLM request JSON");
        }
    }

    private String normalizeType(String value) throws ServiceException {
        String type = StringUtils.defaultString(value).trim().toUpperCase(Locale.ROOT);
        if (!OPENAI.equals(type) && !GEMINI.equals(type)) throw new ServiceException("Provider type must be OPENAI or GEMINI");
        return type;
    }

    private String defaultBaseUrl(String providerType) {
        return OPENAI.equals(providerType) ? "https://api.openai.com/v1" : "https://generativelanguage.googleapis.com/v1beta";
    }

    private String stripTrailingSlash(String value) { return value.replaceAll("/+$", ""); }
    private String stringParam(Map<String, Object> request, String key) {
        return request == null || request.get(key) == null ? null : StringUtils.trimToNull(String.valueOf(request.get(key)));
    }
    private String stringValue(Map<String, Object> map, String key) {
        return map == null || map.get(key) == null ? null : StringUtils.trimToNull(String.valueOf(map.get(key)));
    }
    private int intValue(Map<String, Object> map, String key, int defaultValue) {
        try {
            String value = stringValue(map, key);
            return StringUtils.isBlank(value) ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
    private String safeMessage(Exception ex) { return StringUtils.abbreviate(StringUtils.defaultIfBlank(ex.getMessage(), ex.getClass().getSimpleName()), 2000); }
}