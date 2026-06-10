package org.qifu.md.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdStrategySnapshot extends MdStrategySnapshotKey {
	private static final long serialVersionUID = 1L;
	
	private BigDecimal scoreValue;
	private Integer kpiCount;
	private Integer okrCount;
	private String calculationTrace;
	private Date snapshotAt;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;
	
	public BigDecimal getScoreValue() {
		return scoreValue;
	}
	
	public void setScoreValue(BigDecimal scoreValue) {
		this.scoreValue = scoreValue;
	}
	
	public Integer getKpiCount() {
		return kpiCount;
	}
	
	public void setKpiCount(Integer kpiCount) {
		this.kpiCount = kpiCount;
	}
	
	public Integer getOkrCount() {
		return okrCount;
	}
	
	public void setOkrCount(Integer okrCount) {
		this.okrCount = okrCount;
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