package org.qifu.md.model;

import java.util.ArrayList;
import java.util.List;

public class KpiMeasureDataImportPreview implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private int totalCount;
    private int validCount;
    private int errorCount;
    private int insertCount;
    private int updateCount;
    private boolean canImport;
    private List<KpiMeasureDataImportRow> rows = new ArrayList<>();

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public int getValidCount() { return validCount; }
    public void setValidCount(int validCount) { this.validCount = validCount; }
    public int getErrorCount() { return errorCount; }
    public void setErrorCount(int errorCount) { this.errorCount = errorCount; }
    public int getInsertCount() { return insertCount; }
    public void setInsertCount(int insertCount) { this.insertCount = insertCount; }
    public int getUpdateCount() { return updateCount; }
    public void setUpdateCount(int updateCount) { this.updateCount = updateCount; }
    public boolean isCanImport() { return canImport; }
    public void setCanImport(boolean canImport) { this.canImport = canImport; }
    public List<KpiMeasureDataImportRow> getRows() { return rows; }
    public void setRows(List<KpiMeasureDataImportRow> rows) { this.rows = rows; }
}
