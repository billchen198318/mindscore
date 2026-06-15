package org.qifu.md.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.qifu.base.model.YesNoKeyProvide;
import org.qifu.md.entity.MdFormula;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiMeasureData;
import org.qifu.md.entity.MdKpiScoreColor;
import org.qifu.md.model.AggregationContext;
import org.qifu.util.LoadResources;

public class KpiScoreCalculationUtils {

    public static final String STATUS_UNKNOWN = "UNKNOWN";
    public static final String COLOR_CUSTOM = "CUSTOM";
    public static final String COLOR_DEFAULT = "DEFAULT";

    protected KpiScoreCalculationUtils() {
        throw new IllegalStateException("Utils class: KpiScoreCalculationUtils");
    }

    public static CalculationResult calculate(MdKpi kpi, MdKpiMeasureData measureData, MdFormula formula,
            List<MdKpiScoreColor> kpiColorRules, List<MdKpiScoreColor> globalColorRules) {
        validate(kpi, measureData, formula);

        BigDecimal rawScore = calculateRawScore(kpi, measureData, formula);
        BigDecimal score = applyScoreCap(rawScore, kpi);
        MdKpiScoreColor colorRule = resolveColorRule(score, kpiColorRules, globalColorRules);
        String status = colorRule == null ? STATUS_UNKNOWN : StringUtils.defaultIfBlank(colorRule.getScoreStatus(), STATUS_UNKNOWN);

        CalculationResult result = new CalculationResult();
        result.setRawTarget(resolveTarget(kpi, measureData));
        result.setRawActual(measureData.getActualValue());
        result.setRawScore(rawScore);
        result.setScoreValue(score);
        result.setScoreStatus(status);
        result.setColorRule(colorRule);
        result.setCalculationTrace(buildTrace(kpi, measureData, formula, rawScore, score, colorRule, status));
        return result;
    }

    public static CalculationResult calculateAggregated(MdKpi kpi, List<MdKpiMeasureData> measureDataList, MdFormula formula,
            String aggrCode, String aggrExpression, List<MdKpiScoreColor> kpiColorRules, List<MdKpiScoreColor> globalColorRules) {
        validate(kpi, measureDataList, formula);

        List<BigDecimal> rawScores = calculateRawScores(kpi, measureDataList, formula);
        BigDecimal rawScore = aggregateRawScores(kpi, measureDataList, rawScores, aggrCode, aggrExpression);
        BigDecimal score = applyScoreCap(rawScore, kpi);
        MdKpiScoreColor colorRule = resolveColorRule(score, kpiColorRules, globalColorRules);
        String status = colorRule == null ? STATUS_UNKNOWN : StringUtils.defaultIfBlank(colorRule.getScoreStatus(), STATUS_UNKNOWN);
        MdKpiMeasureData latestMeasureData = measureDataList.get(measureDataList.size() - 1);

        CalculationResult result = new CalculationResult();
        result.setRawTarget(resolveTarget(kpi, latestMeasureData));
        result.setRawActual(latestMeasureData.getActualValue());
        result.setRawScore(rawScore);
        result.setScoreValue(score);
        result.setScoreStatus(status);
        result.setColorRule(colorRule);
        result.setCalculationTrace(buildAggregateTrace(kpi, measureDataList, formula, aggrCode, aggrExpression, rawScores, rawScore, score, colorRule, status));
        return result;
    }

    public static List<BigDecimal> calculateRawScores(MdKpi kpi, List<MdKpiMeasureData> measureDataList, MdFormula formula) {
        validate(kpi, measureDataList, formula);
        List<BigDecimal> scores = new ArrayList<>();
        for (MdKpiMeasureData measureData : measureDataList) {
            if (measureData != null) {
                scores.add(calculateRawScore(kpi, measureData, formula));
            }
        }
        return scores;
    }

    public static BigDecimal calculateRawScore(MdKpi kpi, MdKpiMeasureData measureData, MdFormula formula) {
        validate(kpi, measureData, formula);
        return toBigDecimal(FormulaUtils.execute(
                formula.getScriptType(),
                formula.getExpression(),
                FormulaUtils.getParameter(kpi, measureData)));
    }

