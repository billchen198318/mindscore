package org.qifu.md.model;

public class KpiMeasureDataImportResult implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private int totalCount;
    private int insertCount;
    private int updateCount;

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public int getInsertCount() { return insertCount; }
    public void setInsertCount(int insertCount) { this.insertCount = insertCount; }
    public int getUpdateCount() { return updateCount; }
    public void setUpdateCount(int updateCount) { this.updateCount = updateCount; }
}
