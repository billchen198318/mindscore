package org.qifu.md.model;

public class KpiReportQueryRequest implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String kpiOid;
    private String periodType;
    private String periodKey;
    private String periodKeyFrom;
    private String periodKeyTo;
    private String dataForType;
    private String account;
    private String orgOid;
    private Integer limit;

    public String getKpiOid() {
        return kpiOid;
    }

    public void setKpiOid(String kpiOid) {
        this.kpiOid = kpiOid;
    }

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

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
