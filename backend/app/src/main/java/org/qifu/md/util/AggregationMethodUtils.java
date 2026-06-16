package org.qifu.md.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.qifu.base.model.ScriptTypeCode;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiMeasureData;
import org.qifu.md.model.AggregationContext;
import org.qifu.md.model.AggregationMethodTestRequest;
import org.qifu.util.ScriptExpressionUtils;

public class AggregationMethodUtils {

    private static final String METHOD_AVG = "AVG";
    private static final String METHOD_SUM = "SUM";
    private static final String METHOD_MAX = "MAX";
    private static final String METHOD_MIN = "MIN";
    private static final String METHOD_CNT = "CNT";
    private static final String METHOD_DISTINCT = "DISTINCT";
    private static final String METHOD_LATEST_ACTUAL = "LATEST_ACTUAL";
    private static final String METHOD_FIRST_ACTUAL = "FIRST_ACTUAL";
    private static final String METHOD_NON_NULL_CNT = "NON_NULL_CNT";
    private static final String METHOD_VALID_RATE = "VALID_RATE";
    private static final String METHOD_ACHIEVEMENT_RATE = "ACHIEVEMENT_RATE";
    private static final String METHOD_PASS_RATE = "PASS_RATE";

    protected AggregationMethodUtils() {
        throw new IllegalStateException("Utils class: AggregationMethodUtils");
    }

