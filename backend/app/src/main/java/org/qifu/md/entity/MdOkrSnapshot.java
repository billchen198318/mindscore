package org.qifu.md.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdOkrSnapshot extends MdOkrSnapshotKey {
	private static final long serialVersionUID = 1L;
	
	private BigDecimal progressValue;
	private BigDecimal confidenceScore;
	private String scoreStatus;
	private String calculationTrace;
	private Date snapshotAt;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;
	
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
	
	public String getCalculationTrace() {
		return calculationTrace;
	}
	
	public void setCalculationTrace(String calculationTrace) {
		this.calculationTrace = calculationTrace;
	}
	
	public Date getSnapshotAt() {
		return snapshotAt;
	}
	
	public void setSnapshotAt(Date snapshotAt) {
		this.snapshotAt = snapshotAt;
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