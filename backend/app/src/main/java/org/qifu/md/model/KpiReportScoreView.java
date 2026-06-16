package org.qifu.md.model;

import java.math.BigDecimal;
import java.util.Date;

public class KpiReportScoreView implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String oid;
    private String kpiOid;
    private String kpiCode;
    private String kpiName;
    private String unitName;
    private String periodType;
    private String periodKey;
    private String dataForType;
    private String account;
    private String orgOid;
    private String ownerName;
    private BigDecimal rawTarget;
    private BigDecimal rawActual;
    private BigDecimal scoreValue;
    private String scoreStatus;
    private String colorName;
    private String fontColor;
    private String bgColor;
    private String formulaOid;
    private String formulaCode;
    private String formulaName;
    private Integer formulaVersionNo;
    private String aggrMethodOid;
    private String aggrCode;
    private String aggrName;
    private String aggrType;
    private String calculationTrace;
    private Date calculatedAt;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getKpiOid() {
        return kpiOid;
    }

    public void setKpiOid(String kpiOid) {
        this.kpiOid = kpiOid;
    }

    public String getKpiCode() {
        return kpiCode;
    }

    public void setKpiCode(String kpiCode) {
        this.kpiCode = kpiCode;
    }

    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public String getPeriodKey() {
        return periodKey;
    }

    public void setPeriodKey(String periodKey) {
        this.periodKey = periodKey;
    }

    public String getDataForType() {
        return dataForType;
    }

    public void setDataForType(String dataForType) {
        this.dataForType = dataForType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOrgOid() {
        return orgOid;
    }

    public void setOrgOid(String orgOid) {
        this.orgOid = orgOid;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

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

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getFormulaOid() {
        return formulaOid;
    }

    public void setFormulaOid(String formulaOid) {
        this.formulaOid = formulaOid;
    }

    public String getFormulaCode() {
        return formulaCode;
    }

    public void setFormulaCode(String formulaCode) {
        this.formulaCode = formulaCode;
    }

    public String getFormulaName() {
        return formulaName;
    }

    public void setFormulaName(String formulaName) {
        this.formulaName = formulaName;
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

    public String getAggrCode() {
        return aggrCode;
    }

    public void setAggrCode(String aggrCode) {
        this.aggrCode = aggrCode;
    }

    public String getAggrName() {
        return aggrName;
    }

    public void setAggrName(String aggrName) {
        this.aggrName = aggrName;
    }

    public String getAggrType() {
        return aggrType;
    }

    public void setAggrType(String aggrType) {
        this.aggrType = aggrType;
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
}
