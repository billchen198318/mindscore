package org.qifu.md.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdKpiScoreSnapshot extends MdKpiScoreSnapshotKey {
	private static final long serialVersionUID = 1L;
	
	private BigDecimal rawTarget;
	private BigDecimal rawActual;
	private BigDecimal scoreValue;
	private String scoreStatus;
	private String formulaOid;
	private Integer formulaVersionNo;
	private String aggrMethodOid;
	private String calculationTrace;
	private Date calculatedAt;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;
	
	public BigDecimal getRawTarget() {
		return rawTarget;
	}
	
	public void setRawTarget(BigDecimal rawTarget) {
		this.rawTarget = rawTarget;
	}
	
	public BigDecimal getRawActual() {
		return rawActual;
	}
	
	public void setRawActual(BigDecimal rawActual) {
		this.rawActual = rawActual;
	}
	
	public BigDecimal getScoreValue() {
		return scoreValue;
	}
	
	public void setScoreValue(BigDecimal scoreValue) {
		this.scoreValue = scoreValue;
	}
	
	public String getScoreStatus() {
		return scoreStatus;
	}
	
	public void setScoreStatus(String scoreStatus) {
		this.scoreStatus = scoreStatus;
	}
	
	public String getFormulaOid() {
		return formulaOid;
	}
	
	public void setFormulaOid(String formulaOid) {
		this.formulaOid = formulaOid;
	}
	
	public Integer getFormulaVersionNo() {
		return formulaVersionNo;
	}
	
	public void setFormulaVersionNo(Integer formulaVersionNo) {
		this.formulaVersionNo = formulaVersionNo;
	}
	
	public String getAggrMethodOid() {
		return aggrMethodOid;
	}
	
	public void setAggrMethodOid(String aggrMethodOid) {
		this.aggrMethodOid = aggrMethodOid;
	}
	
	public String getCalculationTrace() {
		return calculationTrace;
	}
	
	public void setCalculationTrace(String calculationTrace) {
		this.calculationTrace = calculationTrace;
	}
	
	public Date getCalculatedAt() {
		return calculatedAt;
	}
	
	public void setCalculatedAt(Date calculatedAt) {
		this.calculatedAt = calculatedAt;
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