    public static BigDecimal aggregateRawScores(MdKpi kpi, List<MdKpiMeasureData> measureDataList, List<BigDecimal> rawScores,
            String aggrCode, String aggrExpression) {
        AggregationContext context = new AggregationContext();
        context.setKpi(kpi);
        context.setMeasureDataList(measureDataList);
        context.setScores(rawScores);
        if (StringUtils.isNotBlank(aggrExpression)) {
            return AggregationMethodUtils.executeScript(aggrExpression, context);
        }
        return AggregationMethodUtils.executeBuiltin(aggrCode, context);
    }

    public static BigDecimal applyScoreCap(BigDecimal score, MdKpi kpi) {
        if (score == null) {
            return BigDecimal.ZERO;
        }
        return score;
    }

    public static MdKpiScoreColor resolveColorRule(BigDecimal score, List<MdKpiScoreColor> kpiColorRules,
            List<MdKpiScoreColor> globalColorRules) {
        MdKpiScoreColor rule = findCustomRule(score, kpiColorRules);
        if (rule != null) {
            return rule;
        }
        rule = findCustomRule(score, globalColorRules);
        if (rule != null) {
            return rule;
        }
        rule = findDefaultRule(kpiColorRules);
        return rule == null ? findDefaultRule(globalColorRules) : rule;
    }

    private static MdKpiScoreColor findCustomRule(BigDecimal score, List<MdKpiScoreColor> rules) {
        if (score == null || CollectionUtils.isEmpty(rules)) {
            return null;
        }
        for (MdKpiScoreColor rule : rules) {
            if (!isEnabled(rule) || !COLOR_CUSTOM.equals(rule.getColorType())) {
                continue;
            }
            if (rule.getScoreMin() == null || rule.getScoreMax() == null) {
                continue;
            }
            if (rule.getScoreMin().compareTo(score) <= 0 && rule.getScoreMax().compareTo(score) >= 0) {
                return rule;
            }
        }
        return null;
    }

    private static MdKpiScoreColor findDefaultRule(List<MdKpiScoreColor> rules) {
        if (CollectionUtils.isEmpty(rules)) {
            return null;
        }
        for (MdKpiScoreColor rule : rules) {
            if (isEnabled(rule) && COLOR_DEFAULT.equals(rule.getColorType())) {
                return rule;
            }
        }
        return null;
    }

    private static boolean isEnabled(MdKpiScoreColor rule) {
        return rule != null && YesNoKeyProvide.YES.equals(rule.getEnabled());
    }

    private static BigDecimal resolveTarget(MdKpi kpi, MdKpiMeasureData measureData) {
        if (measureData != null && measureData.getTargetValue() != null) {
            return measureData.getTargetValue();
        }
        return kpi == null ? null : kpi.getTargetValue();
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        return new BigDecimal(value.toString());
    }

    private static void validate(MdKpi kpi, MdKpiMeasureData measureData, MdFormula formula) {
        if (kpi == null) {
            throw new IllegalArgumentException("KPI cannot be null.");
        }
        if (measureData == null) {
            throw new IllegalArgumentException("KPI measure data cannot be null.");
        }
        if (formula == null) {
            throw new IllegalArgumentException("KPI formula cannot be null.");
        }
    }

    private static void validate(MdKpi kpi, List<MdKpiMeasureData> measureDataList, MdFormula formula) {
        if (kpi == null) {
            throw new IllegalArgumentException("KPI cannot be null.");
        }
        if (CollectionUtils.isEmpty(measureDataList)) {
            throw new IllegalArgumentException("KPI measure data list cannot be empty.");
        }
        if (formula == null) {
            throw new IllegalArgumentException("KPI formula cannot be null.");
        }
    }

