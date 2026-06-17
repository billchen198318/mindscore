package org.qifu.md.model;

import java.util.ArrayList;
import java.util.List;

import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.entity.MdOkrObjectiveOwner;
import org.qifu.md.entity.MdOkrSnapshot;

public class OkrSnapshotDetail implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private MdOkrSnapshot snapshot;
    private MdOkrObjective objective;
    private List<MdOkrObjectiveOwner> ownerList = new ArrayList<>();
    private List<OkrSnapshotKeyResultDetail> keyResultDetailList = new ArrayList<>();

    public MdOkrSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(MdOkrSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public MdOkrObjective getObjective() {
        return objective;
    }

    public void setObjective(MdOkrObjective objective) {
        this.objective = objective;
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
}
