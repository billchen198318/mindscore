package org.qifu.md.model;

import java.util.ArrayList;
import java.util.List;

import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiOwner;

public class KpiMasterRequest implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private MdKpi kpi;
    private List<MdKpiOwner> ownerList = new ArrayList<>();

    public MdKpi getKpi() {
        return kpi;
    }

    public void setKpi(MdKpi kpi) {
        this.kpi = kpi;
    }

    public List<MdKpiOwner> getOwnerList() {
        return ownerList;
    }

    public void setOwnerList(List<MdKpiOwner> ownerList) {
        this.ownerList = ownerList;
    }
}
