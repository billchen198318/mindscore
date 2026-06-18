package org.qifu.md.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.qifu.md.entity.MdStrategyObjective;

public class StrategyReportObjectiveView implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private MdStrategyObjective objective;
    private BigDecimal scoreValue;
    private int kpiCount;
    private int okrCount;
    private List<StrategyReportLinkView> linkList = new ArrayList<>();

    public MdStrategyObjective getObjective() {
        return objective;
    }

    public void setObjective(MdStrategyObjective objective) {
        this.objective = objective;
    }

    public BigDecimal getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(BigDecimal scoreValue) {
        this.scoreValue = scoreValue;
    }

    public int getKpiCount() {
        return kpiCount;
    }

    public void setKpiCount(int kpiCount) {
        this.kpiCount = kpiCount;
    }

    public int getOkrCount() {
        return okrCount;
    }

    public void setOkrCount(int okrCount) {
        this.okrCount = okrCount;
    }

    public List<StrategyReportLinkView> getLinkList() {
        return linkList;
    }

    public void setLinkList(List<StrategyReportLinkView> linkList) {
        this.linkList = linkList;
    }
}
