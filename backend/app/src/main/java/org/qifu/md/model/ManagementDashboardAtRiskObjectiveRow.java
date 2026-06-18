package org.qifu.md.model;

import java.math.BigDecimal;

public class ManagementDashboardAtRiskObjectiveRow implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String objectiveOid;
    private String objectiveCode;
    private String objectiveName;
    private String cycleOid;
    private String periodKey;
    private BigDecimal progressValue = BigDecimal.ZERO;
    private BigDecimal confidenceScore = BigDecimal.ZERO;
    private String scoreStatus;

    public String getObjectiveOid() {
        return objectiveOid;
    }

    public void setObjectiveOid(String objectiveOid) {
        this.objectiveOid = objectiveOid;
    }

    public String getObjectiveCode() {
        return objectiveCode;
    }

    public void setObjectiveCode(String objectiveCode) {
        this.objectiveCode = objectiveCode;
    }

    public String getObjectiveName() {
        return objectiveName;
    }

    public void setObjectiveName(String objectiveName) {
        this.objectiveName = objectiveName;
    }

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

    public BigDecimal getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(BigDecimal progressValue) {
        this.progressValue = progressValue;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getScoreStatus() {
        return scoreStatus;
    }

    public void setScoreStatus(String scoreStatus) {
        this.scoreStatus = scoreStatus;
    }
}
