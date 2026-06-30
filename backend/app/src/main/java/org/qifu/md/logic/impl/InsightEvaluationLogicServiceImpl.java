package org.qifu.md.logic.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.ServiceAuthority;
import org.qifu.base.model.ServiceMethodAuthority;
import org.qifu.base.model.ServiceMethodType;
import org.qifu.md.entity.MdInsight;
import org.qifu.md.entity.MdInsightEvidence;
import org.qifu.md.entity.MdInsightRecommendation;
import org.qifu.md.entity.MdInterpretationRule;
import org.qifu.md.entity.MdPerformanceSignal;
import org.qifu.md.logic.IInsightEvaluationLogicService;
import org.qifu.md.model.InsightEvaluationResult;
import org.qifu.md.service.IMdInsightService;
import org.qifu.md.service.IMdInsightEvidenceService;
import org.qifu.md.service.IMdInsightRecommendationService;
import org.qifu.md.service.IMdInterpretationRuleService;
import org.qifu.md.service.IMdPerformanceSignalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.qifu.util.LoadResources;
import tools.jackson.core.type.TypeReference;

@Service
@ServiceAuthority(check = true)
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class InsightEvaluationLogicServiceImpl implements IInsightEvaluationLogicService {
    private static final String TENANT_DEFAULT = "DEFAULT";
    private static final String STATUS_OPEN = "OPEN";
    private static final String GENERATED_BY_RULE = "RULE";

    private final IMdPerformanceSignalService<MdPerformanceSignal, String> signalService;
    private final IMdInterpretationRuleService<MdInterpretationRule, String> ruleService;
    private final IMdInsightService<MdInsight, String> insightService;
    private final IMdInsightEvidenceService<MdInsightEvidence, String> evidenceService;
    private final IMdInsightRecommendationService<MdInsightRecommendation, String> recommendationService;
    public InsightEvaluationLogicServiceImpl(
            IMdPerformanceSignalService<MdPerformanceSignal, String> signalService,
            IMdInterpretationRuleService<MdInterpretationRule, String> ruleService,
            IMdInsightService<MdInsight, String> insightService,
            IMdInsightEvidenceService<MdInsightEvidence, String> evidenceService,
            IMdInsightRecommendationService<MdInsightRecommendation, String> recommendationService) {
        this.signalService = signalService;
        this.ruleService = ruleService;
        this.insightService = insightService;
        this.evidenceService = evidenceService;
        this.recommendationService = recommendationService;
    }

    @Override
    @ServiceMethodAuthority(type = { ServiceMethodType.INSERT, ServiceMethodType.UPDATE })
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = { RuntimeException.class, Exception.class })
    public DefaultResult<InsightEvaluationResult> evaluate(Map<String, Object> params) throws ServiceException {
        Map<String, Object> signalParams = buildSignalParams(params);
        Map<String, Object> ruleParams = buildRuleParams(params);
        List<MdPerformanceSignal> signals = signalService.selectListByParams(signalParams).getValue();
        List<MdInterpretationRule> rules = ruleService.selectListByParams(ruleParams).getValue();

        InsightEvaluationResult summary = new InsightEvaluationResult();
        summary.setSignalCount(signals == null ? 0 : signals.size());
        summary.setRuleCount(rules == null ? 0 : rules.size());

        if (signals != null && rules != null) {
            for (MdPerformanceSignal signal : signals) {
                for (MdInterpretationRule rule : rules) {
                    if (!matchesSource(signal, rule) || !matchesCondition(signal, rule)) {
                        continue;
                    }
                    summary.setMatchedCount(summary.getMatchedCount() + 1);
                    MdInsight existing = loadExisting(signal, rule);
                    MdInsight insight = existing == null ? new MdInsight() : existing;
                    fillInsight(insight, signal, rule);
                    if (existing == null) {
                        insightService.insert(insight);
                        summary.setInsertedCount(summary.getInsertedCount() + 1);
                    } else {
                        insightService.update(insight);
                        summary.setUpdatedCount(summary.getUpdatedCount() + 1);
                    }
                    persistInsightDetails(insight, signal, rule);
                }
            }
        }

        DefaultResult<InsightEvaluationResult> result = new DefaultResult<>();
        result.setValue(summary);
        result.setMessage("Insight evaluation completed");
        return result;
    }

    private Map<String, Object> buildSignalParams(Map<String, Object> params) {
        Map<String, Object> signalParams = new HashMap<>();
        copy(params, signalParams, "signalType");
        copy(params, signalParams, "sourceType");
        copy(params, signalParams, "periodType");
        copy(params, signalParams, "periodKey");
        copy(params, signalParams, "ownerAccount");
        copy(params, signalParams, "orgOid");
        copy(params, signalParams, "statusCode");
        copy(params, signalParams, "riskLevel");
        copy(params, signalParams, "snapshotOid");
        signalParams.put("signalStatus", stringParam(params, "signalStatus", STATUS_OPEN));
        signalParams.put("orderBy", "GENERATED_AT");
        signalParams.put("sortType", "DESC");
        return signalParams;
    }

    private Map<String, Object> buildRuleParams(Map<String, Object> params) {
        Map<String, Object> ruleParams = new HashMap<>();
        String sourceType = stringParam(params, "sourceType", null);
        if (StringUtils.isNotBlank(sourceType)) {
            ruleParams.put("sourceType", sourceType);
        }
        ruleParams.put("enabledFlag", "Y");
        ruleParams.put("orderBy", "PRIORITY_NO, RULE_CODE");
        ruleParams.put("sortType", "ASC");
        return ruleParams;
    }

    private void fillInsight(MdInsight insight, MdPerformanceSignal signal, MdInterpretationRule rule) throws ServiceException {
        Map<String, Object> action = parseJson(rule.getActionExpr(), "actionExpr", false);
        Date now = new Date();
        insight.setTenantOid(StringUtils.defaultIfBlank(rule.getTenantOid(), TENANT_DEFAULT));
        insight.setInsightNo(insightNo(signal, rule));
        insight.setInsightType(stringValue(action, "insightType",
                "RECOMMENDATION".equalsIgnoreCase(rule.getRuleType()) ? "RECOMMENDATION" : "PERFORMANCE_RISK"));
        insight.setSeverity(stringValue(action, "severity", StringUtils.defaultIfBlank(rule.getSeverity(), signal.getRiskLevel())));
        insight.setSourceType(signal.getSourceType());
        insight.setSourceOid(signal.getSourceOid());
        insight.setSignalOid(signal.getOid());
        insight.setRuleOid(rule.getOid());
        insight.setTitle(applyTemplate(stringValue(action, "titleTemplate", defaultTitle(signal, rule)), signal, rule));
        insight.setSummaryText(applyTemplate(stringValue(action, "summaryTemplate", defaultSummary(signal, rule)), signal, rule));
        insight.setStatus(stringValue(action, "status", STATUS_OPEN));
        insight.setOwnerAccount(signal.getOwnerAccount());
        insight.setGeneratedByType(GENERATED_BY_RULE);
        insight.setGeneratedAt(now);
        insight.setIsDeleted(0);
    }

    private void persistInsightDetails(MdInsight insight, MdPerformanceSignal signal, MdInterpretationRule rule) throws ServiceException {
        if (insight == null || StringUtils.isBlank(insight.getOid())) {
            return;
        }
        createEvidenceIfMissing(insight, signal, rule);
        createRecommendationIfMissing(insight, signal, rule);
    }

    private void createEvidenceIfMissing(MdInsight insight, MdPerformanceSignal signal, MdInterpretationRule rule) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("insightOid", insight.getOid());
        List<MdInsightEvidence> existing = evidenceService.selectListByParams(params).getValue();
        if (existing != null && !existing.isEmpty()) {
            return;
        }
        int sortNo = 10;
        saveEvidence(insight, "SIGNAL", signal.getSourceType(), signal.getSourceOid(), "Source", sourceLabel(signal), null, null, sortNo);
        sortNo += 10;
        saveEvidence(insight, "SIGNAL", signal.getSourceType(), signal.getSourceOid(), "Signal type", signal.getSignalType(), null, null, sortNo);
        sortNo += 10;
        saveEvidence(insight, "SIGNAL", signal.getSourceType(), signal.getSourceOid(), "Status", signal.getStatusCode(), null, null, sortNo);
        sortNo += 10;
        saveEvidence(insight, "SIGNAL", signal.getSourceType(), signal.getSourceOid(), "Risk level", signal.getRiskLevel(), null, null, sortNo);
        sortNo += 10;
        saveEvidence(insight, "METRIC", signal.getSourceType(), signal.getSourceOid(), "Score", null, signal.getScoreValue(), null, sortNo);
        sortNo += 10;
        saveEvidence(insight, "METRIC", signal.getSourceType(), signal.getSourceOid(), "Target", null, signal.getTargetValue(), null, sortNo);
        sortNo += 10;
        saveEvidence(insight, "METRIC", signal.getSourceType(), signal.getSourceOid(), "Actual", null, signal.getActualValue(), null, sortNo);
        sortNo += 10;
        saveEvidence(insight, "METRIC", signal.getSourceType(), signal.getSourceOid(), "Variance", null, signal.getVarianceValue(), null, sortNo);
        sortNo += 10;
        saveEvidence(insight, "METRIC", signal.getSourceType(), signal.getSourceOid(), "Variance rate", null, signal.getVarianceRate(), null, sortNo);
        sortNo += 10;
        saveEvidence(insight, "RULE", "RULE", rule.getOid(), "Rule", StringUtils.defaultIfBlank(rule.getRuleName(), rule.getRuleCode()), null, rule.getConditionExpr(), sortNo);
        sortNo += 10;
        if (StringUtils.isNotBlank(signal.getEvidenceJson())) {
            saveEvidence(insight, "JSON", signal.getSourceType(), signal.getSourceOid(), "Signal evidence JSON", null, null, signal.getEvidenceJson(), sortNo);
        }
    }

    private void saveEvidence(MdInsight insight, String evidenceType, String sourceType, String sourceOid,
            String label, String valueText, BigDecimal valueNo, String evidenceJson, int sortNo) throws ServiceException {
        if (StringUtils.isBlank(valueText) && valueNo == null && StringUtils.isBlank(evidenceJson)) {
            return;
        }
        MdInsightEvidence evidence = new MdInsightEvidence();
        evidence.setTenantOid(StringUtils.defaultIfBlank(insight.getTenantOid(), TENANT_DEFAULT));
        evidence.setInsightOid(insight.getOid());
        evidence.setEvidenceType(evidenceType);
        evidence.setSourceType(sourceType);
        evidence.setSourceOid(sourceOid);
        evidence.setLabel(label);
        evidence.setValueText(valueText);
        evidence.setValueNo(valueNo);
        evidence.setEvidenceJson(evidenceJson);
        evidence.setSortNo(sortNo);
        evidence.setIsDeleted(0);
        evidenceService.insert(evidence);
    }

    @SuppressWarnings("unchecked")
    private void createRecommendationIfMissing(MdInsight insight, MdPerformanceSignal signal, MdInterpretationRule rule) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("insightOid", insight.getOid());
        List<MdInsightRecommendation> existing = recommendationService.selectListByParams(params).getValue();
        if (existing != null && !existing.isEmpty()) {
            return;
        }
        Map<String, Object> action = parseJson(rule.getActionExpr(), "actionExpr", false);
        Object recommendations = action.get("recommendations");
        if (recommendations instanceof List<?> list) {
            int priority = 10;
            for (Object item : list) {
                if (item instanceof Map<?, ?> recommendationMap) {
                    createRecommendation(insight, signal, rule, (Map<String, Object>) recommendationMap, priority);
                    priority += 10;
                }
            }
            return;
        }
        if (StringUtils.isNotBlank(stringValue(action, "recommendationTitleTemplate", null))
                || StringUtils.isNotBlank(stringValue(action, "recommendationContentTemplate", null))) {
            createRecommendation(insight, signal, rule, action, 10);
        }
    }

    private void createRecommendation(MdInsight insight, MdPerformanceSignal signal, MdInterpretationRule rule,
            Map<String, Object> action, int defaultPriority) throws ServiceException {
        MdInsightRecommendation recommendation = new MdInsightRecommendation();
        recommendation.setTenantOid(StringUtils.defaultIfBlank(insight.getTenantOid(), TENANT_DEFAULT));
        recommendation.setInsightOid(insight.getOid());
        recommendation.setRecommendationType(stringValue(action, "recommendationType", "NEXT_STEP").toUpperCase(Locale.ROOT));
        recommendation.setTitle(applyTemplate(stringValue(action, "recommendationTitleTemplate", defaultRecommendationTitle(signal, rule)), signal, rule));
        recommendation.setContentText(applyTemplate(stringValue(action, "recommendationContentTemplate", defaultRecommendationContent(signal, rule)), signal, rule));
        recommendation.setPriorityNo(intValue(action, "recommendationPriorityNo", defaultPriority));
        recommendation.setStatus(STATUS_OPEN);
        recommendation.setAcceptedFlag("N");
        recommendation.setActionCreatedFlag("N");
        recommendation.setIsDeleted(0);
        recommendationService.insert(recommendation);
    }

    private String sourceLabel(MdPerformanceSignal signal) {
        String code = StringUtils.defaultString(signal.getSourceCode());
        String name = StringUtils.defaultString(signal.getSourceName());
        return StringUtils.isBlank(code) ? name : code + " " + name;
    }

    private String defaultRecommendationTitle(MdPerformanceSignal signal, MdInterpretationRule rule) {
        return "Review " + StringUtils.defaultIfBlank(signal.getSourceName(), signal.getSourceCode());
    }

    private String defaultRecommendationContent(MdPerformanceSignal signal, MdInterpretationRule rule) {
        return "Review the matched signal and decide the next action for {sourceName}.";
    }
    private MdInsight loadExisting(MdPerformanceSignal signal, MdInterpretationRule rule) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("signalOid", signal.getOid());
        params.put("ruleOid", rule.getOid());
        params.put("status", STATUS_OPEN);
        params.put("generatedByType", GENERATED_BY_RULE);
        List<MdInsight> existing = insightService.selectListByParams(params).getValue();
        return existing == null || existing.isEmpty() ? null : existing.get(0);
    }

    private boolean matchesSource(MdPerformanceSignal signal, MdInterpretationRule rule) {
        String ruleSourceType = StringUtils.defaultString(rule.getSourceType()).trim();
        return StringUtils.isBlank(ruleSourceType)
                || "ALL".equalsIgnoreCase(ruleSourceType)
                || ruleSourceType.equalsIgnoreCase(signal.getSourceType());
    }

    private boolean matchesCondition(MdPerformanceSignal signal, MdInterpretationRule rule) throws ServiceException {
        Map<String, Object> condition = parseJson(rule.getConditionExpr(), "conditionExpr", true);
        return equalsIfPresent(condition, "signalType", signal.getSignalType())
                && equalsIfPresent(condition, "sourceType", signal.getSourceType())
                && equalsIfPresent(condition, "statusCode", signal.getStatusCode())
                && equalsIfPresent(condition, "riskLevel", signal.getRiskLevel())
                && equalsIfPresent(condition, "trendCode", signal.getTrendCode())
                && equalsIfPresent(condition, "signalStatus", signal.getSignalStatus())
                && numberIfPresent(condition, "scoreValueGte", signal.getScoreValue(), 0)
                && numberIfPresent(condition, "scoreValueGt", signal.getScoreValue(), 1)
                && numberIfPresent(condition, "scoreValueLte", signal.getScoreValue(), 2)
                && numberIfPresent(condition, "scoreValueLt", signal.getScoreValue(), 3)
                && numberIfPresent(condition, "varianceRateGte", signal.getVarianceRate(), 0)
                && numberIfPresent(condition, "varianceRateGt", signal.getVarianceRate(), 1)
                && numberIfPresent(condition, "varianceRateLte", signal.getVarianceRate(), 2)
                && numberIfPresent(condition, "varianceRateLt", signal.getVarianceRate(), 3)
                && numberIfPresent(condition, "varianceValueGte", signal.getVarianceValue(), 0)
                && numberIfPresent(condition, "varianceValueGt", signal.getVarianceValue(), 1)
                && numberIfPresent(condition, "varianceValueLte", signal.getVarianceValue(), 2)
                && numberIfPresent(condition, "varianceValueLt", signal.getVarianceValue(), 3);
    }

    private Map<String, Object> parseJson(String json, String fieldName, boolean required) throws ServiceException {
        if (StringUtils.isBlank(json)) {
            if (required) {
                throw new ServiceException("Rule " + fieldName + " is required");
            }
            return Map.of();
        }
        try {
            return LoadResources.getObjectMapper().readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            throw new ServiceException("Invalid rule " + fieldName + " JSON");
        }
    }

    private boolean equalsIfPresent(Map<String, Object> condition, String key, String actual) {
        if (!condition.containsKey(key) || condition.get(key) == null || StringUtils.isBlank(String.valueOf(condition.get(key)))) {
            return true;
        }
        return StringUtils.defaultString(actual).equalsIgnoreCase(String.valueOf(condition.get(key)).trim());
    }

    private boolean numberIfPresent(Map<String, Object> condition, String key, BigDecimal actual, int operator) throws ServiceException {
        if (!condition.containsKey(key) || condition.get(key) == null || StringUtils.isBlank(String.valueOf(condition.get(key)))) {
            return true;
        }
        if (actual == null) {
            return false;
        }
        BigDecimal expected;
        try {
            expected = new BigDecimal(String.valueOf(condition.get(key)));
        } catch (NumberFormatException ex) {
            throw new ServiceException("Invalid numeric condition: " + key);
        }
        int compare = actual.compareTo(expected);
        return switch (operator) {
            case 0 -> compare >= 0;
            case 1 -> compare > 0;
            case 2 -> compare <= 0;
            case 3 -> compare < 0;
            default -> false;
        };
    }

    private String applyTemplate(String template, MdPerformanceSignal signal, MdInterpretationRule rule) {
        return StringUtils.defaultString(template)
                .replace("{sourceType}", StringUtils.defaultString(signal.getSourceType()))
                .replace("{sourceCode}", StringUtils.defaultString(signal.getSourceCode()))
                .replace("{sourceName}", StringUtils.defaultString(signal.getSourceName()))
                .replace("{signalType}", StringUtils.defaultString(signal.getSignalType()))
                .replace("{statusCode}", StringUtils.defaultString(signal.getStatusCode()))
                .replace("{riskLevel}", StringUtils.defaultString(signal.getRiskLevel()))
                .replace("{periodType}", StringUtils.defaultString(signal.getPeriodType()))
                .replace("{periodKey}", StringUtils.defaultString(signal.getPeriodKey()))
                .replace("{ownerAccount}", StringUtils.defaultString(signal.getOwnerAccount()))
                .replace("{scoreValue}", value(signal.getScoreValue()))
                .replace("{targetValue}", value(signal.getTargetValue()))
                .replace("{actualValue}", value(signal.getActualValue()))
                .replace("{varianceValue}", value(signal.getVarianceValue()))
                .replace("{varianceRate}", value(signal.getVarianceRate()))
                .replace("{trendCode}", StringUtils.defaultString(signal.getTrendCode()))
                .replace("{ruleName}", StringUtils.defaultString(rule.getRuleName()));
    }

    private String defaultTitle(MdPerformanceSignal signal, MdInterpretationRule rule) {
        return "{sourceName} matched " + StringUtils.defaultIfBlank(rule.getRuleName(), rule.getRuleCode());
    }

    private String defaultSummary(MdPerformanceSignal signal, MdInterpretationRule rule) {
        return "{sourceType} {sourceCode} has signal {signalType}, status {statusCode}, risk {riskLevel}.";
    }

    private String insightNo(MdPerformanceSignal signal, MdInterpretationRule rule) {
        String signalPart = StringUtils.left(StringUtils.defaultString(signal.getOid()).replace("-", ""), 12);
        String rulePart = StringUtils.left(StringUtils.defaultString(rule.getRuleCode()).replaceAll("[^A-Za-z0-9]", ""), 24);
        return ("INS-" + signalPart + "-" + rulePart).toUpperCase(Locale.ROOT);
    }

    private String stringValue(Map<String, Object> map, String key, String defaultValue) {
        if (!map.containsKey(key) || map.get(key) == null || StringUtils.isBlank(String.valueOf(map.get(key)))) {
            return defaultValue;
        }
        return String.valueOf(map.get(key)).trim();
    }

    private int intValue(Map<String, Object> map, String key, int defaultValue) {
        if (!map.containsKey(key) || map.get(key) == null || StringUtils.isBlank(String.valueOf(map.get(key)))) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(String.valueOf(map.get(key)).trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private String value(BigDecimal number) {
        return number == null ? "" : number.stripTrailingZeros().toPlainString();
    }

    private void copy(Map<String, Object> source, Map<String, Object> target, String key) {
        String value = stringParam(source, key, null);
        if (StringUtils.isNotBlank(value)) {
            target.put(key, value);
        }
    }

    private String stringParam(Map<String, Object> params, String key, String defaultValue) {
        if (params == null || params.get(key) == null || StringUtils.isBlank(String.valueOf(params.get(key)))) {
            return defaultValue;
        }
        return String.valueOf(params.get(key)).trim();
    }
}
