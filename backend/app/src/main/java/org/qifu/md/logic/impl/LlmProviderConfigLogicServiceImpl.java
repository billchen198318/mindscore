package org.qifu.md.logic.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.PageOf;
import org.qifu.base.model.QueryResult;
import org.qifu.md.entity.MdLlmProviderConfig;
import org.qifu.md.entity.MdLlmRunLog;
import org.qifu.md.logic.ILlmProviderConfigLogicService;
import org.qifu.md.model.LlmConnectionTestResult;
import org.qifu.md.model.LlmProviderConfigRequest;
import org.qifu.md.model.LlmProviderConfigView;
import org.qifu.md.service.IMdLlmProviderConfigService;
import org.qifu.md.service.IMdLlmRunLogService;
import org.qifu.util.EncryptorUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LlmProviderConfigLogicServiceImpl implements ILlmProviderConfigLogicService {
    private static final String OPENAI = "OPENAI";
    private static final String GEMINI = "GEMINI";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);

    private final IMdLlmProviderConfigService<MdLlmProviderConfig, String> providerService;
    private final IMdLlmRunLogService<MdLlmRunLog, String> runLogService;
    private final HttpClient httpClient;
    private final String encryptionKey;

    public LlmProviderConfigLogicServiceImpl(
            IMdLlmProviderConfigService<MdLlmProviderConfig, String> providerService,
            IMdLlmRunLogService<MdLlmRunLog, String> runLogService,
            @Value("${mindscore.llm.encryption-key:}") String encryptionKey) {
        this.providerService = providerService;
        this.runLogService = runLogService;
        this.encryptionKey = encryptionKey;
        this.httpClient = HttpClient.newBuilder().connectTimeout(REQUEST_TIMEOUT).build();
    }

    @Override
    public QueryResult<List<LlmProviderConfigView>> findProviderPage(Map<String, Object> params, PageOf pageOf) throws ServiceException {
        QueryResult<List<MdLlmProviderConfig>> source = providerService.findPage(params, pageOf);
        QueryResult<List<LlmProviderConfigView>> result = new QueryResult<>();
        result.setMessage(source.getMessage());
        result.setPageOf(source.getPageOf());
        if (source.getValue() != null) {
            result.setValue(source.getValue().stream().map(this::toView).toList());
        }
        return result;
    }

    @Override
    public QueryResult<List<MdLlmRunLog>> findRunLogPage(Map<String, Object> params, PageOf pageOf) throws ServiceException {
        return runLogService.findPage(params, pageOf);
    }

    @Override
    public LlmProviderConfigView load(String oid) throws ServiceException {
        return toView(loadEntity(oid));
    }

    @Override
    @Transactional
    public LlmProviderConfigView create(LlmProviderConfigRequest request) throws ServiceException {
        validate(request, true);
        MdLlmProviderConfig entity = new MdLlmProviderConfig();
        copyEditableFields(request, entity);
        setApiKey(entity, request.apiKey());
        DefaultResult<MdLlmProviderConfig> result = providerService.insert(entity);
        return toView(requireValue(result, "Unable to create LLM provider"));
    }

    @Override
    @Transactional
    public LlmProviderConfigView update(LlmProviderConfigRequest request) throws ServiceException {
        validate(request, false);
        MdLlmProviderConfig entity = loadEntity(request.oid());
        copyEditableFields(request, entity);
        if (StringUtils.isNotBlank(request.apiKey())) {
            setApiKey(entity, request.apiKey());
            entity.setConnectStatus(null);
            entity.setLastTestAt(null);
            entity.setLastErrorMessage(null);
        }
        DefaultResult<MdLlmProviderConfig> result = providerService.update(entity);
        return toView(requireValue(result, "Unable to update LLM provider"));
    }

    @Override
    @Transactional
    public boolean delete(String oid) throws ServiceException {
        MdLlmProviderConfig entity = loadEntity(oid);
        return Boolean.TRUE.equals(providerService.delete(entity).getValue());
    }

    @Override
    @Transactional
    public LlmConnectionTestResult testConnection(String oid) throws ServiceException {
        MdLlmProviderConfig provider = loadEntity(oid);
        requireEncryptionKey();
        String apiKey;
        try {
            apiKey = EncryptorUtils.decryptGcm(encryptionKey, provider.getApiKeyEncrypted());
        } catch (RuntimeException ex) {
            throw new ServiceException("Unable to decrypt the provider API key");
        }

        Date startedAt = new Date();
        long startedNanos = System.nanoTime();
        int statusCode = 0;
        String requestId = null;
        String errorMessage = null;
        boolean connected = false;
        try {
            HttpRequest request = buildConnectionRequest(provider, apiKey);
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            statusCode = response.statusCode();
            requestId = response.headers().firstValue("x-request-id").orElse(null);
            connected = statusCode >= 200 && statusCode < 300;
            if (!connected) {
                errorMessage = "Provider returned HTTP " + statusCode;
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            errorMessage = "Connection test was interrupted";
        } catch (Exception ex) {
            errorMessage = safeMessage(ex);
        }

        long durationMs = (System.nanoTime() - startedNanos) / 1_000_000L;
        Date finishedAt = new Date();
        provider.setConnectStatus(connected ? "SUCCESS" : "FAILED");
        provider.setLastTestAt(finishedAt);
        provider.setLastErrorMessage(errorMessage);
        providerService.update(provider);
        writeTestLog(provider, startedAt, finishedAt, durationMs, requestId, connected, statusCode, errorMessage);

        return new LlmConnectionTestResult(connected, statusCode, durationMs,
                connected ? "Connection successful" : errorMessage);
    }

    private HttpRequest buildConnectionRequest(MdLlmProviderConfig provider, String apiKey) throws ServiceException {
        String providerType = normalizeType(provider.getProviderType());
        String baseUrl = StringUtils.defaultIfBlank(provider.getApiBaseUrl(), defaultBaseUrl(providerType));
        URI uri;
        try {
            uri = URI.create(stripTrailingSlash(baseUrl) + "/models");
        } catch (IllegalArgumentException ex) {
            throw new ServiceException("Invalid API base URL");
        }
        if (!"https".equalsIgnoreCase(uri.getScheme()) && !"http".equalsIgnoreCase(uri.getScheme())) {
            throw new ServiceException("API base URL must use HTTP or HTTPS");
        }
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri).timeout(REQUEST_TIMEOUT).GET()
                .header("Accept", "application/json");
        if (OPENAI.equals(providerType)) {
            builder.header("Authorization", "Bearer " + apiKey);
        } else {
            builder.header("x-goog-api-key", apiKey);
        }
        return builder.build();
    }

    private void writeTestLog(MdLlmProviderConfig provider, Date startedAt, Date finishedAt, long durationMs,
            String requestId, boolean connected, int statusCode, String errorMessage) throws ServiceException {
        MdLlmRunLog log = new MdLlmRunLog();
        log.setProviderOid(provider.getOid());
        log.setProviderType(provider.getProviderType());
        log.setModelName(provider.getDefaultModel());
        log.setRequestType("TEST");
        log.setRequestId(requestId);
        log.setStatus(connected ? "SUCCESS" : "FAILED");
        log.setStartedAt(startedAt);
        log.setFinishedAt(finishedAt);
        log.setDurationMs(durationMs);
        if (!connected) {
            log.setErrorCode(statusCode > 0 ? "HTTP_" + statusCode : "CONNECTION_ERROR");
            log.setErrorMessage(errorMessage);
        }
        runLogService.insert(log);
    }

    private void copyEditableFields(LlmProviderConfigRequest request, MdLlmProviderConfig entity) throws ServiceException {
        entity.setProviderCode(request.providerCode().trim());
        entity.setProviderName(request.providerName().trim());
        entity.setProviderType(normalizeType(request.providerType()));
        entity.setApiBaseUrl(StringUtils.defaultIfBlank(request.apiBaseUrl(), defaultBaseUrl(entity.getProviderType())).trim());
        entity.setDefaultModel(request.defaultModel().trim());
        entity.setEnabledFlag(StringUtils.defaultIfBlank(request.enabledFlag(), "Y"));
        entity.setDefaultFlag(StringUtils.defaultIfBlank(request.defaultFlag(), "N"));
        entity.setConfigJson(StringUtils.trimToNull(request.configJson()));
    }

    private void setApiKey(MdLlmProviderConfig entity, String apiKey) throws ServiceException {
        requireEncryptionKey();
        try {
            entity.setApiKeyEncrypted(EncryptorUtils.encryptGcm(encryptionKey, apiKey));
            entity.setApiKeyMasked(maskApiKey(apiKey));
        } catch (RuntimeException ex) {
            throw new ServiceException("Unable to encrypt the provider API key");
        }
    }

    private void validate(LlmProviderConfigRequest request, boolean create) throws ServiceException {
        if (request == null || StringUtils.isBlank(request.providerCode()) || StringUtils.isBlank(request.providerName())
                || StringUtils.isBlank(request.providerType()) || StringUtils.isBlank(request.defaultModel())) {
            throw new ServiceException("Provider code, name, type, and default model are required");
        }
        if (create && StringUtils.isBlank(request.apiKey())) {
            throw new ServiceException("API key is required");
        }
        if (!create && StringUtils.isBlank(request.oid())) {
            throw new ServiceException("Provider OID is required");
        }
        normalizeType(request.providerType());
    }

    private String normalizeType(String value) throws ServiceException {
        String type = StringUtils.defaultString(value).trim().toUpperCase(Locale.ROOT);
        if (!OPENAI.equals(type) && !GEMINI.equals(type)) {
            throw new ServiceException("Provider type must be OPENAI or GEMINI");
        }
        return type;
    }

    private String defaultBaseUrl(String providerType) {
        return OPENAI.equals(providerType) ? "https://api.openai.com/v1"
                : "https://generativelanguage.googleapis.com/v1beta";
    }

    private MdLlmProviderConfig loadEntity(String oid) throws ServiceException {
        MdLlmProviderConfig key = new MdLlmProviderConfig();
        key.setOid(oid);
        return requireValue(providerService.selectByEntityPrimaryKey(key), "LLM provider not found");
    }

    private <T> T requireValue(DefaultResult<T> result, String message) throws ServiceException {
        if (result == null || result.getValue() == null) throw new ServiceException(message);
        return result.getValue();
    }

    private LlmProviderConfigView toView(MdLlmProviderConfig entity) {
        return new LlmProviderConfigView(entity.getOid(), entity.getProviderCode(), entity.getProviderName(),
                entity.getProviderType(), entity.getApiBaseUrl(), entity.getDefaultModel(), entity.getApiKeyMasked(),
                StringUtils.isNotBlank(entity.getApiKeyEncrypted()), entity.getEnabledFlag(), entity.getDefaultFlag(),
                entity.getConnectStatus(), entity.getLastTestAt(), entity.getLastErrorMessage(), entity.getConfigJson(),
                entity.getCdate(), entity.getUdate());
    }

    private void requireEncryptionKey() throws ServiceException {
        if (StringUtils.isBlank(encryptionKey)) {
            throw new ServiceException("MINDSCORE_LLM_ENCRYPTION_KEY is not configured");
        }
        try {
            EncryptorUtils.validateGcmKey(encryptionKey);
        } catch (IllegalArgumentException ex) {
            throw new ServiceException("MINDSCORE_LLM_ENCRYPTION_KEY must be a Base64-encoded AES key containing 16, 24, or 32 bytes");
        }
    }

    private String maskApiKey(String apiKey) {
        String value = StringUtils.defaultString(apiKey);
        if (value.length() <= 8) return "****";
        return value.substring(0, 4) + "****" + value.substring(value.length() - 4);
    }

    private String stripTrailingSlash(String value) { return value.replaceAll("/+$", ""); }
    private String safeMessage(Exception ex) {
        String message = StringUtils.defaultIfBlank(ex.getMessage(), ex.getClass().getSimpleName());
        return StringUtils.abbreviate(message, 2000);
    }
}
