package org.qifu.md.model;

import java.util.ArrayList;
import java.util.List;

public class KpiMeasureDataImportRow implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private int rowNumber;
    private String kpiCode;
    private String periodType;
    private String periodKey;
    private String dataForType;
    private String orgCode;
    private String account;
    private String targetValue;
    private String actualValue;
    private String note;
    private String kpiName;
    private String orgName;
    private String action;
    private boolean valid;
    private List<String> errors = new ArrayList<>();

    public int getRowNumber() { return rowNumber; }
    public void setRowNumber(int rowNumber) { this.rowNumber = rowNumber; }
    public String getKpiCode() { return kpiCode; }
    public void setKpiCode(String kpiCode) { this.kpiCode = kpiCode; }
    public String getPeriodType() { return periodType; }
    public void setPeriodType(String periodType) { this.periodType = periodType; }
    public String getPeriodKey() { return periodKey; }
    public void setPeriodKey(String periodKey) { this.periodKey = periodKey; }
    public String getDataForType() { return dataForType; }
    public void setDataForType(String dataForType) { this.dataForType = dataForType; }
    public String getOrgCode() { return orgCode; }
    public void setOrgCode(String orgCode) { this.orgCode = orgCode; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getTargetValue() { return targetValue; }
    public void setTargetValue(String targetValue) { this.targetValue = targetValue; }
    public String getActualValue() { return actualValue; }
    public void setActualValue(String actualValue) { this.actualValue = actualValue; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getKpiName() { return kpiName; }
    public void setKpiName(String kpiName) { this.kpiName = kpiName; }
    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
