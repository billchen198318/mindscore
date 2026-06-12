package org.qifu.md.model;

import java.math.BigDecimal;
import java.util.List;

public class AggregationMethodTestRequest implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String aggrType;
    private String expression;
    private List<BigDecimal> scores;

    public String getAggrType() {
        return aggrType;
    }

    public void setAggrType(String aggrType) {
        this.aggrType = aggrType;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public List<BigDecimal> getScores() {
        return scores;
    }

    public void setScores(List<BigDecimal> scores) {
        this.scores = scores;
    }
}
