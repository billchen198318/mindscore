package org.qifu.md.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Strings;
import org.qifu.base.model.ScriptTypeCode;
import org.qifu.md.model.AggregationMethodTestRequest;
import org.qifu.util.ScriptExpressionUtils;

public class AggregationMethodUtils {

    protected AggregationMethodUtils() {
        throw new IllegalStateException("Utils class: AggregationMethodUtils");
    }

    public static BigDecimal test(AggregationMethodTestRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Aggregation test request cannot be null.");
        }
        return executeScript(request.getExpression(), request.getScores());
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
}
