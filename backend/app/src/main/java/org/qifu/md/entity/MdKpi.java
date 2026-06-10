package org.qifu.md.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdKpi extends MdKpiKey {
	private static final long serialVersionUID = 1L;
	
	private String kpiName;
	private String description;
	private String unitName;
	private String dataType;
	private String periodType;
	private String managementMode;
	private String compareMode;
	private BigDecimal minValue;
	private BigDecimal targetValue;
	private BigDecimal maxValue;
	private BigDecimal quasiRange;
	private String scoreCapMode;
	private String scoringPolicy;
	private String formulaOid;
	private String recommendedFormulaOid;
	private String formulaSelectionMode;
	private String aggrMethodOid;
	private Integer formulaVersionNo;
	private BigDecimal weightValue;
	private String enabled;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;
	
	public String getKpiName() {
		return kpiName;
	}
	
	public void setKpiName(String kpiName) {
		this.kpiName = kpiName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getUnitName() {
		return unitName;
	}
	
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	
	public String getDataType() {
		return dataType;
	}
	
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public String getPeriodType() {
		return periodType;
	}
	
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
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
	
	public BigDecimal getMinValue() {
		return minValue;
	}
	
	public void setMinValue(BigDecimal minValue) {
		this.minValue = minValue;
	}
	
	public BigDecimal getTargetValue() {
		return targetValue;
	}
	
	public void setTargetValue(BigDecimal targetValue) {
		this.targetValue = targetValue;
	}
	
	public BigDecimal getMaxValue() {
		return maxValue;
	}
	
	public void setMaxValue(BigDecimal maxValue) {
		this.maxValue = maxValue;
	}
	
	public BigDecimal getQuasiRange() {
		return quasiRange;
	}
	
	public void setQuasiRange(BigDecimal quasiRange) {
		this.quasiRange = quasiRange;
	}
	
	public String getScoreCapMode() {
		return scoreCapMode;
	}
	
	public void setScoreCapMode(String scoreCapMode) {
		this.scoreCapMode = scoreCapMode;
	}
	
	public String getScoringPolicy() {
		return scoringPolicy;
	}
	
	public void setScoringPolicy(String scoringPolicy) {
		this.scoringPolicy = scoringPolicy;
	}
	
	public String getFormulaOid() {
		return formulaOid;
	}
	
	public void setFormulaOid(String formulaOid) {
		this.formulaOid = formulaOid;
	}
	
	public String getRecommendedFormulaOid() {
		return recommendedFormulaOid;
	}
	
	public void setRecommendedFormulaOid(String recommendedFormulaOid) {
		this.recommendedFormulaOid = recommendedFormulaOid;
	}
	
	public String getFormulaSelectionMode() {
		return formulaSelectionMode;
	}
	
	public void setFormulaSelectionMode(String formulaSelectionMode) {
		this.formulaSelectionMode = formulaSelectionMode;
	}
	
	public String getAggrMethodOid() {
		return aggrMethodOid;
	}
	
	public void setAggrMethodOid(String aggrMethodOid) {
		this.aggrMethodOid = aggrMethodOid;
	}
	
	public Integer getFormulaVersionNo() {
		return formulaVersionNo;
	}
	
	public void setFormulaVersionNo(Integer formulaVersionNo) {
		this.formulaVersionNo = formulaVersionNo;
	}
	
	public BigDecimal getWeightValue() {
		return weightValue;
	}
	
	public void setWeightValue(BigDecimal weightValue) {
		this.weightValue = weightValue;
	}
	
	public String getEnabled() {
		return enabled;
	}
	
	public void setEnabled(String enabled) {
		this.enabled = enabled;
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