package org.qifu.md.model;

import java.math.BigDecimal;

import org.qifu.md.entity.MdStrategyObjectiveLink;

public class StrategyReportLinkView implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private MdStrategyObjectiveLink link;
    private String sourceCode;
    private String sourceName;
    private BigDecimal scoreValue;
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

    public boolean isMissingScore() {
        return missingScore;
    }

    public void setMissingScore(boolean missingScore) {
        this.missingScore = missingScore;
    }
}
