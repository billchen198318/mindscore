package org.qifu.md.entity;

import java.util.Date;
import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdFormulaRecommendRule extends MdFormulaRecommendRuleKey {
	private static final long serialVersionUID = 2465492135978431256L;
	private String ruleName;
	private String managementMode;
	private String compareMode;
	private String periodType;
	private String dataType;
	private String recommendedFormulaOid;
	private Integer priorityNo;
	private String isDefault;
	private String enabled;
	private String description;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;

	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getManagementMode() {
		return managementMode;
	}
	public void setManagementMode(String managementMode) {
		this.managementMode = managementMode;
	}

	public String getCompareMode() {
		return compareMode;
	}
	public void setCompareMode(String compareMode) {
		this.compareMode = compareMode;
	}

	public String getPeriodType() {
		return periodType;
	}
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}

	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getRecommendedFormulaOid() {
		return recommendedFormulaOid;
	}
	public void setRecommendedFormulaOid(String recommendedFormulaOid) {
		this.recommendedFormulaOid = recommendedFormulaOid;
	}

	public Integer getPriorityNo() {
		return priorityNo;
	}
	public void setPriorityNo(Integer priorityNo) {
		this.priorityNo = priorityNo;
	}

	public String getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
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
