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
import org.qifu.md.entity.MdInterpretationRule;
import org.qifu.md.entity.MdPerformanceSignal;
import org.qifu.md.logic.IInsightEvaluationLogicService;
import org.qifu.md.model.InsightEvaluationResult;
import org.qifu.md.service.IMdInsightService;
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
    public InsightEvaluationLogicServiceImpl(
            IMdPerformanceSignalService<MdPerformanceSignal, String> signalService,
            IMdInterpretationRuleService<MdInterpretationRule, String> ruleService,
            IMdInsightService<MdInsight, String> insightService) {
        this.signalService = signalService;
        this.ruleService = ruleService;
        this.insightService = insightService;
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
                .replace("{scoreValue}", value(signal.getScoreValue()))
                .replace("{varianceRate}", value(signal.getVarianceRate()))
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
