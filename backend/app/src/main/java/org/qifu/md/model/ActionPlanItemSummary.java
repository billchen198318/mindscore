package org.qifu.md.model;

import java.math.BigDecimal;

public class ActionPlanItemSummary implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private int itemCount;
    private BigDecimal avgProgress = BigDecimal.ZERO;
    private int completedCount;
    private int overdueCount;

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public BigDecimal getAvgProgress() {
        return avgProgress;
    }

    public void setAvgProgress(BigDecimal avgProgress) {
        this.avgProgress = avgProgress;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    public int getOverdueCount() {
        return overdueCount;
    }

    public void setOverdueCount(int overdueCount) {
        this.overdueCount = overdueCount;
    }
}
