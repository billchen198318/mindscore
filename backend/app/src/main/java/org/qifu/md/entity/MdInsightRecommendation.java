package org.qifu.md.entity;

import java.util.Date;

import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdInsightRecommendation extends MdInsightRecommendationKey {
    private static final long serialVersionUID = 1L;

    private String tenantOid;
    private String insightOid;
    private String recommendationType;
    private String title;
    private String contentText;
    private Integer priorityNo;
    private String status;
    private String acceptedFlag;
    private String actionCreatedFlag;
    private String cuserid;
    private Date cdate;
    private String uuserid;
    private Date udate;
    private Integer isDeleted;

    public String getTenantOid() { return tenantOid; }
    public void setTenantOid(String tenantOid) { this.tenantOid = tenantOid; }
    public String getInsightOid() { return insightOid; }
    public void setInsightOid(String insightOid) { this.insightOid = insightOid; }
    public String getRecommendationType() { return recommendationType; }
    public void setRecommendationType(String recommendationType) { this.recommendationType = recommendationType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContentText() { return contentText; }
    public void setContentText(String contentText) { this.contentText = contentText; }
    public Integer getPriorityNo() { return priorityNo; }
    public void setPriorityNo(Integer priorityNo) { this.priorityNo = priorityNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAcceptedFlag() { return acceptedFlag; }
    public void setAcceptedFlag(String acceptedFlag) { this.acceptedFlag = acceptedFlag; }
    public String getActionCreatedFlag() { return actionCreatedFlag; }
    public void setActionCreatedFlag(String actionCreatedFlag) { this.actionCreatedFlag = actionCreatedFlag; }
    @CreateUserField(name = "cuserid") public String getCuserid() { return cuserid; }
    public void setCuserid(String cuserid) { this.cuserid = cuserid; }
    @CreateDateField(name = "cdate") public Date getCdate() { return cdate; }
    public void setCdate(Date cdate) { this.cdate = cdate; }
    @UpdateUserField(name = "uuserid") public String getUuserid() { return uuserid; }
    public void setUuserid(String uuserid) { this.uuserid = uuserid; }
    @UpdateDateField(name = "udate") public Date getUdate() { return udate; }
    public void setUdate(Date udate) { this.udate = udate; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