    private static String buildTrace(MdKpi kpi, MdKpiMeasureData measureData, MdFormula formula,
            BigDecimal rawScore, BigDecimal score, MdKpiScoreColor colorRule, String status) {
        Map<String, Object> trace = new LinkedHashMap<>();
        trace.put("kpiOid", kpi.getOid());
        trace.put("kpiCode", kpi.getKpiCode());
        trace.put("periodType", measureData.getPeriodType());
        trace.put("periodKey", measureData.getPeriodKey());
        trace.put("dataForType", measureData.getDataForType());
        trace.put("account", measureData.getAccount());
        trace.put("orgOid", measureData.getOrgOid());
        trace.put("formulaOid", formula.getOid());
        trace.put("formulaCode", formula.getFormulaCode());
        trace.put("scriptType", formula.getScriptType());
        trace.put("expression", formula.getExpression());
        trace.put("rawTarget", resolveTarget(kpi, measureData));
        trace.put("rawActual", measureData.getActualValue());
        trace.put("rawScore", rawScore);
        trace.put("scoreCapMode", kpi.getScoreCapMode());
        trace.put("scoreValue", score);
        trace.put("scoreStatus", status);
        trace.put("colorRuleOid", colorRule == null ? null : colorRule.getOid());
        trace.put("colorCode", colorRule == null ? null : colorRule.getColorCode());
        return toJson(trace);
    }

    private static String buildAggregateTrace(MdKpi kpi, List<MdKpiMeasureData> measureDataList, MdFormula formula,
            String aggrCode, String aggrExpression, List<BigDecimal> rawScores, BigDecimal rawScore,
            BigDecimal score, MdKpiScoreColor colorRule, String status) {
        Map<String, Object> trace = new LinkedHashMap<>();
        trace.put("kpiOid", kpi.getOid());
        trace.put("kpiCode", kpi.getKpiCode());
        trace.put("measureCount", measureDataList.size());
        trace.put("formulaOid", formula.getOid());
        trace.put("formulaCode", formula.getFormulaCode());
        trace.put("scriptType", formula.getScriptType());
        trace.put("expression", formula.getExpression());
        trace.put("aggrCode", aggrCode);
        trace.put("aggrExpression", aggrExpression);
        trace.put("rawScores", rawScores);
        trace.put("rawScore", rawScore);
        trace.put("scoreCapMode", kpi.getScoreCapMode());
        trace.put("scoreValue", score);
        trace.put("scoreStatus", status);
        trace.put("colorRuleOid", colorRule == null ? null : colorRule.getOid());
        trace.put("colorCode", colorRule == null ? null : colorRule.getColorCode());
        return toJson(trace);
    }

    private static String toJson(Map<String, Object> trace) {
        try {
            return LoadResources.getObjectMapper().writeValueAsString(trace);
        } catch (Exception e) {
            throw new IllegalStateException("Build KPI calculation trace failed.", e);
        }
    }

    public static class CalculationResult implements java.io.Serializable {
        private static final long serialVersionUID = 1L;

        private BigDecimal rawTarget;
        private BigDecimal rawActual;
        private BigDecimal rawScore;
        private BigDecimal scoreValue;
        private String scoreStatus;
        private MdKpiScoreColor colorRule;
        private String calculationTrace;

        public BigDecimal getRawTarget() {
            return rawTarget;
        }

        public void setRawTarget(BigDecimal rawTarget) {
            this.rawTarget = rawTarget;
        }

        public BigDecimal getRawActual() {
            return rawActual;
        }

        public void setRawActual(BigDecimal rawActual) {
            this.rawActual = rawActual;
        }

        public BigDecimal getRawScore() {
            return rawScore;
        }

        public void setRawScore(BigDecimal rawScore) {
            this.rawScore = rawScore;
        }

        public BigDecimal getScoreValue() {
            return scoreValue;
        }

        public void setScoreValue(BigDecimal scoreValue) {
            this.scoreValue = scoreValue;
        }

        public String getScoreStatus() {
            return scoreStatus;
        }

        public void setScoreStatus(String scoreStatus) {
            this.scoreStatus = scoreStatus;
        }

        public MdKpiScoreColor getColorRule() {
            return colorRule;
        }

        public void setColorRule(MdKpiScoreColor colorRule) {
            this.colorRule = colorRule;
        }

        public String getCalculationTrace() {
            return calculationTrace;
        }

        public void setCalculationTrace(String calculationTrace) {
            this.calculationTrace = calculationTrace;
        }
    }
}
