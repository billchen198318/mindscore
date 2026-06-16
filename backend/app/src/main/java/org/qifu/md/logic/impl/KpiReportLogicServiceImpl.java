package org.qifu.md.logic.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.YesNoKeyProvide;
import org.qifu.md.entity.MdAggregationMethod;
import org.qifu.md.entity.MdFormula;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiScoreColor;
import org.qifu.md.entity.MdKpiScoreSnapshot;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.logic.IKpiReportLogicService;
import org.qifu.md.model.KpiReportQueryRequest;
import org.qifu.md.model.KpiReportScoreView;
import org.qifu.md.model.KpiReportSummary;
import org.qifu.md.service.IMdKpiScoreColorService;
import org.qifu.md.service.IMdKpiScoreSnapshotService;
import org.qifu.md.service.IMdKpiService;
import org.qifu.md.service.IMdAggregationMethodService;
import org.qifu.md.service.IMdFormulaService;
import org.qifu.md.service.IMdOrgMemberService;
import org.qifu.md.service.IMdOrgUnitService;
import org.qifu.md.util.KpiScoreCalculationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class KpiReportLogicServiceImpl implements IKpiReportLogicService {

    private static final String SCOPE_GLOBAL = "GLOBAL";
    private static final String SCOPE_KPI = "KPI";

    private final IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> mdKpiScoreSnapshotService;
    private final IMdKpiService<MdKpi, String> mdKpiService;
    private final IMdFormulaService<MdFormula, String> mdFormulaService;
    private final IMdAggregationMethodService<MdAggregationMethod, String> mdAggregationMethodService;
    private final IMdKpiScoreColorService<MdKpiScoreColor, String> mdKpiScoreColorService;
    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;
    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;

    public KpiReportLogicServiceImpl(IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> mdKpiScoreSnapshotService,
            IMdKpiService<MdKpi, String> mdKpiService,
            IMdFormulaService<MdFormula, String> mdFormulaService,
            IMdAggregationMethodService<MdAggregationMethod, String> mdAggregationMethodService,
            IMdKpiScoreColorService<MdKpiScoreColor, String> mdKpiScoreColorService,
            IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService,
            IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService) {
        this.mdKpiScoreSnapshotService = mdKpiScoreSnapshotService;
        this.mdKpiService = mdKpiService;
        this.mdFormulaService = mdFormulaService;
        this.mdAggregationMethodService = mdAggregationMethodService;
        this.mdKpiScoreColorService = mdKpiScoreColorService;
        this.mdOrgUnitService = mdOrgUnitService;
        this.mdOrgMemberService = mdOrgMemberService;
    }

    @Override
    public DefaultResult<List<KpiReportScoreView>> enrich(List<MdKpiScoreSnapshot> snapshots) throws ServiceException {
        DefaultResult<List<KpiReportScoreView>> result = new DefaultResult<>();
        List<KpiReportScoreView> views = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(snapshots)) {
            for (MdKpiScoreSnapshot snapshot : snapshots) {
                views.add(toView(snapshot));
            }
        }
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(views);
        return result;
    }

    @Override
    public DefaultResult<List<KpiReportScoreView>> trend(KpiReportQueryRequest request) throws ServiceException {
        List<MdKpiScoreSnapshot> snapshots = loadSnapshots(request);
        return enrich(limit(filterRange(snapshots, request), request == null ? null : request.getLimit()));
    }

    @Override
    public DefaultResult<List<KpiReportScoreView>> targetActual(KpiReportQueryRequest request) throws ServiceException {
        List<MdKpiScoreSnapshot> snapshots = loadSnapshots(request);
        return enrich(limit(filterRange(snapshots, request), request == null ? null : request.getLimit()));
    }

    @Override
    public DefaultResult<KpiReportSummary> summary(KpiReportQueryRequest request) throws ServiceException {
        List<MdKpiScoreSnapshot> snapshots = filterRange(loadSnapshots(request), request);
        KpiReportSummary summary = new KpiReportSummary();
        summary.setKpiCount(snapshots.size());
        summary.setGoodCount(0);
        summary.setWarningCount(0);
        summary.setBadCount(0);
        summary.setUnknownCount(0);

        BigDecimal totalScore = BigDecimal.ZERO;
        int scoreCount = 0;
        for (MdKpiScoreSnapshot snapshot : snapshots) {
            if (snapshot.getScoreValue() != null) {
                totalScore = totalScore.add(snapshot.getScoreValue());
                scoreCount++;
            }
            String status = StringUtils.defaultIfBlank(snapshot.getScoreStatus(), KpiScoreCalculationUtils.STATUS_UNKNOWN);
            if ("GOOD".equals(status)) {
                summary.setGoodCount(summary.getGoodCount() + 1);
            } else if ("WARNING".equals(status)) {
                summary.setWarningCount(summary.getWarningCount() + 1);
            } else if ("BAD".equals(status)) {
                summary.setBadCount(summary.getBadCount() + 1);
            } else {
                summary.setUnknownCount(summary.getUnknownCount() + 1);
            }
        }
        summary.setAvgScore(scoreCount == 0 ? BigDecimal.ZERO : totalScore.divide(BigDecimal.valueOf(scoreCount), 4, RoundingMode.HALF_UP));

        DefaultResult<KpiReportSummary> result = new DefaultResult<>();
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(summary);
        return result;
    }

    private List<MdKpiScoreSnapshot> loadSnapshots(KpiReportQueryRequest request) throws ServiceException {
        Map<String, Object> params = toParams(request);
        if (params.isEmpty()) {
            return this.mdKpiScoreSnapshotService.selectList("PERIOD_TYPE, PERIOD_KEY, KPI_OID", "ASC").getValue();
        }
        return this.mdKpiScoreSnapshotService.selectListByParams(params, "PERIOD_TYPE, PERIOD_KEY, KPI_OID", "ASC").getValue();
    }

    private Map<String, Object> toParams(KpiReportQueryRequest request) {
        Map<String, Object> params = new HashMap<>();
        if (request == null) {
            return params;
        }
        putIfNotBlank(params, "kpiOid", request.getKpiOid());
        putIfNotBlank(params, "periodType", request.getPeriodType());
        putIfNotBlank(params, "periodKey", request.getPeriodKey());
        putIfNotBlank(params, "dataForType", request.getDataForType());
        putIfNotBlank(params, "account", request.getAccount());
        putIfNotBlank(params, "orgOid", request.getOrgOid());
        return params;
    }

    private void putIfNotBlank(Map<String, Object> params, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            params.put(key, StringUtils.trim(value));
        }
    }

    private List<MdKpiScoreSnapshot> filterRange(List<MdKpiScoreSnapshot> snapshots, KpiReportQueryRequest request) {
        if (CollectionUtils.isEmpty(snapshots) || request == null
                || (StringUtils.isBlank(request.getPeriodKeyFrom()) && StringUtils.isBlank(request.getPeriodKeyTo()))) {
            return snapshots == null ? new ArrayList<>() : snapshots;
        }
        List<MdKpiScoreSnapshot> filtered = new ArrayList<>();
        for (MdKpiScoreSnapshot snapshot : snapshots) {
            String periodKey = snapshot.getPeriodKey();
            if (StringUtils.isBlank(periodKey)) {
                continue;
            }
            if (StringUtils.isNotBlank(request.getPeriodKeyFrom()) && periodKey.compareTo(request.getPeriodKeyFrom()) < 0) {
                continue;
            }
            if (StringUtils.isNotBlank(request.getPeriodKeyTo()) && periodKey.compareTo(request.getPeriodKeyTo()) > 0) {
                continue;
            }
            filtered.add(snapshot);
        }
        return filtered;
    }

    private List<MdKpiScoreSnapshot> limit(List<MdKpiScoreSnapshot> snapshots, Integer limit) {
        if (CollectionUtils.isEmpty(snapshots) || limit == null || limit < 1 || snapshots.size() <= limit) {
            return snapshots == null ? new ArrayList<>() : snapshots;
        }
        return new ArrayList<>(snapshots.subList(Math.max(0, snapshots.size() - limit), snapshots.size()));
    }

    private KpiReportScoreView toView(MdKpiScoreSnapshot snapshot) throws ServiceException {
        KpiReportScoreView view = new KpiReportScoreView();
        view.setOid(snapshot.getOid());
        view.setKpiOid(snapshot.getKpiOid());
        view.setPeriodType(snapshot.getPeriodType());
        view.setPeriodKey(snapshot.getPeriodKey());
        view.setDataForType(snapshot.getDataForType());
        view.setAccount(snapshot.getAccount());
        view.setOrgOid(snapshot.getOrgOid());
        view.setRawTarget(snapshot.getRawTarget());
        view.setRawActual(snapshot.getRawActual());
        view.setScoreValue(snapshot.getScoreValue());
        view.setScoreStatus(snapshot.getScoreStatus());
        view.setFormulaOid(snapshot.getFormulaOid());
        view.setFormulaVersionNo(snapshot.getFormulaVersionNo());
        view.setAggrMethodOid(snapshot.getAggrMethodOid());
        view.setCalculationTrace(snapshot.getCalculationTrace());
        view.setCalculatedAt(snapshot.getCalculatedAt());

        MdKpi kpi = loadKpi(snapshot.getKpiOid());
        if (kpi != null) {
            view.setKpiCode(kpi.getKpiCode());
            view.setKpiName(kpi.getKpiName());
            view.setUnitName(kpi.getUnitName());
        }
        applyFormulaInfo(view, snapshot.getFormulaOid());
        applyAggregationInfo(view, snapshot.getAggrMethodOid());
        view.setOwnerName(resolveOwnerName(snapshot));

        MdKpiScoreColor color = resolveColor(snapshot);
        if (color != null) {
            view.setColorName(color.getColorName());
            view.setFontColor(color.getFontColor());
            view.setBgColor(color.getBgColor());
        }
        return view;
    }

    private void applyFormulaInfo(KpiReportScoreView view, String formulaOid) throws ServiceException {
        if (StringUtils.isBlank(formulaOid)) {
            return;
        }
        MdFormula key = new MdFormula();
        key.setOid(formulaOid);
        MdFormula formula = this.mdFormulaService.selectByEntityPrimaryKey(key).getValue();
        if (formula == null) {
            return;
        }
        view.setFormulaCode(formula.getFormulaCode());
        view.setFormulaName(formula.getFormulaName());
        view.setFormulaVersionNo(formula.getVersionNo());
    }

    private void applyAggregationInfo(KpiReportScoreView view, String aggrMethodOid) throws ServiceException {
        if (StringUtils.isBlank(aggrMethodOid)) {
            return;
        }
        MdAggregationMethod key = new MdAggregationMethod();
        key.setOid(aggrMethodOid);
        MdAggregationMethod aggrMethod = this.mdAggregationMethodService.selectByEntityPrimaryKey(key).getValue();
        if (aggrMethod == null) {
            return;
        }
        view.setAggrCode(aggrMethod.getAggrCode());
        view.setAggrName(aggrMethod.getAggrName());
        view.setAggrType(aggrMethod.getAggrType());
    }

    private MdKpi loadKpi(String kpiOid) throws ServiceException {
        if (StringUtils.isBlank(kpiOid)) {
            return null;
        }
        MdKpi key = new MdKpi();
        key.setOid(kpiOid);
        return this.mdKpiService.selectByEntityPrimaryKey(key).getValue();
    }

    private String resolveOwnerName(MdKpiScoreSnapshot snapshot) throws ServiceException {
        if ("ORG".equals(snapshot.getDataForType()) && StringUtils.isNotBlank(snapshot.getOrgOid())) {
            MdOrgUnit key = new MdOrgUnit();
            key.setOid(snapshot.getOrgOid());
            MdOrgUnit org = this.mdOrgUnitService.selectByEntityPrimaryKey(key).getValue();
            return org == null ? snapshot.getOrgOid() : org.getOrgCode() + " - " + org.getOrgName();
        }
        if ("ACCOUNT".equals(snapshot.getDataForType()) && StringUtils.isNotBlank(snapshot.getAccount())) {
            Map<String, Object> params = new HashMap<>();
            params.put("account", snapshot.getAccount());
            List<MdOrgMember> members = this.mdOrgMemberService.selectListByParams(params).getValue();
            if (CollectionUtils.isNotEmpty(members)) {
                MdOrgMember member = members.get(0);
                return member.getAccount() + (StringUtils.isBlank(member.getDisplayName()) ? "" : " - " + member.getDisplayName());
            }
            return snapshot.getAccount();
        }
        return "Global";
    }

    private MdKpiScoreColor resolveColor(MdKpiScoreSnapshot snapshot) throws ServiceException {
        List<MdKpiScoreColor> rules = new ArrayList<>();
        if (StringUtils.isNotBlank(snapshot.getKpiOid())) {
            rules.addAll(loadColorRules(SCOPE_KPI, snapshot.getKpiOid(), snapshot.getScoreStatus()));
        }
        rules.addAll(loadColorRules(SCOPE_GLOBAL, SCOPE_GLOBAL, snapshot.getScoreStatus()));
        return CollectionUtils.isEmpty(rules) ? null : rules.get(0);
    }

    private List<MdKpiScoreColor> loadColorRules(String scopeType, String scopeKey, String scoreStatus) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("enabled", YesNoKeyProvide.YES);
        params.put("scopeType", scopeType);
        params.put("scopeKey", scopeKey);
        if (StringUtils.isNotBlank(scoreStatus)) {
            params.put("scoreStatus", scoreStatus);
        }
        return this.mdKpiScoreColorService.selectListByParams(params, "SORT_NO, SCORE_MIN", "ASC").getValue();
    }
}
