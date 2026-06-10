package org.qifu.md.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdKpiMeasureData extends MdKpiMeasureDataKey {
	private static final long serialVersionUID = 1L;
	
	private Date measureDate;
	private BigDecimal targetValue;
	private BigDecimal actualValue;
	private BigDecimal minValue;
	private BigDecimal maxValue;
	private String sourceType;
	private String sourceRef;
	private String evidenceText;
	private String locked;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;
	
	public Date getMeasureDate() {
		return measureDate;
	}
	
	public void setMeasureDate(Date measureDate) {
		this.measureDate = measureDate;
	}
	
	public BigDecimal getTargetValue() {
		return targetValue;
	}
	
	public void setTargetValue(BigDecimal targetValue) {
		this.targetValue = targetValue;
	}
	
	public BigDecimal getActualValue() {
		return actualValue;
	}
	
	public void setActualValue(BigDecimal actualValue) {
		this.actualValue = actualValue;
	}
	
	public BigDecimal getMinValue() {
		return minValue;
	}
	
	public void setMinValue(BigDecimal minValue) {
		this.minValue = minValue;
	}
	
	public BigDecimal getMaxValue() {
		return maxValue;
	}
	
	public void setMaxValue(BigDecimal maxValue) {
		this.maxValue = maxValue;
	}
	
	public String getSourceType() {
		return sourceType;
	}
	
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	
	public String getSourceRef() {
		return sourceRef;
	}
	
	public void setSourceRef(String sourceRef) {
		this.sourceRef = sourceRef;
	}
	
	public String getEvidenceText() {
		return evidenceText;
	}
	
	public void setEvidenceText(String evidenceText) {
		this.evidenceText = evidenceText;
	}
	
	public String getLocked() {
		return locked;
	}
	
	public void setLocked(String locked) {
		this.locked = locked;
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