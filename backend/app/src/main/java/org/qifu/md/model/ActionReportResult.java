package org.qifu.md.model;

import java.util.ArrayList;
import java.util.List;

public class ActionReportResult implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private ActionReportSummary summary = new ActionReportSummary();
    private List<ActionReportRow> rows = new ArrayList<>();

    public ActionReportSummary getSummary() {
        return summary;
    }

    public void setSummary(ActionReportSummary summary) {
        this.summary = summary;
    }

    public List<ActionReportRow> getRows() {
        return rows;
    }

    public void setRows(List<ActionReportRow> rows) {
        this.rows = rows;
    }
}
