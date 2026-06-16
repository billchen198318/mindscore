package org.qifu.md.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.qifu.base.model.ScriptTypeCode;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiMeasureData;
import org.qifu.md.model.FormulaTestRequest;
import org.qifu.util.ScriptExpressionUtils;

public class FormulaUtils {
    public static final String TOKEN_ACTUAL = "$P{actual}";
    public static final String TOKEN_TARGET = "$P{target}";
    public static final String TOKEN_KPI_MAX = "$P{kpi.max}";
    public static final String TOKEN_KPI_MIN = "$P{kpi.min}";
    public static final String TOKEN_KPI_TARGET = "$P{kpi.target}";
    public static final String TOKEN_KPI_WEIGHT = "$P{kpi.weight}";

    public static final String VAR_ACTUAL = "actual";
    public static final String VAR_TARGET = "target";
    public static final String VAR_KPI_MAX = "kpiMax";
    public static final String VAR_KPI_MIN = "kpiMin";
    public static final String VAR_KPI_TARGET = "kpiTarget";
    public static final String VAR_KPI_WEIGHT = "kpiWeight";

    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\$P\\{([A-Za-z0-9_.-]+)\\}");
    private static final Map<String, String> TOKEN_VARIABLE_MAP = new LinkedHashMap<>();

    static {
        TOKEN_VARIABLE_MAP.put(TOKEN_ACTUAL, VAR_ACTUAL);
        TOKEN_VARIABLE_MAP.put(TOKEN_TARGET, VAR_TARGET);
        TOKEN_VARIABLE_MAP.put(TOKEN_KPI_MAX, VAR_KPI_MAX);
        TOKEN_VARIABLE_MAP.put(TOKEN_KPI_MIN, VAR_KPI_MIN);
        TOKEN_VARIABLE_MAP.put(TOKEN_KPI_TARGET, VAR_KPI_TARGET);
        TOKEN_VARIABLE_MAP.put(TOKEN_KPI_WEIGHT, VAR_KPI_WEIGHT);
    }

    protected FormulaUtils() {
        throw new IllegalStateException("Utils class: FormulaUtils");
    }

    public static Object test(FormulaTestRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Formula test request cannot be null.");
        }
        return execute(request.getScriptType(), request.getExpression(), getParameter(request));
    }

    public static Object execute(String scriptType, String expression, Map<String, Object> parameters) {
        validate(scriptType, expression);
        return ScriptExpressionUtils.execute(scriptType, parseExpression(expression), null, parameters);
    }

    public static String parseExpression(String expression) {
        if (StringUtils.isBlank(expression)) {
            return expression;
        }
        String parsedExpression = normalizeOperators(expression);
        for (Map.Entry<String, String> entry : TOKEN_VARIABLE_MAP.entrySet()) {
            parsedExpression = Strings.CS.replace(parsedExpression, entry.getKey(), entry.getValue());
        }
        validateSupportedTokens(parsedExpression);
        return parsedExpression;
    }

    public static Map<String, Object> getParameter(FormulaTestRequest request) {
        Map<String, Object> parameters = new HashMap<>();
        putIfNotNull(parameters, VAR_ACTUAL, request.getActual());
        putIfNotNull(parameters, VAR_TARGET, request.getTarget());
        putIfNotNull(parameters, VAR_KPI_MAX, request.getKpiMax());
        putIfNotNull(parameters, VAR_KPI_MIN, request.getKpiMin());
        putIfNotNull(parameters, VAR_KPI_TARGET, request.getKpiTarget());
        putIfNotNull(parameters, VAR_KPI_WEIGHT, request.getKpiWeight());
        return parameters;
    }

    public static Map<String, Object> getParameter(MdKpi kpi, MdKpiMeasureData measureData) {
        Map<String, Object> parameters = new HashMap<>();
        if (measureData != null) {
            putIfNotNull(parameters, VAR_ACTUAL, measureData.getActualValue());
        }
        putIfNotNull(parameters, VAR_TARGET, resolveTarget(kpi, measureData));
        if (kpi != null) {
            putIfNotNull(parameters, VAR_KPI_MAX, resolveMax(kpi, measureData));
            putIfNotNull(parameters, VAR_KPI_MIN, resolveMin(kpi, measureData));
            putIfNotNull(parameters, VAR_KPI_TARGET, kpi.getTargetValue());
            putIfNotNull(parameters, VAR_KPI_WEIGHT, kpi.getWeightValue());
        }
        return parameters;
    }

    public static FormulaTestRequest defaultTestRequest(String scriptType, String expression) {
        FormulaTestRequest request = new FormulaTestRequest();
        request.setScriptType(scriptType);
        request.setExpression(expression);
        request.setActual(BigDecimal.valueOf(70));
        request.setTarget(BigDecimal.valueOf(100));
        request.setKpiMax(BigDecimal.valueOf(100));
        request.setKpiMin(BigDecimal.valueOf(0));
        request.setKpiTarget(BigDecimal.valueOf(80));
        request.setKpiWeight(BigDecimal.valueOf(1));
        return request;
    }

    private static void validate(String scriptType, String expression) {
        if (!ScriptTypeCode.GROOVY.equals(scriptType)) {
            throw new IllegalArgumentException("Formula scriptType only supports GROOVY.");
        }
        if (StringUtils.isBlank(expression)) {
            throw new IllegalArgumentException("Formula expression cannot be blank.");
        }
    }

    private static String normalizeOperators(String expression) {
        String normalizedExpression = expression;
        normalizedExpression = Strings.CS.replace(normalizedExpression, "÷", "/");
        normalizedExpression = Strings.CS.replace(normalizedExpression, "×", "*");
        normalizedExpression = Strings.CS.replace(normalizedExpression, "−", "-");
        return normalizedExpression;
    }

    private static void validateSupportedTokens(String expression) {
        Matcher matcher = TOKEN_PATTERN.matcher(expression);
        if (matcher.find()) {
            throw new IllegalArgumentException("Unsupported formula parameter token: $P{" + matcher.group(1) + "}");
        }
    }

    private static void putIfNotNull(Map<String, Object> parameters, String key, Object value) {
        if (value != null) {
            parameters.put(key, value);
        }
    }

    private static BigDecimal resolveTarget(MdKpi kpi, MdKpiMeasureData measureData) {
        if (measureData != null && measureData.getTargetValue() != null) {
            return measureData.getTargetValue();
        }
        return kpi == null ? null : kpi.getTargetValue();
    }

    private static BigDecimal resolveMin(MdKpi kpi, MdKpiMeasureData measureData) {
        if (measureData != null && measureData.getMinValue() != null) {
            return measureData.getMinValue();
        }
        return kpi == null ? null : kpi.getMinValue();
    }

    private static BigDecimal resolveMax(MdKpi kpi, MdKpiMeasureData measureData) {
        if (measureData != null && measureData.getMaxValue() != null) {
            return measureData.getMaxValue();
        }
        return kpi == null ? null : kpi.getMaxValue();
    }
}
