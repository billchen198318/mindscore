package org.qifu.md.model;

import java.math.BigDecimal;
import java.util.Date;

import org.qifu.md.entity.MdStrategyObjectiveLink;

public class StrategyReportLinkView implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private MdStrategyObjectiveLink link;
    private String sourceCode;
    private String sourceName;
    private BigDecimal scoreValue;
    private String scoreStatus;
    private String dataForType;
    private String account;
    private String orgOid;
    private String calculationTrace;
    private Date calculatedAt;
    private boolean missingScore;

    public MdStrategyObjectiveLink getLink() {
        return link;
    }

    public void setLink(MdStrategyObjectiveLink link) {
        this.link = link;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
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

    public boolean isMissingScore() {
        return missingScore;
    }

    public void setMissingScore(boolean missingScore) {
        this.missingScore = missingScore;
    }
}
