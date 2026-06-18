package org.qifu.md.model;

public class ManagementDashboardQuery implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String periodType;
    private String periodKey;
    private String periodKeyFrom;
    private String periodKeyTo;
    private String dataForType;
    private String account;
    private String orgOid;
    private String cycleOid;
    private String workspaceOid;

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public String getPeriodKey() {
        return periodKey;
    }

    public void setPeriodKey(String periodKey) {
        this.periodKey = periodKey;
    }

    public String getPeriodKeyFrom() {
        return periodKeyFrom;
    }

    public void setPeriodKeyFrom(String periodKeyFrom) {
        this.periodKeyFrom = periodKeyFrom;
    }

    public String getPeriodKeyTo() {
        return periodKeyTo;
    }

    public void setPeriodKeyTo(String periodKeyTo) {
        this.periodKeyTo = periodKeyTo;
    }

    public String getDataForType() {
        return dataForType;
    }

    public void setDataForType(String dataForType) {
        this.dataForType = dataForType;
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

    public String getCycleOid() {
        return cycleOid;
    }

    public void setCycleOid(String cycleOid) {
        this.cycleOid = cycleOid;
    }

    public String getWorkspaceOid() {
        return workspaceOid;
    }

    public void setWorkspaceOid(String workspaceOid) {
        this.workspaceOid = workspaceOid;
    }
}
