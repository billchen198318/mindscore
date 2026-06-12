package org.qifu.md.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiMeasureData;

public class AggregationContext implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private MdKpi kpi;
    private List<MdKpiMeasureData> measureDataList;
    private List<BigDecimal> scores;

    public MdKpi getKpi() {
        return kpi;
    }

    public void setKpi(MdKpi kpi) {
        this.kpi = kpi;
    }

    public List<MdKpiMeasureData> getMeasureDataList() {
        return measureDataList;
    }

    public void setMeasureDataList(List<MdKpiMeasureData> measureDataList) {
        this.measureDataList = measureDataList;
    }

    public List<BigDecimal> getScores() {
        if (CollectionUtils.isNotEmpty(scores)) {
            return scores;
        }
        List<BigDecimal> values = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(measureDataList)) {
            for (MdKpiMeasureData measureData : measureDataList) {
                if (measureData != null && measureData.getActualValue() != null) {
                    values.add(measureData.getActualValue());
                }
            }
        }
        return values;
    }

    public void setScores(List<BigDecimal> scores) {
        this.scores = scores;
    }
}
