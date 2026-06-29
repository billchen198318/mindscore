package org.qifu.md.model;

public class InsightEvaluationResult implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private int signalCount;
    private int ruleCount;
    private int matchedCount;
    private int insertedCount;
    private int updatedCount;

    public int getSignalCount() { return signalCount; }
    public void setSignalCount(int signalCount) { this.signalCount = signalCount; }
    public int getRuleCount() { return ruleCount; }
    public void setRuleCount(int ruleCount) { this.ruleCount = ruleCount; }
    public int getMatchedCount() { return matchedCount; }
    public void setMatchedCount(int matchedCount) { this.matchedCount = matchedCount; }
    public int getInsertedCount() { return insertedCount; }
    public void setInsertedCount(int insertedCount) { this.insertedCount = insertedCount; }
    public int getUpdatedCount() { return updatedCount; }
    public void setUpdatedCount(int updatedCount) { this.updatedCount = updatedCount; }
}
