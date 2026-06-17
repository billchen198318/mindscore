package org.qifu.md.model;

import java.util.ArrayList;
import java.util.List;

import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.entity.MdOkrObjectiveOwner;
import org.qifu.md.entity.MdOkrSnapshot;

public class OkrReportObjectiveView implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private MdOkrObjective objective;
    private MdOkrSnapshot snapshot;
    private List<MdOkrObjectiveOwner> ownerList = new ArrayList<>();
    private List<OkrSnapshotKeyResultDetail> keyResultDetailList = new ArrayList<>();
    private List<OkrReportObjectiveView> children = new ArrayList<>();

    public MdOkrObjective getObjective() {
        return objective;
    }

    public void setObjective(MdOkrObjective objective) {
        this.objective = objective;
    }

    public MdOkrSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(MdOkrSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public List<MdOkrObjectiveOwner> getOwnerList() {
        return ownerList;
    }

    public void setOwnerList(List<MdOkrObjectiveOwner> ownerList) {
        this.ownerList = ownerList;
    }

    public List<OkrSnapshotKeyResultDetail> getKeyResultDetailList() {
        return keyResultDetailList;
    }

    public void setKeyResultDetailList(List<OkrSnapshotKeyResultDetail> keyResultDetailList) {
        this.keyResultDetailList = keyResultDetailList;
    }

    public List<OkrReportObjectiveView> getChildren() {
        return children;
    }

    public void setChildren(List<OkrReportObjectiveView> children) {
        this.children = children;
    }
}
