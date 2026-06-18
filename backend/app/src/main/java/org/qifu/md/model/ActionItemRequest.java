package org.qifu.md.model;

import java.util.ArrayList;
import java.util.List;

import org.qifu.md.entity.MdActionItem;
import org.qifu.md.entity.MdActionOwner;
import org.qifu.md.entity.MdActionSourceLink;

public class ActionItemRequest {

    private MdActionItem actionItem;
    private List<MdActionOwner> ownerList = new ArrayList<>();
    private List<MdActionSourceLink> sourceLinkList = new ArrayList<>();

    public MdActionItem getActionItem() {
        return actionItem;
    }

    public void setActionItem(MdActionItem actionItem) {
        this.actionItem = actionItem;
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
