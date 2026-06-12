package org.qifu.md.model;

import java.math.BigDecimal;
import java.util.List;

import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiMeasureData;

public class AggregationMethodTestRequest implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String aggrType;
    private String aggrCode;
    private String methodCode;
    private String expression;
    private List<BigDecimal> scores;
    private MdKpi kpi;
    private List<MdKpiMeasureData> measureDataList;

    public String getAggrType() {
        return aggrType;
    }

    public void setAggrType(String aggrType) {
        this.aggrType = aggrType;
    }

    public String getAggrCode() {
        return aggrCode;
    }

    public void setAggrCode(String aggrCode) {
        this.aggrCode = aggrCode;
    }

    public String getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
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

    public MdKpi getKpi() {
        return kpi;
    }

    public void setKpi(MdKpi kpi) {
        this.kpi = kpi;
    }

    public List<MdKpiMeasureData> getMeasureDataList() {
        return measureDataList;
    }

    public void setMeasureDataList(List<MdKpiMeasureData> measureDataList) {
        this.measureDataList = measureDataList;
    }
}
