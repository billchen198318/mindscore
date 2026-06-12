package org.qifu.md.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.qifu.base.model.ScriptTypeCode;
import org.qifu.md.model.AggregationMethodTestRequest;
import org.qifu.util.ScriptExpressionUtils;

public class AggregationMethodUtils {

    private static final String METHOD_AVG = "AVG";
    private static final String METHOD_SUM = "SUM";
    private static final String METHOD_MAX = "MAX";
    private static final String METHOD_MIN = "MIN";
    private static final String METHOD_CNT = "CNT";

    protected AggregationMethodUtils() {
        throw new IllegalStateException("Utils class: AggregationMethodUtils");
    }

    public static BigDecimal test(AggregationMethodTestRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Aggregation test request cannot be null.");
        }
        if (StringUtils.isNotBlank(request.getMethodCode())) {
            return executeBuiltin(request.getMethodCode(), request.getScores());
        }
        return executeScript(request.getExpression(), request.getScores());
    }

    public static List<SupportedMethod> getSupportedMethods() {
        return Arrays.asList(
                new SupportedMethod(METHOD_SUM, "SUM", "scores == null ? 0 : scores.sum()", "Sum all score values."),
                new SupportedMethod(METHOD_AVG, "AVG", "scores == null || scores.isEmpty() ? 0 : scores.sum() / scores.size()", "Average all score values."),
                new SupportedMethod(METHOD_MAX, "MAX", "scores == null || scores.isEmpty() ? 0 : scores.max()", "Maximum score value."),
                new SupportedMethod(METHOD_MIN, "MIN", "scores == null || scores.isEmpty() ? 0 : scores.min()", "Minimum score value."),
                new SupportedMethod(METHOD_CNT, "Count", "scores == null ? 0 : scores.size()", "Count score values."));
    }

    public static BigDecimal executeBuiltin(String aggrCode, List<BigDecimal> scores) {
        if (CollectionUtils.isEmpty(scores)) {
            return BigDecimal.ZERO;
        }
        if (Strings.CS.contains(aggrCode, "AVG")) {
            BigDecimal sum = BigDecimal.ZERO;
            for (BigDecimal s : scores) {
                sum = sum.add(s);
            }
            return sum.divide(BigDecimal.valueOf(scores.size()), 4, RoundingMode.HALF_UP);
        }
        if (Strings.CS.contains(aggrCode, "SUM")) {
            BigDecimal sum = BigDecimal.ZERO;
            for (BigDecimal s : scores) {
                sum = sum.add(s);
            }
            return sum;
        }
        if (Strings.CS.contains(aggrCode, "MAX")) {
            BigDecimal max = scores.get(0);
            for (BigDecimal s : scores) {
                if (s.compareTo(max) > 0) max = s;
            }
            return max;
        }
        if (Strings.CS.contains(aggrCode, "MIN")) {
            BigDecimal min = scores.get(0);
            for (BigDecimal s : scores) {
                if (s.compareTo(min) < 0) min = s;
            }
            return min;
        }
        if (Strings.CS.contains(aggrCode, "CNT")) {
            return BigDecimal.valueOf(scores.size());
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal executeScript(String expression, List<BigDecimal> scores) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("scores", scores);
        Object result = ScriptExpressionUtils.execute(ScriptTypeCode.GROOVY, expression, null, parameters);
        if (result instanceof BigDecimal) {
            return (BigDecimal) result;
        }
        if (result instanceof Number) {
            return new BigDecimal(result.toString());
        }
        return BigDecimal.ZERO;
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
