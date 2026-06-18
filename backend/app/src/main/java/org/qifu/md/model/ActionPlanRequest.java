package org.qifu.md.model;

import java.util.ArrayList;
import java.util.List;

import org.qifu.md.entity.MdActionOwner;
import org.qifu.md.entity.MdActionPlan;
import org.qifu.md.entity.MdActionSourceLink;

public class ActionPlanRequest {

    private MdActionPlan actionPlan;
    private List<MdActionOwner> ownerList = new ArrayList<>();
    private List<MdActionSourceLink> sourceLinkList = new ArrayList<>();

    public MdActionPlan getActionPlan() {
        return actionPlan;
    }

    public void setActionPlan(MdActionPlan actionPlan) {
        this.actionPlan = actionPlan;
    }

    public List<MdActionOwner> getOwnerList() {
        return ownerList;
    }

    public void setOwnerList(List<MdActionOwner> ownerList) {
        this.ownerList = ownerList;
    }

    public List<MdActionSourceLink> getSourceLinkList() {
        return sourceLinkList;
    }

    public void setSourceLinkList(List<MdActionSourceLink> sourceLinkList) {
        this.sourceLinkList = sourceLinkList;
    }
}
