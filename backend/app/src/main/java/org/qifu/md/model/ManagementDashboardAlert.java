package org.qifu.md.model;

import java.math.BigDecimal;

public class ManagementDashboardAlert implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String domain;
    private String severity;
    private String title;
    private String summary;
    private String periodKey;
    private BigDecimal scoreValue;
    private String sourceOid;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPeriodKey() {
        return periodKey;
    }

    public void setPeriodKey(String periodKey) {
        this.periodKey = periodKey;
    }

    public BigDecimal getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(BigDecimal scoreValue) {
        this.scoreValue = scoreValue;
    }

    public String getSourceOid() {
        return sourceOid;
    }

    public void setSourceOid(String sourceOid) {
        this.sourceOid = sourceOid;
    }
}
