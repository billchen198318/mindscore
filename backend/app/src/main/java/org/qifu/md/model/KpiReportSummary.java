package org.qifu.md.model;

import java.math.BigDecimal;

public class KpiReportSummary implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private Integer kpiCount;
    private BigDecimal avgScore;
    private Integer goodCount;
    private Integer warningCount;
    private Integer badCount;
    private Integer unknownCount;

    public Integer getKpiCount() {
        return kpiCount;
    }

    public void setKpiCount(Integer kpiCount) {
        this.kpiCount = kpiCount;
    }

    public BigDecimal getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(BigDecimal avgScore) {
        this.avgScore = avgScore;
    }

    public Integer getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(Integer goodCount) {
        this.goodCount = goodCount;
    }

    public Integer getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(Integer warningCount) {
        this.warningCount = warningCount;
    }

    public Integer getBadCount() {
        return badCount;
    }

    public void setBadCount(Integer badCount) {
        this.badCount = badCount;
    }

    public Integer getUnknownCount() {
        return unknownCount;
    }

    public void setUnknownCount(Integer unknownCount) {
        this.unknownCount = unknownCount;
    }
}
