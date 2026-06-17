package org.qifu.md.model;

import java.util.ArrayList;
import java.util.List;

import org.qifu.md.entity.MdOkrCheckin;
import org.qifu.md.entity.MdOkrKeyResult;

public class OkrSnapshotKeyResultDetail implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private MdOkrKeyResult keyResult;
    private List<MdOkrCheckin> checkinList = new ArrayList<>();

    public MdOkrKeyResult getKeyResult() {
        return keyResult;
    }

    public void setKeyResult(MdOkrKeyResult keyResult) {
        this.keyResult = keyResult;
    }

    public List<MdOkrCheckin> getCheckinList() {
        return checkinList;
    }

    public void setCheckinList(List<MdOkrCheckin> checkinList) {
        this.checkinList = checkinList;
    }
}
