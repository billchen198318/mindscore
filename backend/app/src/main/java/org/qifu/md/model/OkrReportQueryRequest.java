package org.qifu.md.model;

public class OkrReportQueryRequest implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String cycleOid;
    private String periodKey;
    private String status;
    private String orgOid;
    private String account;

    public String getCycleOid() {
        return cycleOid;
    }

    public void setCycleOid(String cycleOid) {
        this.cycleOid = cycleOid;
    }

    public String getPeriodKey() {
        return periodKey;
    }

    public void setPeriodKey(String periodKey) {
        this.periodKey = periodKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrgOid() {
        return orgOid;
    }

    public void setOrgOid(String orgOid) {
        this.orgOid = orgOid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