    public static BigDecimal test(AggregationMethodTestRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Aggregation test request cannot be null.");
        }
        AggregationContext context = toContext(request);
        if (StringUtils.isNotBlank(request.getMethodCode())) {
            return executeBuiltin(request.getMethodCode(), context);
        }
        return executeScript(request.getExpression(), context);
    }

    public static List<SupportedMethod> getSupportedMethods() {
        return Arrays.asList(
                new SupportedMethod(METHOD_SUM, "SUM", "scores == null ? 0 : scores.sum()", "Sum all score values."),
                new SupportedMethod(METHOD_AVG, "AVG", "scores == null || scores.isEmpty() ? 0 : scores.sum() / scores.size()", "Average all score values."),
                new SupportedMethod(METHOD_MAX, "MAX", "scores == null || scores.isEmpty() ? 0 : scores.max()", "Maximum score value."),
                new SupportedMethod(METHOD_MIN, "MIN", "scores == null || scores.isEmpty() ? 0 : scores.min()", "Minimum score value."),
                new SupportedMethod(METHOD_CNT, "Count", "scores == null ? 0 : scores.size()", "Count score values."),
                new SupportedMethod(METHOD_DISTINCT, "Distinct Count", "scores == null ? 0 : scores.collect { it.stripTrailingZeros() }.unique().size()", "Count distinct score values."),
                new SupportedMethod(METHOD_LATEST_ACTUAL, "Latest Actual", "measures == null || measures.isEmpty() ? 0 : measures.sort { it.measureDate == null ? new Date(0) : it.measureDate }.last().actualValue", "Latest measure actual value."),
                new SupportedMethod(METHOD_FIRST_ACTUAL, "First Actual", "measures == null || measures.isEmpty() ? 0 : measures.sort { it.measureDate == null ? new Date(0) : it.measureDate }.first().actualValue", "First measure actual value."),
                new SupportedMethod(METHOD_NON_NULL_CNT, "Non-null Count", "measures == null ? 0 : measures.count { it.actualValue != null }", "Count measure rows with actual value."),
                new SupportedMethod(METHOD_VALID_RATE, "Valid Rate", "measures == null || measures.isEmpty() ? 0 : measures.count { it.actualValue != null } * 100 / measures.size()", "Percent of measure rows with actual value."),
                new SupportedMethod(METHOD_ACHIEVEMENT_RATE, "Achievement Rate", "target == null || target == 0 ? 0 : actual * 100 / target", "Latest actual divided by target as percent."),
                new SupportedMethod(METHOD_PASS_RATE, "Pass Rate", "passRate", "Percent of measure rows that pass KPI compare rule."));
    }

    public static BigDecimal executeBuiltin(String aggrCode, List<BigDecimal> scores) {
        AggregationContext context = new AggregationContext();
        context.setScores(scores);
        return executeBuiltin(aggrCode, context);
    }

    public static BigDecimal executeBuiltin(String aggrCode, MdKpi kpi, List<MdKpiMeasureData> measureDataList) {
        AggregationContext context = new AggregationContext();
        context.setKpi(kpi);
        context.setMeasureDataList(measureDataList);
        return executeBuiltin(aggrCode, context);
    }

    public static BigDecimal executeBuiltin(String aggrCode, AggregationContext context) {
        if (StringUtils.isBlank(aggrCode)) {
            return BigDecimal.ZERO;
        }
        aggrCode = StringUtils.trimToEmpty(aggrCode).toUpperCase();
        List<BigDecimal> scores = context == null ? null : context.getScores();
        if (CollectionUtils.isEmpty(scores)) {
            scores = java.util.Collections.emptyList();
        }
        if (METHOD_LATEST_ACTUAL.equals(aggrCode)) {
            return latestActual(context);
        }
        if (METHOD_FIRST_ACTUAL.equals(aggrCode)) {
            return firstActual(context);
        }
        if (METHOD_NON_NULL_CNT.equals(aggrCode)) {
            return nonNullCount(context);
        }
        if (METHOD_VALID_RATE.equals(aggrCode)) {
            return validRate(context);
        }
        if (METHOD_ACHIEVEMENT_RATE.equals(aggrCode)) {
            return achievementRate(context);
        }
        if (METHOD_PASS_RATE.equals(aggrCode)) {
            return passRate(context);
        }
        if (METHOD_AVG.equals(aggrCode)) {
            if (CollectionUtils.isEmpty(scores)) {
                return BigDecimal.ZERO;
            }
            BigDecimal sum = BigDecimal.ZERO;
            for (BigDecimal s : scores) {
                sum = sum.add(s);
            }
            return sum.divide(BigDecimal.valueOf(scores.size()), 4, RoundingMode.HALF_UP);
        }
        if (METHOD_SUM.equals(aggrCode)) {
            if (CollectionUtils.isEmpty(scores)) {
                return BigDecimal.ZERO;
            }
            BigDecimal sum = BigDecimal.ZERO;
            for (BigDecimal s : scores) {
                sum = sum.add(s);
            }
            return sum;
        }
        if (METHOD_MAX.equals(aggrCode)) {
            if (CollectionUtils.isEmpty(scores)) {
                return BigDecimal.ZERO;
            }
            BigDecimal max = scores.get(0);
            for (BigDecimal s : scores) {
                if (s.compareTo(max) > 0) max = s;
            }
            return max;
        }
        if (METHOD_MIN.equals(aggrCode)) {
            if (CollectionUtils.isEmpty(scores)) {
                return BigDecimal.ZERO;
            }
            BigDecimal min = scores.get(0);
            for (BigDecimal s : scores) {
                if (s.compareTo(min) < 0) min = s;
            }
            return min;
        }
        if (METHOD_CNT.equals(aggrCode)) {
            return BigDecimal.valueOf(scores.size());
        }
        if (METHOD_DISTINCT.equals(aggrCode)) {
            long distinctCount = scores.stream()
                    .map(BigDecimal::stripTrailingZeros)
                    .distinct()
                    .count();
            return BigDecimal.valueOf(distinctCount);
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal executeScript(String expression, List<BigDecimal> scores) {
        AggregationContext context = new AggregationContext();
        context.setScores(scores);
        return executeScript(expression, context);
    }

    public static BigDecimal executeScript(String expression, MdKpi kpi, List<MdKpiMeasureData> measureDataList) {
        AggregationContext context = new AggregationContext();
        context.setKpi(kpi);
        context.setMeasureDataList(measureDataList);
        return executeScript(expression, context);
    }

    public static BigDecimal executeScript(String expression, AggregationContext context) {
        Map<String, Object> parameters = new HashMap<>();
        putContextParameters(parameters, context);
        Object result = ScriptExpressionUtils.execute(ScriptTypeCode.GROOVY, expression, null, parameters);
        if (result instanceof BigDecimal) {
            return (BigDecimal) result;
        }
        if (result instanceof Number) {
            return new BigDecimal(result.toString());
        }
        return BigDecimal.ZERO;
    }

    private static AggregationContext toContext(AggregationMethodTestRequest request) {
        AggregationContext context = new AggregationContext();
        context.setKpi(request.getKpi());
        context.setMeasureDataList(request.getMeasureDataList());
        context.setScores(request.getScores());
        return context;
    }

    private static void putContextParameters(Map<String, Object> parameters, AggregationContext context) {
        List<BigDecimal> scores = context == null ? java.util.Collections.emptyList() : context.getScores();
        List<MdKpiMeasureData> measures = context == null ? java.util.Collections.emptyList() : context.getMeasureDataList();
        MdKpi kpi = context == null ? null : context.getKpi();
        MdKpiMeasureData latestMeasure = latestMeasure(context);
        BigDecimal actual = latestMeasure == null ? latestScore(scores) : latestMeasure.getActualValue();
        BigDecimal target = resolveTarget(kpi, latestMeasure);

        parameters.put("scores", scores);
        parameters.put("kpi", kpi);
        parameters.put("measures", measures);
        parameters.put("latestMeasure", latestMeasure);
        parameters.put("actual", actual);
        parameters.put("target", target);
        parameters.put("passRate", passRate(context));
        if (kpi != null) {
            parameters.put("kpiMax", kpi.getMaxValue());
            parameters.put("kpiMin", kpi.getMinValue());
            parameters.put("kpiTarget", kpi.getTargetValue());
            parameters.put("kpiWeight", kpi.getWeightValue());
        }
    }

    private static BigDecimal latestActual(AggregationContext context) {
        MdKpiMeasureData measureData = latestMeasure(context);
        if (measureData != null && measureData.getActualValue() != null) {
            return measureData.getActualValue();
        }
        return latestScore(context == null ? null : context.getScores());
    }

    private static BigDecimal firstActual(AggregationContext context) {
        MdKpiMeasureData measureData = firstMeasure(context);
        if (measureData != null && measureData.getActualValue() != null) {
            return measureData.getActualValue();
        }
        List<BigDecimal> scores = context == null ? null : context.getScores();
        return CollectionUtils.isEmpty(scores) ? BigDecimal.ZERO : scores.get(0);
    }

    private static BigDecimal latestScore(List<BigDecimal> scores) {
        return CollectionUtils.isEmpty(scores) ? BigDecimal.ZERO : scores.get(scores.size() - 1);
    }

    private static BigDecimal nonNullCount(AggregationContext context) {
        List<MdKpiMeasureData> measures = context == null ? null : context.getMeasureDataList();
        if (CollectionUtils.isNotEmpty(measures)) {
            long count = measures.stream()
                    .filter(item -> item != null && item.getActualValue() != null)
                    .count();
            return BigDecimal.valueOf(count);
        }
        List<BigDecimal> scores = context == null ? null : context.getScores();
        return BigDecimal.valueOf(CollectionUtils.isEmpty(scores) ? 0 : scores.size());
    }

    private static BigDecimal validRate(AggregationContext context) {
        List<MdKpiMeasureData> measures = context == null ? null : context.getMeasureDataList();
        if (CollectionUtils.isEmpty(measures)) {
            return BigDecimal.ZERO;
        }
        BigDecimal validCount = nonNullCount(context);
        return validCount.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(measures.size()), 4, RoundingMode.HALF_UP);
    }

    private static BigDecimal achievementRate(AggregationContext context) {
        MdKpi kpi = context == null ? null : context.getKpi();
        MdKpiMeasureData latestMeasure = latestMeasure(context);
        BigDecimal actual = latestMeasure == null ? latestActual(context) : latestMeasure.getActualValue();
        BigDecimal target = resolveTarget(kpi, latestMeasure);
        if (actual == null || target == null || BigDecimal.ZERO.compareTo(target) == 0) {
            return BigDecimal.ZERO;
        }
        return actual.multiply(BigDecimal.valueOf(100)).divide(target, 4, RoundingMode.HALF_UP);
    }

    private static BigDecimal passRate(AggregationContext context) {
        List<MdKpiMeasureData> measures = context == null ? null : context.getMeasureDataList();
        if (CollectionUtils.isEmpty(measures)) {
            return BigDecimal.ZERO;
        }
        MdKpi kpi = context.getKpi();
        long testedCount = measures.stream()
                .filter(item -> item != null && item.getActualValue() != null)
                .count();
        if (testedCount == 0) {
            return BigDecimal.ZERO;
        }
        long passedCount = measures.stream()
                .filter(item -> item != null && item.getActualValue() != null)
                .filter(item -> isPassed(kpi, item))
                .count();
        return BigDecimal.valueOf(passedCount).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(testedCount), 4, RoundingMode.HALF_UP);
    }

    private static boolean isPassed(MdKpi kpi, MdKpiMeasureData measureData) {
        BigDecimal actual = measureData.getActualValue();
        BigDecimal target = resolveTarget(kpi, measureData);
        BigDecimal min = measureData.getMinValue() != null ? measureData.getMinValue() : kpi == null ? null : kpi.getMinValue();
        BigDecimal max = measureData.getMaxValue() != null ? measureData.getMaxValue() : kpi == null ? null : kpi.getMaxValue();
        String compareMode = kpi == null ? "" : StringUtils.defaultString(kpi.getCompareMode()).toUpperCase();

        if (actual == null) {
            return false;
        }
        if ("RANGE".equals(compareMode) || Strings.CS.contains(compareMode, "BETWEEN")) {
            boolean minPassed = min == null || actual.compareTo(min) >= 0;
            boolean maxPassed = max == null || actual.compareTo(max) <= 0;
            return minPassed && maxPassed;
        }
        if ("MINIMUM".equals(compareMode)) {
            return min != null ? actual.compareTo(min) >= 0 : target != null && actual.compareTo(target) >= 0;
        }
        if ("MAXIMUM".equals(compareMode)) {
            return max != null ? actual.compareTo(max) <= 0 : target != null && actual.compareTo(target) <= 0;
        }
        if (target == null) {
            return false;
        }
        if (Strings.CS.contains(compareMode, "LESS") || Strings.CS.contains(compareMode, "LOWER") || Strings.CS.contains(compareMode, "LT")) {
            return actual.compareTo(target) <= 0;
        }
        if (Strings.CS.contains(compareMode, "EQUAL") || Strings.CS.contains(compareMode, "EQ")) {
            return actual.compareTo(target) == 0;
        }
        return actual.compareTo(target) >= 0;
    }

    private static BigDecimal resolveTarget(MdKpi kpi, MdKpiMeasureData measureData) {
        if (measureData != null && measureData.getTargetValue() != null) {
            return measureData.getTargetValue();
        }
        return kpi == null ? null : kpi.getTargetValue();
    }

    private static MdKpiMeasureData latestMeasure(AggregationContext context) {
        List<MdKpiMeasureData> sortedMeasures = sortedMeasures(context);
        return CollectionUtils.isEmpty(sortedMeasures) ? null : sortedMeasures.get(sortedMeasures.size() - 1);
    }

    private static MdKpiMeasureData firstMeasure(AggregationContext context) {
        List<MdKpiMeasureData> sortedMeasures = sortedMeasures(context);
        return CollectionUtils.isEmpty(sortedMeasures) ? null : sortedMeasures.get(0);
    }

    private static List<MdKpiMeasureData> sortedMeasures(AggregationContext context) {
        List<MdKpiMeasureData> measures = context == null ? null : context.getMeasureDataList();
        if (CollectionUtils.isEmpty(measures)) {
            return java.util.Collections.emptyList();
        }
        return measures.stream()
                .filter(item -> item != null)
                .sorted(Comparator.comparing(item -> item.getMeasureDate() == null ? new Date(0L) : item.getMeasureDate()))
                .collect(Collectors.toList());
    }

    public static class SupportedMethod implements java.io.Serializable {
        private static final long serialVersionUID = 1L;

        private String code;
        private String label;
        private String expression;
        private String description;

        public SupportedMethod(String code, String label, String expression, String description) {
            this.code = code;
            this.label = label;
            this.expression = expression;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
