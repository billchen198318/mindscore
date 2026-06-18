package org.qifu.md.model;

import java.util.ArrayList;
import java.util.List;

import org.qifu.md.entity.MdStrategySnapshot;
import org.qifu.md.entity.MdStrategyWorkspace;

public class StrategyReportResult implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private MdStrategyWorkspace workspace;
    private MdStrategySnapshot snapshot;
    private List<StrategyReportThemeView> themeList = new ArrayList<>();

    public MdStrategyWorkspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(MdStrategyWorkspace workspace) {
        this.workspace = workspace;
    }

    public MdStrategySnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(MdStrategySnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public List<StrategyReportThemeView> getThemeList() {
        return themeList;
    }

    public void setThemeList(List<StrategyReportThemeView> themeList) {
        this.themeList = themeList;
    }
}
