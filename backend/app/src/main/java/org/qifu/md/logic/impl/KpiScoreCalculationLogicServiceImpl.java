package org.qifu.md.logic.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.message.BaseSystemMessage;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.PleaseSelect;
import org.qifu.base.model.ServiceMethodAuthority;
import org.qifu.base.model.ServiceMethodType;
import org.qifu.base.model.YesNoKeyProvide;
import org.qifu.md.entity.MdAggregationMethod;
import org.qifu.md.entity.MdFormula;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiMeasureData;
import org.qifu.md.entity.MdKpiScoreColor;
import org.qifu.md.entity.MdKpiScoreSnapshot;
import org.qifu.md.logic.IKpiMeasureDataLogicService;
import org.qifu.md.logic.IKpiScoreCalculationLogicService;
import org.qifu.md.service.IMdAggregationMethodService;
import org.qifu.md.service.IMdFormulaService;
import org.qifu.md.service.IMdKpiMeasureDataService;
import org.qifu.md.service.IMdKpiScoreColorService;
import org.qifu.md.service.IMdKpiScoreSnapshotService;
import org.qifu.md.service.IMdKpiService;
import org.qifu.md.util.KpiScoreCalculationUtils;
import org.qifu.md.util.KpiScoreCalculationUtils.CalculationResult;
import org.qifu.md.util.KpiScoreRecalculationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class KpiScoreCalculationLogicServiceImpl implements IKpiScoreCalculationLogicService {

    private static final String FORMULA_AUTO = "AUTO";
    private static final String SCOPE_GLOBAL = "GLOBAL";
    private static final String SCOPE_KPI = "KPI";

    private final IMdKpiService<MdKpi, String> mdKpiService;
    private final IMdFormulaService<MdFormula, String> mdFormulaService;
    private final IMdAggregationMethodService<MdAggregationMethod, String> mdAggregationMethodService;
    private final IMdKpiMeasureDataService<MdKpiMeasureData, String> mdKpiMeasureDataService;
    private final IMdKpiScoreColorService<MdKpiScoreColor, String> mdKpiScoreColorService;
    private final IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> mdKpiScoreSnapshotService;
    private final IKpiMeasureDataLogicService kpiMeasureDataLogicService;

    public KpiScoreCalculationLogicServiceImpl(IMdKpiService<MdKpi, String> mdKpiService,
            IMdFormulaService<MdFormula, String> mdFormulaService,
            IMdAggregationMethodService<MdAggregationMethod, String> mdAggregationMethodService,
            IMdKpiMeasureDataService<MdKpiMeasureData, String> mdKpiMeasureDataService,
            IMdKpiScoreColorService<MdKpiScoreColor, String> mdKpiScoreColorService,
            IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> mdKpiScoreSnapshotService,
            IKpiMeasureDataLogicService kpiMeasureDataLogicService) {
        this.mdKpiService = mdKpiService;
        this.mdFormulaService = mdFormulaService;
        this.mdAggregationMethodService = mdAggregationMethodService;
        this.mdKpiMeasureDataService = mdKpiMeasureDataService;
        this.mdKpiScoreColorService = mdKpiScoreColorService;
        this.mdKpiScoreSnapshotService = mdKpiScoreSnapshotService;
        this.kpiMeasureDataLogicService = kpiMeasureDataLogicService;
    }

    @ServiceMethodAuthority(type = ServiceMethodType.UPDATE)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<MdKpiScoreSnapshot> calculateCurrent(MdKpiMeasureData entity) throws ServiceException {
        MdKpiMeasureData measureData = this.kpiMeasureDataLogicService.loadByKey(entity).getValueEmptyThrowMessage();
        MdKpi kpi = loadKpi(measureData.getKpiOid());
        MdFormula formula = loadFormula(resolveFormulaOid(kpi));
        CalculationResult calculation = KpiScoreCalculationUtils.calculate(
                kpi,
                measureData,
                formula,
                loadColorRules(SCOPE_KPI, kpi.getOid()),
                loadColorRules(SCOPE_GLOBAL, SCOPE_GLOBAL));
        MdKpiScoreSnapshot snapshot = toSnapshot(kpi, measureData, formula, calculation);
        MdKpiScoreSnapshot saved = saveOrUpdate(snapshot);

        DefaultResult<MdKpiScoreSnapshot> result = new DefaultResult<>();
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(saved);
        return result;
    }

    @ServiceMethodAuthority(type = ServiceMethodType.UPDATE)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<List<MdKpiScoreSnapshot>> recalculateByPeriod(MdKpiMeasureData criteria) throws ServiceException {
        Map<String, Object> params;
        try {
            params = KpiScoreRecalculationUtils.toPeriodParams(criteria);
        } catch (IllegalArgumentException e) {
            throw new ServiceException(e.getMessage());
        }
        List<MdKpiMeasureData> measureDataList = this.mdKpiMeasureDataService
                .selectListByParams(params, "KPI_OID, DATA_FOR_TYPE, ACCOUNT, ORG_OID", "ASC")
                .getValue();

        List<MdKpiScoreSnapshot> savedSnapshots = new ArrayList<>();
        for (List<MdKpiMeasureData> group : KpiScoreRecalculationUtils.groupBySnapshotKey(measureDataList)) {
            if (CollectionUtils.isEmpty(group)) {
                continue;
            }
            MdKpiMeasureData firstMeasureData = group.get(0);
            MdKpi kpi = loadKpi(firstMeasureData.getKpiOid());
            MdFormula formula = loadFormula(resolveFormulaOid(kpi));
            CalculationResult calculation = calculateGroup(kpi, group, formula);
            MdKpiScoreSnapshot snapshot = toSnapshot(kpi, firstMeasureData, formula, calculation);
            savedSnapshots.add(saveOrUpdate(snapshot));
        }

        DefaultResult<List<MdKpiScoreSnapshot>> result = new DefaultResult<>();
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(savedSnapshots);
        return result;
    }

    private CalculationResult calculateGroup(MdKpi kpi, List<MdKpiMeasureData> measureDataList, MdFormula formula) throws ServiceException {
        List<MdKpiScoreColor> kpiColorRules = loadColorRules(SCOPE_KPI, kpi.getOid());
        List<MdKpiScoreColor> globalColorRules = loadColorRules(SCOPE_GLOBAL, SCOPE_GLOBAL);
        if (measureDataList.size() == 1) {
            return KpiScoreCalculationUtils.calculate(kpi, measureDataList.get(0), formula, kpiColorRules, globalColorRules);
        }
        MdAggregationMethod aggrMethod = loadAggregationMethod(kpi.getAggrMethodOid());
        return KpiScoreCalculationUtils.calculateAggregated(
                kpi,
                measureDataList,
                formula,
                aggrMethod == null ? null : aggrMethod.getAggrCode(),
                aggrMethod == null ? null : aggrMethod.getExpression(),
                kpiColorRules,
                globalColorRules);
    }

    private MdKpi loadKpi(String kpiOid) throws ServiceException {
        if (PleaseSelect.noSelect(kpiOid)) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        MdKpi key = new MdKpi();
        key.setOid(kpiOid);
        return this.mdKpiService.selectByEntityPrimaryKey(key).getValueEmptyThrowMessage();
    }

    private String resolveFormulaOid(MdKpi kpi) throws ServiceException {
        if (kpi == null) {
            throw new ServiceException("KPI cannot be null.");
        }
        if (FORMULA_AUTO.equals(kpi.getFormulaSelectionMode()) && StringUtils.isNotBlank(kpi.getRecommendedFormulaOid())) {
            return kpi.getRecommendedFormulaOid();
        }
        if (StringUtils.isNotBlank(kpi.getFormulaOid())) {
            return kpi.getFormulaOid();
        }
        throw new ServiceException("KPI formula is not configured.");
    }

    private MdFormula loadFormula(String formulaOid) throws ServiceException {
        if (PleaseSelect.noSelect(formulaOid)) {
            throw new ServiceException("KPI formula is not configured.");
        }
        MdFormula key = new MdFormula();
        key.setOid(formulaOid);
        return this.mdFormulaService.selectByEntityPrimaryKey(key).getValueEmptyThrowMessage();
    }

    private MdAggregationMethod loadAggregationMethod(String aggrMethodOid) throws ServiceException {
        if (PleaseSelect.noSelect(aggrMethodOid)) {
            return null;
        }
        MdAggregationMethod key = new MdAggregationMethod();
        key.setOid(aggrMethodOid);
        return this.mdAggregationMethodService.selectByEntityPrimaryKey(key).getValue();
    }

    private List<MdKpiScoreColor> loadColorRules(String scopeType, String scopeKey) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("enabled", YesNoKeyProvide.YES);
        params.put("scopeType", scopeType);
        params.put("scopeKey", scopeKey);
        return this.mdKpiScoreColorService.selectListByParams(params, "SORT_NO, SCORE_MIN", "ASC").getValue();
    }

    private MdKpiScoreSnapshot toSnapshot(MdKpi kpi, MdKpiMeasureData measureData, MdFormula formula, CalculationResult calculation) {
        MdKpiScoreSnapshot snapshot = new MdKpiScoreSnapshot();
        snapshot.setKpiOid(kpi.getOid());
        snapshot.setPeriodType(measureData.getPeriodType());
        snapshot.setPeriodKey(measureData.getPeriodKey());
        snapshot.setDataForType(measureData.getDataForType());
        snapshot.setAccount(measureData.getAccount());
        snapshot.setOrgOid(measureData.getOrgOid());
        snapshot.setRawTarget(calculation.getRawTarget());
        snapshot.setRawActual(calculation.getRawActual());
        snapshot.setScoreValue(calculation.getScoreValue());
        snapshot.setScoreStatus(calculation.getScoreStatus());
        snapshot.setFormulaOid(formula.getOid());
        snapshot.setFormulaVersionNo(formula.getVersionNo());
        snapshot.setAggrMethodOid(kpi.getAggrMethodOid());
        snapshot.setCalculationTrace(calculation.getCalculationTrace());
        snapshot.setCalculatedAt(new Date());
        return snapshot;
    }

    private MdKpiScoreSnapshot saveOrUpdate(MdKpiScoreSnapshot snapshot) throws ServiceException {
        MdKpiScoreSnapshot existing = loadSnapshotByKey(snapshot);
        if (existing == null) {
            return this.mdKpiScoreSnapshotService.insert(snapshot).getValueEmptyThrowMessage();
        }
        snapshot.setOid(existing.getOid());
        this.mdKpiScoreSnapshotService.update(snapshot).getValueEmptyThrowMessage();
        return this.mdKpiScoreSnapshotService.selectByEntityPrimaryKey(snapshot).getValueEmptyThrowMessage();
    }

    private MdKpiScoreSnapshot loadSnapshotByKey(MdKpiScoreSnapshot snapshot) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("kpiOid", snapshot.getKpiOid());
        params.put("periodType", snapshot.getPeriodType());
        params.put("periodKey", snapshot.getPeriodKey());
        params.put("dataForType", snapshot.getDataForType());
        if (StringUtils.isNotBlank(snapshot.getAccount())) {
            params.put("account", snapshot.getAccount());
        }
        if (StringUtils.isNotBlank(snapshot.getOrgOid())) {
            params.put("orgOid", snapshot.getOrgOid());
        }
        List<MdKpiScoreSnapshot> snapshots = this.mdKpiScoreSnapshotService.selectListByParams(params).getValue();
        return snapshots == null || snapshots.isEmpty() ? null : snapshots.get(0);
    }
}
