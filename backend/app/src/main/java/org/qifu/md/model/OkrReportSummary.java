package org.qifu.md.model;

import java.math.BigDecimal;

public class OkrReportSummary implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private int objectiveCount;
    private int keyResultCount;
    private int initiativeCount;
    private BigDecimal avgProgress = BigDecimal.ZERO;
    private int goodCount;
    private int warningCount;
    private int badCount;
    private int unknownCount;

    public int getObjectiveCount() {
        return objectiveCount;
    }

    public void setObjectiveCount(int objectiveCount) {
        this.objectiveCount = objectiveCount;
    }

    public int getKeyResultCount() {
        return keyResultCount;
    }

    public void setKeyResultCount(int keyResultCount) {
        this.keyResultCount = keyResultCount;
    }

    public int getInitiativeCount() {
        return initiativeCount;
    }

    public void setInitiativeCount(int initiativeCount) {
        this.initiativeCount = initiativeCount;
    }

    public BigDecimal getAvgProgress() {
        return avgProgress;
    }

    public void setAvgProgress(BigDecimal avgProgress) {
        this.avgProgress = avgProgress;
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
}
