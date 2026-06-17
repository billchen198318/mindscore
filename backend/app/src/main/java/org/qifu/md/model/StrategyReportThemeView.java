package org.qifu.md.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.qifu.md.entity.MdStrategyTheme;

public class StrategyReportThemeView implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private MdStrategyTheme theme;
    private BigDecimal scoreValue;
    private int objectiveCount;
    private List<StrategyReportObjectiveView> objectiveList = new ArrayList<>();

    public MdStrategyTheme getTheme() {
        return theme;
    }

    public void setTheme(MdStrategyTheme theme) {
        this.theme = theme;
    }

    public BigDecimal getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(BigDecimal scoreValue) {
        this.scoreValue = scoreValue;
    }

    public int getObjectiveCount() {
        return objectiveCount;
    }

    public void setObjectiveCount(int objectiveCount) {
        this.objectiveCount = objectiveCount;
    }

    public List<StrategyReportObjectiveView> getObjectiveList() {
        return objectiveList;
    }

    public void setObjectiveList(List<StrategyReportObjectiveView> objectiveList) {
        this.objectiveList = objectiveList;
    }
}
