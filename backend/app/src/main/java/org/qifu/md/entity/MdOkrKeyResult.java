package org.qifu.md.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdOkrKeyResult extends MdOkrKeyResultKey {
	private static final long serialVersionUID = 1L;
	
	private String krName;
	private String krType;
	private BigDecimal startValue;
	private BigDecimal targetValue;
	private BigDecimal currentValue;
	private BigDecimal progressValue;
	private BigDecimal weightValue;
	private String unitName;
	private Integer sortNo;
	private String status;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;
	
	public String getKrName() {
		return krName;
	}
	
	public void setKrName(String krName) {
		this.krName = krName;
	}
	
	public String getKrType() {
		return krType;
	}
	
	public void setKrType(String krType) {
		this.krType = krType;
	}
	
	public BigDecimal getStartValue() {
		return startValue;
	}
	
	public void setStartValue(BigDecimal startValue) {
		this.startValue = startValue;
	}
	
	public BigDecimal getTargetValue() {
		return targetValue;
	}
	
	public void setTargetValue(BigDecimal targetValue) {
		this.targetValue = targetValue;
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
	
	public BigDecimal getWeightValue() {
		return weightValue;
	}
	
	public void setWeightValue(BigDecimal weightValue) {
		this.weightValue = weightValue;
	}
	
	public String getUnitName() {
		return unitName;
	}
	
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	
	public Integer getSortNo() {
		return sortNo;
	}
	
	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
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