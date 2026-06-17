package org.qifu.md.model;

import java.util.ArrayList;
import java.util.List;

import org.qifu.md.entity.MdOkrInitiative;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.entity.MdOkrObjectiveOwner;

public class OkrObjectiveRequest implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private MdOkrObjective objective;
    private List<MdOkrObjectiveOwner> ownerList = new ArrayList<>();
    private List<MdOkrInitiative> initiativeList = new ArrayList<>();

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

    public List<MdOkrInitiative> getInitiativeList() {
        return initiativeList;
    }

    public void setInitiativeList(List<MdOkrInitiative> initiativeList) {
        this.initiativeList = initiativeList;
    }
}
