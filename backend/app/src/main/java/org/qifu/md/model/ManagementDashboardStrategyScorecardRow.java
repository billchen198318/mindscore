package org.qifu.md.model;

import java.math.BigDecimal;

public class ManagementDashboardStrategyScorecardRow implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String workspaceOid;
    private String workspaceCode;
    private String workspaceName;
    private String periodType;
    private String periodKey;
    private BigDecimal scoreValue = BigDecimal.ZERO;
    private int kpiCount;
    private int okrCount;
    private int themeCount;
    private int objectiveCount;

    public String getWorkspaceOid() {
        return workspaceOid;
    }

    public void setWorkspaceOid(String workspaceOid) {
        this.workspaceOid = workspaceOid;
    }

    public String getWorkspaceCode() {
        return workspaceCode;
    }

    public void setWorkspaceCode(String workspaceCode) {
        this.workspaceCode = workspaceCode;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
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

    public BigDecimal getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(BigDecimal scoreValue) {
        this.scoreValue = scoreValue;
    }

    public int getKpiCount() {
        return kpiCount;
    }

    public void setKpiCount(int kpiCount) {
        this.kpiCount = kpiCount;
    }

    public int getOkrCount() {
        return okrCount;
    }

    public void setOkrCount(int okrCount) {
        this.okrCount = okrCount;
    }

    public int getThemeCount() {
        return themeCount;
    }

    public void setThemeCount(int themeCount) {
        this.themeCount = themeCount;
    }

    public int getObjectiveCount() {
        return objectiveCount;
    }

    public void setObjectiveCount(int objectiveCount) {
        this.objectiveCount = objectiveCount;
    }
}
