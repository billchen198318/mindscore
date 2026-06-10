package org.qifu.md.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdStrategyObjective extends MdStrategyObjectiveKey {
	private static final long serialVersionUID = 1L;
	
	private String objectiveName;
	private BigDecimal weightValue;
	private Integer sortNo;
	private String description;
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
	
	public BigDecimal getWeightValue() {
		return weightValue;
	}
	
	public void setWeightValue(BigDecimal weightValue) {
		this.weightValue = weightValue;
	}
	
	public Integer getSortNo() {
		return sortNo;
	}
	
	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
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