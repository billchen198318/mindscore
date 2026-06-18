package org.qifu.md.model;

import java.math.BigDecimal;

public class ManagementDashboardOrgSummary implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String orgOid;
    private String orgCode;
    private String orgName;
    private int kpiSnapshotCount;
    private BigDecimal avgKpiScore = BigDecimal.ZERO;
    private int goodCount;
    private int warningCount;
    private int badCount;
    private int unknownCount;

    public String getOrgOid() {
        return orgOid;
    }

    public void setOrgOid(String orgOid) {
        this.orgOid = orgOid;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public int getKpiSnapshotCount() {
        return kpiSnapshotCount;
    }

    public void setKpiSnapshotCount(int kpiSnapshotCount) {
        this.kpiSnapshotCount = kpiSnapshotCount;
    }

    public BigDecimal getAvgKpiScore() {
        return avgKpiScore;
    }

    public void setAvgKpiScore(BigDecimal avgKpiScore) {
        this.avgKpiScore = avgKpiScore;
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
