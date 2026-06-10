package org.qifu.md.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdOkrCheckin extends MdOkrCheckinKey {
	private static final long serialVersionUID = 1L;
	
	private String krOid;
	private Date checkinDate;
	private BigDecimal currentValue;
	private BigDecimal progressValue;
	private BigDecimal confidenceScore;
	private String commentText;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;
	
	public String getKrOid() {
		return krOid;
	}
	
	public void setKrOid(String krOid) {
		this.krOid = krOid;
	}
	
	public Date getCheckinDate() {
		return checkinDate;
	}
	
	public void setCheckinDate(Date checkinDate) {
		this.checkinDate = checkinDate;
	}
	
	public BigDecimal getCurrentValue() {
		return currentValue;
	}
	
	public void setCurrentValue(BigDecimal currentValue) {
		this.currentValue = currentValue;
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
	
	public String getCommentText() {
		return commentText;
	}
	
	public void setCommentText(String commentText) {
		this.commentText = commentText;
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