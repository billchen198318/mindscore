package org.qifu.md.model;

import java.math.BigDecimal;

public class ManagementDashboardDomainSummary implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private int totalCount;
    private BigDecimal avgScore = BigDecimal.ZERO;
    private int goodCount;
    private int warningCount;
    private int badCount;
    private int unknownCount;
    private int overdueCount;
    private int completedCount;
    private int secondaryCount;
    private int tertiaryCount;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(BigDecimal avgScore) {
        this.avgScore = avgScore;
    }

    public int getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(int goodCount) {
        this.goodCount = goodCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public int getBadCount() {
        return badCount;
    }

    public void setBadCount(int badCount) {
        this.badCount = badCount;
    }

    public int getUnknownCount() {
        return unknownCount;
    }

    public void setUnknownCount(int unknownCount) {
        this.unknownCount = unknownCount;
    }

    public int getOverdueCount() {
        return overdueCount;
    }

    public void setOverdueCount(int overdueCount) {
        this.overdueCount = overdueCount;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    public int getSecondaryCount() {
        return secondaryCount;
    }

    public void setSecondaryCount(int secondaryCount) {
        this.secondaryCount = secondaryCount;
    }

    public int getTertiaryCount() {
        return tertiaryCount;
    }

    public void setTertiaryCount(int tertiaryCount) {
        this.tertiaryCount = tertiaryCount;
    }
}
