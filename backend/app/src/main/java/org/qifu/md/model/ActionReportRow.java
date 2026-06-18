package org.qifu.md.model;

import java.math.BigDecimal;
import java.util.Date;

public class ActionReportRow implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String oid;
    private String planOid;
    private String planCode;
    private String planName;
    private String itemName;
    private String actionStage;
    private String status;
    private Date startDate;
    private Date endDate;
    private Date doneDate;
    private BigDecimal progressValue;
    private boolean overdue;
    private String ownerSummary;
    private String sourceSummary;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getPlanOid() {
        return planOid;
    }

    public void setPlanOid(String planOid) {
        this.planOid = planOid;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getActionStage() {
        return actionStage;
    }

    public void setActionStage(String actionStage) {
        this.actionStage = actionStage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Date doneDate) {
        this.doneDate = doneDate;
    }

    public BigDecimal getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(BigDecimal progressValue) {
        this.progressValue = progressValue;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public String getOwnerSummary() {
        return ownerSummary;
    }

    public void setOwnerSummary(String ownerSummary) {
        this.ownerSummary = ownerSummary;
    }

    public String getSourceSummary() {
        return sourceSummary;
    }

    public void setSourceSummary(String sourceSummary) {
        this.sourceSummary = sourceSummary;
    }
}
