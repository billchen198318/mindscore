package org.qifu.md.logic.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.message.BaseSystemMessage;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.PleaseSelect;
import org.qifu.base.model.ServiceMethodAuthority;
import org.qifu.base.model.ServiceMethodType;
import org.qifu.base.model.YesNoKeyProvide;
import org.qifu.md.entity.MdFormula;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiMeasureData;
import org.qifu.md.entity.MdKpiScoreColor;
import org.qifu.md.entity.MdKpiScoreSnapshot;
import org.qifu.md.logic.IKpiMeasureDataLogicService;
import org.qifu.md.logic.IKpiScoreCalculationLogicService;
import org.qifu.md.service.IMdFormulaService;
import org.qifu.md.service.IMdKpiScoreColorService;
import org.qifu.md.service.IMdKpiScoreSnapshotService;
import org.qifu.md.service.IMdKpiService;
import org.qifu.md.util.KpiScoreCalculationUtils;
import org.qifu.md.util.KpiScoreCalculationUtils.CalculationResult;
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
    private final IMdKpiScoreColorService<MdKpiScoreColor, String> mdKpiScoreColorService;
    private final IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> mdKpiScoreSnapshotService;
    private final IKpiMeasureDataLogicService kpiMeasureDataLogicService;

    public KpiScoreCalculationLogicServiceImpl(IMdKpiService<MdKpi, String> mdKpiService,
            IMdFormulaService<MdFormula, String> mdFormulaService,
            IMdKpiScoreColorService<MdKpiScoreColor, String> mdKpiScoreColorService,
            IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> mdKpiScoreSnapshotService,
            IKpiMeasureDataLogicService kpiMeasureDataLogicService) {
        this.mdKpiService = mdKpiService;
        this.mdFormulaService = mdFormulaService;
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
