package org.qifu.md.model;

import java.util.ArrayList;
import java.util.List;

public class KpiMeasureDataImportRequest implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String sourceRef;
    private List<KpiMeasureDataImportRow> rows = new ArrayList<>();

    public String getSourceRef() { return sourceRef; }
    public void setSourceRef(String sourceRef) { this.sourceRef = sourceRef; }
    public List<KpiMeasureDataImportRow> getRows() { return rows; }
    public void setRows(List<KpiMeasureDataImportRow> rows) { this.rows = rows; }
}
