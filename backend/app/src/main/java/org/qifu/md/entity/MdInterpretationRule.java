package org.qifu.md.entity;

import java.util.Date;

import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdInterpretationRule extends MdInterpretationRuleKey {
    private static final long serialVersionUID = 1L;

    private String tenantOid;
    private String ruleName;
    private String ruleType;
    private String sourceType;
    private String conditionExpr;
    private String actionExpr;
    private String severity;
    private String enabledFlag;
    private Integer priorityNo;
    private String description;
    private Integer isDeleted;
    private String cuserid;
    private Date cdate;
    private String uuserid;
    private Date udate;

    public String getTenantOid() { return tenantOid; }
    public void setTenantOid(String tenantOid) { this.tenantOid = tenantOid; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public String getRuleType() { return ruleType; }
    public void setRuleType(String ruleType) { this.ruleType = ruleType; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getConditionExpr() { return conditionExpr; }
    public void setConditionExpr(String conditionExpr) { this.conditionExpr = conditionExpr; }
    public String getActionExpr() { return actionExpr; }
    public void setActionExpr(String actionExpr) { this.actionExpr = actionExpr; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getEnabledFlag() { return enabledFlag; }
    public void setEnabledFlag(String enabledFlag) { this.enabledFlag = enabledFlag; }
    public Integer getPriorityNo() { return priorityNo; }
    public void setPriorityNo(Integer priorityNo) { this.priorityNo = priorityNo; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
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