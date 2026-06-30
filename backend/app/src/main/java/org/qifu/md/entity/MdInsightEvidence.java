package org.qifu.md.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;

public class MdInsightEvidence extends MdInsightEvidenceKey {
    private static final long serialVersionUID = 1L;

    private String tenantOid;
    private String insightOid;
    private String evidenceType;
    private String sourceType;
    private String sourceOid;
    private String label;
    private String valueText;
    private BigDecimal valueNo;
    private String evidenceJson;
    private Integer sortNo;
    private String cuserid;
    private Date cdate;
    private Integer isDeleted;

    public String getTenantOid() { return tenantOid; }
    public void setTenantOid(String tenantOid) { this.tenantOid = tenantOid; }
    public String getInsightOid() { return insightOid; }
    public void setInsightOid(String insightOid) { this.insightOid = insightOid; }
    public String getEvidenceType() { return evidenceType; }
    public void setEvidenceType(String evidenceType) { this.evidenceType = evidenceType; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getSourceOid() { return sourceOid; }
    public void setSourceOid(String sourceOid) { this.sourceOid = sourceOid; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getValueText() { return valueText; }
    public void setValueText(String valueText) { this.valueText = valueText; }
    public BigDecimal getValueNo() { return valueNo; }
    public void setValueNo(BigDecimal valueNo) { this.valueNo = valueNo; }
    public String getEvidenceJson() { return evidenceJson; }
    public void setEvidenceJson(String evidenceJson) { this.evidenceJson = evidenceJson; }
    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }
    @CreateUserField(name = "cuserid") public String getCuserid() { return cuserid; }
    public void setCuserid(String cuserid) { this.cuserid = cuserid; }
    @CreateDateField(name = "cdate") public Date getCdate() { return cdate; }
    public void setCdate(Date cdate) { this.cdate = cdate; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
