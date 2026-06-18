package org.qifu.md.model;

import java.math.BigDecimal;

public class ActionReportSummary implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private int planCount;
    private int itemCount;
    private int overdueCount;
    private int completedCount;
    private BigDecimal avgProgress = BigDecimal.ZERO;
    private int planStageCount;
    private int doStageCount;
    private int checkStageCount;
    private int actStageCount;
    private int ownerCount;
    private int sourceLinkCount;

    public int getPlanCount() {
        return planCount;
    }

    public void setPlanCount(int planCount) {
        this.planCount = planCount;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
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

    public BigDecimal getAvgProgress() {
        return avgProgress;
    }

    public void setAvgProgress(BigDecimal avgProgress) {
        this.avgProgress = avgProgress;
    }

    public int getPlanStageCount() {
        return planStageCount;
    }

    public void setPlanStageCount(int planStageCount) {
        this.planStageCount = planStageCount;
    }

    public int getDoStageCount() {
        return doStageCount;
    }

    public void setDoStageCount(int doStageCount) {
        this.doStageCount = doStageCount;
    }

    public int getCheckStageCount() {
        return checkStageCount;
    }

    public void setCheckStageCount(int checkStageCount) {
        this.checkStageCount = checkStageCount;
    }

    public int getActStageCount() {
        return actStageCount;
    }

    public void setActStageCount(int actStageCount) {
        this.actStageCount = actStageCount;
    }

    public int getOwnerCount() {
        return ownerCount;
    }

    public void setOwnerCount(int ownerCount) {
        this.ownerCount = ownerCount;
    }

    public int getSourceLinkCount() {
        return sourceLinkCount;
    }

    public void setSourceLinkCount(int sourceLinkCount) {
        this.sourceLinkCount = sourceLinkCount;
    }
}
