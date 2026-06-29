package org.qifu.md.entity;

import java.util.Date;

import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdInsight extends MdInsightKey {
    private static final long serialVersionUID = 1L;

    private String tenantOid;
    private String insightNo;
    private String insightType;
    private String severity;
    private String sourceType;
    private String sourceOid;
    private String signalOid;
    private String ruleOid;
    private String title;
    private String summaryText;
    private String status;
    private String ownerAccount;
    private Date dueDate;
    private String generatedByType;
    private Date generatedAt;
    private Date acceptedAt;
    private Date dismissedAt;
    private Date resolvedAt;
    private Integer isDeleted;
    private String cuserid;
    private Date cdate;
    private String uuserid;
    private Date udate;

    public String getTenantOid() { return tenantOid; }
    public void setTenantOid(String tenantOid) { this.tenantOid = tenantOid; }
    public String getInsightNo() { return insightNo; }
    public void setInsightNo(String insightNo) { this.insightNo = insightNo; }
    public String getInsightType() { return insightType; }
    public void setInsightType(String insightType) { this.insightType = insightType; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getSourceOid() { return sourceOid; }
    public void setSourceOid(String sourceOid) { this.sourceOid = sourceOid; }
    public String getSignalOid() { return signalOid; }
    public void setSignalOid(String signalOid) { this.signalOid = signalOid; }
    public String getRuleOid() { return ruleOid; }
    public void setRuleOid(String ruleOid) { this.ruleOid = ruleOid; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummaryText() { return summaryText; }
    public void setSummaryText(String summaryText) { this.summaryText = summaryText; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getOwnerAccount() { return ownerAccount; }
    public void setOwnerAccount(String ownerAccount) { this.ownerAccount = ownerAccount; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public String getGeneratedByType() { return generatedByType; }
    public void setGeneratedByType(String generatedByType) { this.generatedByType = generatedByType; }
    public Date getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Date generatedAt) { this.generatedAt = generatedAt; }
    public Date getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(Date acceptedAt) { this.acceptedAt = acceptedAt; }
    public Date getDismissedAt() { return dismissedAt; }
    public void setDismissedAt(Date dismissedAt) { this.dismissedAt = dismissedAt; }
    public Date getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Date resolvedAt) { this.resolvedAt = resolvedAt; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
    @CreateUserField(name = "cuserid") public String getCuserid() { return cuserid; }
    public void setCuserid(String cuserid) { this.cuserid = cuserid; }
    @CreateDateField(name = "cdate") public Date getCdate() { return cdate; }
    public void setCdate(Date cdate) { this.cdate = cdate; }
    @UpdateUserField(name = "uuserid") public String getUuserid() { return uuserid; }
    public void setUuserid(String uuserid) { this.uuserid = uuserid; }
    @UpdateDateField(name = "udate") public Date getUdate() { return udate; }
    public void setUdate(Date udate) { this.udate = udate; }
}
