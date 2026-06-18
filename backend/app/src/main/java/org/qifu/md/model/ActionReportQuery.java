package org.qifu.md.model;

import java.util.Date;

public class ActionReportQuery implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String planOid;
    private String actionStage;
    private String status;
    private String ownerType;
    private String account;
    private String orgOid;
    private String sourceType;
    private String sourceOid;
    private Date startDateFrom;
    private Date startDateTo;
    private Date endDateFrom;
    private Date endDateTo;
    private boolean overdueOnly;

    public String getPlanOid() {
        return planOid;
    }

    public void setPlanOid(String planOid) {
        this.planOid = planOid;
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

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOrgOid() {
        return orgOid;
    }

    public void setOrgOid(String orgOid) {
        this.orgOid = orgOid;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceOid() {
        return sourceOid;
    }

    public void setSourceOid(String sourceOid) {
        this.sourceOid = sourceOid;
    }

    public Date getStartDateFrom() {
        return startDateFrom;
    }

    public void setStartDateFrom(Date startDateFrom) {
        this.startDateFrom = startDateFrom;
    }

    public Date getStartDateTo() {
        return startDateTo;
    }

    public void setStartDateTo(Date startDateTo) {
        this.startDateTo = startDateTo;
    }

    public Date getEndDateFrom() {
        return endDateFrom;
    }

    public void setEndDateFrom(Date endDateFrom) {
        this.endDateFrom = endDateFrom;
    }

    public Date getEndDateTo() {
        return endDateTo;
    }

    public void setEndDateTo(Date endDateTo) {
        this.endDateTo = endDateTo;
    }

    public boolean isOverdueOnly() {
        return overdueOnly;
    }

    public void setOverdueOnly(boolean overdueOnly) {
        this.overdueOnly = overdueOnly;
    }
}
