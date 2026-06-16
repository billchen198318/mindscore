package org.qifu.md.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.qifu.base.Constants;
import org.qifu.md.entity.MdKpiMeasureData;

public class KpiScoreRecalculationUtils {

    protected KpiScoreRecalculationUtils() {
        throw new IllegalStateException("Utils class: KpiScoreRecalculationUtils");
    }

    public static void validatePeriodCriteria(MdKpiMeasureData criteria) {
        if (criteria == null || StringUtils.isBlank(criteria.getPeriodType()) || StringUtils.isBlank(criteria.getPeriodKey())) {
            throw new IllegalArgumentException("Period type and period key are required.");
        }
    }

    public static Map<String, Object> toPeriodParams(MdKpiMeasureData criteria) {
        validatePeriodCriteria(criteria);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("periodType", StringUtils.trimToNull(criteria.getPeriodType()));
        params.put("periodKey", StringUtils.trimToNull(criteria.getPeriodKey()));
        putIfNotBlank(params, "kpiOid", criteria.getKpiOid());
        putIfNotBlank(params, "dataForType", criteria.getDataForType());
        putIfNotBlank(params, "account", criteria.getAccount());
        putIfNotBlank(params, "orgOid", criteria.getOrgOid());
        return params;
    }

    public static List<List<MdKpiMeasureData>> groupBySnapshotKey(List<MdKpiMeasureData> measureDataList) {
        Map<String, List<MdKpiMeasureData>> groups = new LinkedHashMap<>();
        if (CollectionUtils.isEmpty(measureDataList)) {
            return new ArrayList<>();
        }
        for (MdKpiMeasureData measureData : measureDataList) {
            if (measureData == null) {
                continue;
            }
            groups.computeIfAbsent(snapshotKey(measureData), key -> new ArrayList<>()).add(measureData);
        }
        return new ArrayList<>(groups.values());
    }

    private static void putIfNotBlank(Map<String, Object> params, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            params.put(key, StringUtils.trim(value));
        }
    }

    private static String snapshotKey(MdKpiMeasureData measureData) {
        return keyPart(measureData.getKpiOid())
                + Constants.ID_DELIMITER + keyPart(measureData.getPeriodType())
                + Constants.ID_DELIMITER + keyPart(measureData.getPeriodKey())
                + Constants.ID_DELIMITER + keyPart(measureData.getDataForType())
                + Constants.ID_DELIMITER + keyPart(measureData.getAccount())
                + Constants.ID_DELIMITER + keyPart(measureData.getOrgOid());
    }

    private static String keyPart(String value) {
        return StringUtils.defaultString(value);
    }
}
