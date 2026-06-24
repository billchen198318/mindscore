package org.qifu.md.entity;

import java.math.BigDecimal;
import java.util.Date;
import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdPerformanceSignal extends MdPerformanceSignalKey {
    private static final long serialVersionUID = 1L;

    private String signalType;
    private String sourceType;
    private String sourceOid;
    private String sourceCode;
    private String sourceName;
    private String periodType;
    private String periodKey;
    private Date startDate;
    private Date endDate;
    private String ownerAccount;
    private String orgOid;
    private BigDecimal scoreValue;
    private BigDecimal targetValue;
    private BigDecimal actualValue;
    private BigDecimal varianceValue;
    private BigDecimal varianceRate;
    private String trendCode;
    private String statusCode;
    private String riskLevel;
    private String signalStatus;
    private String relatedObjectiveOid;
    private String relatedActionOid;
    private String snapshotOid;
    private String evidenceJson;
    private String explanationInput;
    private String generatorVersion;
    private Date generatedAt;
    private Date resolvedAt;
    private String cuserid;
    private Date cdate;
    private String uuserid;
    private Date udate;

    public String getSignalType() { return signalType; }
    public void setSignalType(String signalType) { this.signalType = signalType; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getSourceOid() { return sourceOid; }
    public void setSourceOid(String sourceOid) { this.sourceOid = sourceOid; }
    public String getSourceCode() { return sourceCode; }
    public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }
    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }
    public String getPeriodType() { return periodType; }
    public void setPeriodType(String periodType) { this.periodType = periodType; }
    public String getPeriodKey() { return periodKey; }
    public void setPeriodKey(String periodKey) { this.periodKey = periodKey; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public String getOwnerAccount() { return ownerAccount; }
    public void setOwnerAccount(String ownerAccount) { this.ownerAccount = ownerAccount; }
    public String getOrgOid() { return orgOid; }
    public void setOrgOid(String orgOid) { this.orgOid = orgOid; }
    public BigDecimal getScoreValue() { return scoreValue; }
    public void setScoreValue(BigDecimal scoreValue) { this.scoreValue = scoreValue; }
    public BigDecimal getTargetValue() { return targetValue; }
    public void setTargetValue(BigDecimal targetValue) { this.targetValue = targetValue; }
    public BigDecimal getActualValue() { return actualValue; }
    public void setActualValue(BigDecimal actualValue) { this.actualValue = actualValue; }
    public BigDecimal getVarianceValue() { return varianceValue; }
    public void setVarianceValue(BigDecimal varianceValue) { this.varianceValue = varianceValue; }
    public BigDecimal getVarianceRate() { return varianceRate; }
    public void setVarianceRate(BigDecimal varianceRate) { this.varianceRate = varianceRate; }
    public String getTrendCode() { return trendCode; }
    public void setTrendCode(String trendCode) { this.trendCode = trendCode; }
    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public String getSignalStatus() { return signalStatus; }
    public void setSignalStatus(String signalStatus) { this.signalStatus = signalStatus; }
    public String getRelatedObjectiveOid() { return relatedObjectiveOid; }
    public void setRelatedObjectiveOid(String relatedObjectiveOid) { this.relatedObjectiveOid = relatedObjectiveOid; }
    public String getRelatedActionOid() { return relatedActionOid; }
    public void setRelatedActionOid(String relatedActionOid) { this.relatedActionOid = relatedActionOid; }
    public String getSnapshotOid() { return snapshotOid; }
    public void setSnapshotOid(String snapshotOid) { this.snapshotOid = snapshotOid; }
    public String getEvidenceJson() { return evidenceJson; }
    public void setEvidenceJson(String evidenceJson) { this.evidenceJson = evidenceJson; }
    public String getExplanationInput() { return explanationInput; }
    public void setExplanationInput(String explanationInput) { this.explanationInput = explanationInput; }
    public String getGeneratorVersion() { return generatorVersion; }
    public void setGeneratorVersion(String generatorVersion) { this.generatorVersion = generatorVersion; }
    public Date getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Date generatedAt) { this.generatedAt = generatedAt; }
    public Date getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Date resolvedAt) { this.resolvedAt = resolvedAt; }
    @CreateUserField(name = "cuserid") public String getCuserid() { return cuserid; }
    public void setCuserid(String cuserid) { this.cuserid = cuserid; }
    @CreateDateField(name = "cdate") public Date getCdate() { return cdate; }
    public void setCdate(Date cdate) { this.cdate = cdate; }
    @UpdateUserField(name = "uuserid") public String getUuserid() { return uuserid; }
    public void setUuserid(String uuserid) { this.uuserid = uuserid; }
    @UpdateDateField(name = "udate") public Date getUdate() { return udate; }
    public void setUdate(Date udate) { this.udate = udate; }
}