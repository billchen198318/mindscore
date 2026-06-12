package org.qifu.md.model;

import java.math.BigDecimal;

public class FormulaTestRequest implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String scriptType;
    private String expression;
    private BigDecimal actual;
    private BigDecimal target;
    private BigDecimal kpiMax;
    private BigDecimal kpiMin;
    private BigDecimal kpiTarget;
    private BigDecimal kpiWeight;

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public BigDecimal getActual() {
        return actual;
    }

    public void setActual(BigDecimal actual) {
        this.actual = actual;
    }

    public BigDecimal getTarget() {
        return target;
    }

    public void setTarget(BigDecimal target) {
        this.target = target;
    }

    public BigDecimal getKpiMax() {
        return kpiMax;
    }

    public void setKpiMax(BigDecimal kpiMax) {
        this.kpiMax = kpiMax;
    }

    public BigDecimal getKpiMin() {
        return kpiMin;
    }

    public void setKpiMin(BigDecimal kpiMin) {
        this.kpiMin = kpiMin;
    }

    public BigDecimal getKpiTarget() {
        return kpiTarget;
    }

    public void setKpiTarget(BigDecimal kpiTarget) {
        this.kpiTarget = kpiTarget;
    }

    public BigDecimal getKpiWeight() {
        return kpiWeight;
    }

    public void setKpiWeight(BigDecimal kpiWeight) {
        this.kpiWeight = kpiWeight;
    }
}
