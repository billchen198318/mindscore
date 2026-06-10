package org.qifu.md.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdOkrObjective extends MdOkrObjectiveKey {
	private static final long serialVersionUID = 1L;
	
	private String objectiveName;
	private String description;
	private String parentOid;
	private BigDecimal confidenceScore;
	private BigDecimal progressValue;
	private String status;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;
	
	public String getObjectiveName() {
		return objectiveName;
	}
	
	public void setObjectiveName(String objectiveName) {
		this.objectiveName = objectiveName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getParentOid() {
		return parentOid;
	}
	
	public void setParentOid(String parentOid) {
		this.parentOid = parentOid;
	}
	
	public BigDecimal getConfidenceScore() {
		return confidenceScore;
	}
	
	public void setConfidenceScore(BigDecimal confidenceScore) {
		this.confidenceScore = confidenceScore;
	}
	
	public BigDecimal getProgressValue() {
		return progressValue;
	}
	
	public void setProgressValue(BigDecimal progressValue) {
		this.progressValue = progressValue;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	@CreateUserField(name = "cuserid")
	public String getCuserid() {
		return cuserid;
	}
	
	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}
	
	@CreateDateField(name = "cdate")
	public Date getCdate() {
		return cdate;
	}
	
	public void setCdate(Date cdate) {
		this.cdate = cdate;
	}
	
	@UpdateUserField(name = "uuserid")
	public String getUuserid() {
		return uuserid;
	}
	
	public void setUuserid(String uuserid) {
		this.uuserid = uuserid;
	}
	
	@UpdateDateField(name = "udate")
	public Date getUdate() {
		return udate;
	}
	
	public void setUdate(Date udate) {
		this.udate = udate;
	}
}