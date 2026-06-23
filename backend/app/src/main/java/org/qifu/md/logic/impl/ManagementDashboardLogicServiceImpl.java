package org.qifu.md.logic.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.ServiceAuthority;
import org.qifu.base.model.YesNoKeyProvide;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiScoreSnapshot;
import org.qifu.md.entity.MdOkrInitiative;
import org.qifu.md.entity.MdOkrKeyResult;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.entity.MdOkrSnapshot;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.entity.MdStrategyObjective;
import org.qifu.md.entity.MdStrategySnapshot;
import org.qifu.md.entity.MdStrategyTheme;
import org.qifu.md.entity.MdStrategyWorkspace;
import org.qifu.md.logic.IActionReportLogicService;
import org.qifu.md.logic.IKpiReportLogicService;
import org.qifu.md.logic.IManagementDashboardLogicService;
import org.qifu.md.model.ActionReportQuery;
import org.qifu.md.model.ActionReportResult;
import org.qifu.md.model.ActionReportRow;
import org.qifu.md.model.ActionReportSummary;
import org.qifu.md.model.KpiReportQueryRequest;
import org.qifu.md.model.KpiReportSummary;
import org.qifu.md.model.ManagementDashboardAlert;
import org.qifu.md.model.ManagementDashboardAtRiskObjectiveRow;
import org.qifu.md.model.ManagementDashboardDomainSummary;
import org.qifu.md.model.ManagementDashboardOrgSummary;
import org.qifu.md.model.ManagementDashboardQuery;
import org.qifu.md.model.ManagementDashboardResult;
import org.qifu.md.model.ManagementDashboardStrategyScorecardRow;
import org.qifu.md.service.IMdKpiScoreSnapshotService;
import org.qifu.md.service.IMdKpiService;
import org.qifu.md.service.IMdOkrInitiativeService;
import org.qifu.md.service.IMdOkrKeyResultService;
import org.qifu.md.service.IMdOkrObjectiveService;
import org.qifu.md.service.IMdOkrSnapshotService;
import org.qifu.md.service.IMdOrgUnitService;
import org.qifu.md.service.IMdStrategyObjectiveService;
import org.qifu.md.service.IMdStrategySnapshotService;
import org.qifu.md.service.IMdStrategyThemeService;
import org.qifu.md.service.IMdStrategyWorkspaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@ServiceAuthority(check = true)
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class ManagementDashboardLogicServiceImpl implements IManagementDashboardLogicService {

    private static final BigDecimal STRATEGY_WARNING_SCORE = new BigDecimal("70");
    private static final BigDecimal STRATEGY_BAD_SCORE = new BigDecimal("60");
    private static final int ALERT_LIMIT = 30;

    private final IKpiReportLogicService kpiReportLogicService;
    private final IActionReportLogicService actionReportLogicService;
    private final IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> mdKpiScoreSnapshotService;
    private final IMdKpiService<MdKpi, String> mdKpiService;
    private final IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService;
    private final IMdOkrKeyResultService<MdOkrKeyResult, String> mdOkrKeyResultService;
    private final IMdOkrInitiativeService<MdOkrInitiative, String> mdOkrInitiativeService;
    private final IMdOkrSnapshotService<MdOkrSnapshot, String> mdOkrSnapshotService;
    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;
    private final IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService;
    private final IMdStrategyThemeService<MdStrategyTheme, String> mdStrategyThemeService;
    private final IMdStrategyObjectiveService<MdStrategyObjective, String> mdStrategyObjectiveService;
    private final IMdStrategySnapshotService<MdStrategySnapshot, String> mdStrategySnapshotService;

    public ManagementDashboardLogicServiceImpl(IKpiReportLogicService kpiReportLogicService,
            IActionReportLogicService actionReportLogicService,
            IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> mdKpiScoreSnapshotService,
            IMdKpiService<MdKpi, String> mdKpiService,
            IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService,
            IMdOkrKeyResultService<MdOkrKeyResult, String> mdOkrKeyResultService,
            IMdOkrInitiativeService<MdOkrInitiative, String> mdOkrInitiativeService,
            IMdOkrSnapshotService<MdOkrSnapshot, String> mdOkrSnapshotService,
            IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService,
            IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService,
            IMdStrategyThemeService<MdStrategyTheme, String> mdStrategyThemeService,
            IMdStrategyObjectiveService<MdStrategyObjective, String> mdStrategyObjectiveService,
            IMdStrategySnapshotService<MdStrategySnapshot, String> mdStrategySnapshotService) {
        this.kpiReportLogicService = kpiReportLogicService;
        this.actionReportLogicService = actionReportLogicService;
        this.mdKpiScoreSnapshotService = mdKpiScoreSnapshotService;
        this.mdKpiService = mdKpiService;
        this.mdOkrObjectiveService = mdOkrObjectiveService;
        this.mdOkrKeyResultService = mdOkrKeyResultService;
        this.mdOkrInitiativeService = mdOkrInitiativeService;
        this.mdOkrSnapshotService = mdOkrSnapshotService;
        this.mdOrgUnitService = mdOrgUnitService;
        this.mdStrategyWorkspaceService = mdStrategyWorkspaceService;
        this.mdStrategyThemeService = mdStrategyThemeService;
        this.mdStrategyObjectiveService = mdStrategyObjectiveService;
        this.mdStrategySnapshotService = mdStrategySnapshotService;
    }

    @Override
    public DefaultResult<ManagementDashboardResult> dashboard(ManagementDashboardQuery query) throws ServiceException {
        ManagementDashboardQuery q = query == null ? new ManagementDashboardQuery() : query;
        ManagementDashboardResult dashboard = new ManagementDashboardResult();

        List<ManagementDashboardAlert> alerts = new ArrayList<>();
        ActionReportResult actionReport = this.actionReportLogicService.report(new ActionReportQuery()).getValue();
        dashboard.setKpi(buildKpiSummary(q, alerts));
        dashboard.setOkr(buildOkrSummary(q, alerts));
        dashboard.setStrategy(buildStrategySummary(q, alerts));
        dashboard.setAction(buildActionSummary(actionReport, alerts));
        dashboard.setAlerts(limitAlerts(alerts));
        dashboard.setOrganizationSummaries(buildOrganizationSummaries(q));
        dashboard.setStrategyScorecards(buildStrategyScorecards(q));
        dashboard.setDelayedActions(buildDelayedActions(actionReport));
        dashboard.setAtRiskObjectives(buildAtRiskObjectives(q));

        DefaultResult<ManagementDashboardResult> result = new DefaultResult<>();
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(dashboard);
        return result;
    }

    private ManagementDashboardDomainSummary buildKpiSummary(ManagementDashboardQuery query,
            List<ManagementDashboardAlert> alerts) throws ServiceException {
        KpiReportQueryRequest request = toKpiRequest(query);
        KpiReportSummary kpiSummary = this.kpiReportLogicService.summary(request).getValue();
        ManagementDashboardDomainSummary summary = new ManagementDashboardDomainSummary();
        if (kpiSummary != null) {
            summary.setTotalCount(nullToZero(kpiSummary.getKpiCount()));
            summary.setAvgScore(defaultDecimal(kpiSummary.getAvgScore()));
            summary.setGoodCount(nullToZero(kpiSummary.getGoodCount()));
            summary.setWarningCount(nullToZero(kpiSummary.getWarningCount()));
            summary.setBadCount(nullToZero(kpiSummary.getBadCount()));
            summary.setUnknownCount(nullToZero(kpiSummary.getUnknownCount()));
        }
        for (MdKpiScoreSnapshot snapshot : filterKpiSnapshots(loadKpiSnapshots(request), request)) {
            if (!isWarnStatus(snapshot.getScoreStatus())) {
                continue;
            }
            MdKpi kpi = loadKpi(snapshot.getKpiOid());
            alerts.add(alert("KPI", severity(snapshot.getScoreStatus()),
                    kpi == null ? snapshot.getKpiOid() : kpi.getKpiName(),
                    snapshot.getScoreStatus(), snapshot.getPeriodKey(), snapshot.getScoreValue(), snapshot.getKpiOid()));
        }
        return summary;
    }

    private ManagementDashboardDomainSummary buildOkrSummary(ManagementDashboardQuery query,
            List<ManagementDashboardAlert> alerts) throws ServiceException {
        List<MdOkrObjective> objectives = loadOkrObjectives(query);
        Set<String> objectiveOids = objectives.stream().map(MdOkrObjective::getOid).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        List<MdOkrKeyResult> keyResults = this.mdOkrKeyResultService.selectList("OBJECTIVE_OID, SORT_NO", "ASC").getValue();
        List<MdOkrInitiative> initiatives = this.mdOkrInitiativeService.selectList("OBJECTIVE_OID, SORT_NO", "ASC").getValue();
        List<MdOkrSnapshot> snapshots = filterOkrSnapshots(this.mdOkrSnapshotService.selectList("OBJECTIVE_OID, PERIOD_KEY", "ASC").getValue(), query, objectiveOids);
        Map<String, MdOkrSnapshot> latestSnapshotMap = latestOkrSnapshotMap(snapshots);

        ManagementDashboardDomainSummary summary = new ManagementDashboardDomainSummary();
        summary.setTotalCount(objectives.size());
        summary.setSecondaryCount((int) safeList(keyResults).stream().filter(item -> objectiveOids.contains(item.getObjectiveOid())).count());
        summary.setTertiaryCount((int) safeList(initiatives).stream().filter(item -> objectiveOids.contains(item.getObjectiveOid())).count());

        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (MdOkrObjective objective : objectives) {
            MdOkrSnapshot snapshot = latestSnapshotMap.get(objective.getOid());
            BigDecimal progress = snapshot == null ? objective.getProgressValue() : snapshot.getProgressValue();
            String status = snapshot == null ? null : snapshot.getScoreStatus();
            if (progress != null) {
                total = total.add(progress);
                count++;
            }
            applyStatusCount(summary, status);
            if (isWarnStatus(status)) {
                alerts.add(alert("OKR", severity(status), objective.getObjectiveName(),
                        status, snapshot.getPeriodKey(), progress, objective.getOid()));
            }
        }
        if (count > 0) {
            summary.setAvgScore(total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
        }
        return summary;
    }

    private ManagementDashboardDomainSummary buildStrategySummary(ManagementDashboardQuery query,
            List<ManagementDashboardAlert> alerts) throws ServiceException {
        List<MdStrategyWorkspace> workspaces = loadStrategyWorkspaces(query);
        Set<String> workspaceOids = workspaces.stream().map(MdStrategyWorkspace::getOid).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        List<MdStrategyTheme> themes = this.mdStrategyThemeService.selectList("WORKSPACE_OID, SORT_NO", "ASC").getValue();
        Set<String> themeOids = safeList(themes).stream()
                .filter(item -> workspaceOids.contains(item.getWorkspaceOid()))
                .map(MdStrategyTheme::getOid)
                .collect(Collectors.toSet());
        List<MdStrategyObjective> objectives = this.mdStrategyObjectiveService.selectList("THEME_OID, SORT_NO", "ASC").getValue();
        List<MdStrategySnapshot> snapshots = filterStrategySnapshots(this.mdStrategySnapshotService.selectList("WORKSPACE_OID, PERIOD_TYPE, PERIOD_KEY", "ASC").getValue(), query, workspaceOids);
        Map<String, MdStrategySnapshot> latestSnapshotMap = latestStrategySnapshotMap(snapshots);

        ManagementDashboardDomainSummary summary = new ManagementDashboardDomainSummary();
        summary.setTotalCount(workspaces.size());
        summary.setSecondaryCount((int) safeList(themes).stream().filter(item -> workspaceOids.contains(item.getWorkspaceOid())).count());
        summary.setTertiaryCount((int) safeList(objectives).stream().filter(item -> themeOids.contains(item.getThemeOid())).count());

        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        Map<String, MdStrategyWorkspace> workspaceMap = workspaces.stream().collect(Collectors.toMap(MdStrategyWorkspace::getOid, item -> item, (a, b) -> a));
        for (MdStrategySnapshot snapshot : latestSnapshotMap.values()) {
            if (snapshot.getScoreValue() != null) {
                total = total.add(snapshot.getScoreValue());
                count++;
            }
            String severity = strategySeverity(snapshot.getScoreValue());
            if (StringUtils.isNotBlank(severity)) {
                MdStrategyWorkspace workspace = workspaceMap.get(snapshot.getWorkspaceOid());
                alerts.add(alert("STRATEGY", severity,
                        workspace == null ? snapshot.getWorkspaceOid() : workspace.getWorkspaceName(),
                        severity, snapshot.getPeriodKey(), snapshot.getScoreValue(), snapshot.getWorkspaceOid()));
            }
        }
        if (count > 0) {
            summary.setAvgScore(total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
        }
        return summary;
    }

    private ManagementDashboardDomainSummary buildActionSummary(ActionReportResult report, List<ManagementDashboardAlert> alerts) {
        ActionReportSummary actionSummary = report == null ? null : report.getSummary();
        ManagementDashboardDomainSummary summary = new ManagementDashboardDomainSummary();
        if (actionSummary != null) {
            summary.setTotalCount(actionSummary.getItemCount());
            summary.setSecondaryCount(actionSummary.getPlanCount());
            summary.setTertiaryCount(actionSummary.getOwnerCount());
            summary.setOverdueCount(actionSummary.getOverdueCount());
            summary.setCompletedCount(actionSummary.getCompletedCount());
            summary.setAvgScore(defaultDecimal(actionSummary.getAvgProgress()));
        }
        if (report != null && CollectionUtils.isNotEmpty(report.getRows())) {
            for (ActionReportRow row : report.getRows()) {
                if (!row.isOverdue()) {
                    continue;
                }
                alerts.add(alert("ACTION", "BAD", row.getItemName(), row.getStatus(), null, row.getProgressValue(), row.getOid()));
            }
        }
        return summary;
    }

    private List<ManagementDashboardOrgSummary> buildOrganizationSummaries(ManagementDashboardQuery query) throws ServiceException {
        List<MdOrgUnit> orgs = loadOrgUnits(query);
        Map<String, ManagementDashboardOrgSummary> summaryMap = new HashMap<>();
        for (MdOrgUnit org : orgs) {
            ManagementDashboardOrgSummary summary = new ManagementDashboardOrgSummary();
            summary.setOrgOid(org.getOid());
            summary.setOrgCode(org.getOrgCode());
            summary.setOrgName(org.getOrgName());
            summaryMap.put(org.getOid(), summary);
        }

        KpiReportQueryRequest request = toKpiRequest(query);
        request.setDataForType("ORG");
        List<MdKpiScoreSnapshot> snapshots = filterKpiSnapshots(loadKpiSnapshots(request), request);
        Map<String, BigDecimal> totals = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();
        for (MdKpiScoreSnapshot snapshot : snapshots) {
            if (StringUtils.isBlank(snapshot.getOrgOid())) {
                continue;
            }
            ManagementDashboardOrgSummary summary = summaryMap.computeIfAbsent(snapshot.getOrgOid(), this::unknownOrgSummary);
            summary.setKpiSnapshotCount(summary.getKpiSnapshotCount() + 1);
            applyOrgStatusCount(summary, snapshot.getScoreStatus());
            if (snapshot.getScoreValue() != null) {
                totals.put(snapshot.getOrgOid(), totals.getOrDefault(snapshot.getOrgOid(), BigDecimal.ZERO).add(snapshot.getScoreValue()));
                counts.put(snapshot.getOrgOid(), counts.getOrDefault(snapshot.getOrgOid(), 0) + 1);
            }
        }
        for (Map.Entry<String, ManagementDashboardOrgSummary> entry : summaryMap.entrySet()) {
            int count = counts.getOrDefault(entry.getKey(), 0);
            if (count > 0) {
                entry.getValue().setAvgKpiScore(totals.get(entry.getKey()).divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
            }
        }
        return summaryMap.values().stream()
                .sorted(Comparator.comparing(ManagementDashboardOrgSummary::getOrgCode, Comparator.nullsLast(String::compareTo)))
                .collect(Collectors.toList());
    }

    private List<ManagementDashboardStrategyScorecardRow> buildStrategyScorecards(ManagementDashboardQuery query) throws ServiceException {
        List<MdStrategyWorkspace> workspaces = loadStrategyWorkspaces(query);
        Set<String> workspaceOids = workspaces.stream().map(MdStrategyWorkspace::getOid).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        List<MdStrategyTheme> themes = this.mdStrategyThemeService.selectList("WORKSPACE_OID, SORT_NO", "ASC").getValue();
        List<MdStrategyObjective> objectives = this.mdStrategyObjectiveService.selectList("THEME_OID, SORT_NO", "ASC").getValue();
        List<MdStrategySnapshot> snapshots = filterStrategySnapshots(this.mdStrategySnapshotService.selectList("WORKSPACE_OID, PERIOD_TYPE, PERIOD_KEY", "ASC").getValue(), query, workspaceOids);
        Map<String, MdStrategySnapshot> latestSnapshotMap = latestStrategySnapshotMap(snapshots);

        Map<String, Long> themeCountMap = safeList(themes).stream()
                .filter(item -> workspaceOids.contains(item.getWorkspaceOid()))
                .collect(Collectors.groupingBy(MdStrategyTheme::getWorkspaceOid, Collectors.counting()));
        Map<String, String> themeWorkspaceMap = safeList(themes).stream()
                .collect(Collectors.toMap(MdStrategyTheme::getOid, MdStrategyTheme::getWorkspaceOid, (a, b) -> a));
        Map<String, Long> objectiveCountMap = safeList(objectives).stream()
                .filter(item -> themeWorkspaceMap.containsKey(item.getThemeOid()))
                .collect(Collectors.groupingBy(item -> themeWorkspaceMap.get(item.getThemeOid()), Collectors.counting()));

        List<ManagementDashboardStrategyScorecardRow> rows = new ArrayList<>();
        for (MdStrategyWorkspace workspace : workspaces) {
            MdStrategySnapshot snapshot = latestSnapshotMap.get(workspace.getOid());
            ManagementDashboardStrategyScorecardRow row = new ManagementDashboardStrategyScorecardRow();
            row.setWorkspaceOid(workspace.getOid());
            row.setWorkspaceCode(workspace.getWorkspaceCode());
            row.setWorkspaceName(workspace.getWorkspaceName());
            row.setThemeCount(themeCountMap.getOrDefault(workspace.getOid(), 0L).intValue());
            row.setObjectiveCount(objectiveCountMap.getOrDefault(workspace.getOid(), 0L).intValue());
            if (snapshot != null) {
                row.setPeriodType(snapshot.getPeriodType());
                row.setPeriodKey(snapshot.getPeriodKey());
                row.setScoreValue(defaultDecimal(snapshot.getScoreValue()));
                row.setKpiCount(snapshot.getKpiCount() == null ? 0 : snapshot.getKpiCount());
                row.setOkrCount(snapshot.getOkrCount() == null ? 0 : snapshot.getOkrCount());
            }
            rows.add(row);
        }
        return rows;
    }

    private List<ActionReportRow> buildDelayedActions(ActionReportResult report) {
        if (report == null || CollectionUtils.isEmpty(report.getRows())) {
            return new ArrayList<>();
        }
        return report.getRows().stream()
                .filter(ActionReportRow::isOverdue)
                .collect(Collectors.toList());
    }

    private List<ManagementDashboardAtRiskObjectiveRow> buildAtRiskObjectives(ManagementDashboardQuery query) throws ServiceException {
        List<MdOkrObjective> objectives = loadOkrObjectives(query);
        Set<String> objectiveOids = objectives.stream().map(MdOkrObjective::getOid).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        List<MdOkrSnapshot> snapshots = filterOkrSnapshots(this.mdOkrSnapshotService.selectList("OBJECTIVE_OID, PERIOD_KEY", "ASC").getValue(), query, objectiveOids);
        Map<String, MdOkrSnapshot> latestSnapshotMap = latestOkrSnapshotMap(snapshots);

        List<ManagementDashboardAtRiskObjectiveRow> rows = new ArrayList<>();
        for (MdOkrObjective objective : objectives) {
            MdOkrSnapshot snapshot = latestSnapshotMap.get(objective.getOid());
            if (snapshot == null || !isWarnStatus(snapshot.getScoreStatus())) {
                continue;
            }
            ManagementDashboardAtRiskObjectiveRow row = new ManagementDashboardAtRiskObjectiveRow();
            row.setObjectiveOid(objective.getOid());
            row.setObjectiveCode(objective.getObjectiveCode());
            row.setObjectiveName(objective.getObjectiveName());
            row.setCycleOid(objective.getCycleOid());
            row.setPeriodKey(snapshot.getPeriodKey());
            row.setProgressValue(defaultDecimal(snapshot.getProgressValue()));
            row.setConfidenceScore(defaultDecimal(snapshot.getConfidenceScore()));
            row.setScoreStatus(snapshot.getScoreStatus());
            rows.add(row);
        }
        return rows;
    }

    private KpiReportQueryRequest toKpiRequest(ManagementDashboardQuery query) {
        KpiReportQueryRequest request = new KpiReportQueryRequest();
        request.setPeriodType(trim(query.getPeriodType()));
        request.setPeriodKey(trim(query.getPeriodKey()));
        request.setPeriodKeyFrom(trim(query.getPeriodKeyFrom()));
        request.setPeriodKeyTo(trim(query.getPeriodKeyTo()));
        request.setDataForType(trim(query.getDataForType()));
        request.setAccount(trim(query.getAccount()));
        request.setOrgOid(trim(query.getOrgOid()));
        return request;
    }

    private List<MdKpiScoreSnapshot> loadKpiSnapshots(KpiReportQueryRequest request) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        putIfNotBlank(params, "periodType", request.getPeriodType());
        if (StringUtils.isBlank(request.getPeriodKeyFrom()) || StringUtils.isBlank(request.getPeriodKeyTo())) {
            putIfNotBlank(params, "periodKey", request.getPeriodKey());
        }
        putIfNotBlank(params, "dataForType", request.getDataForType());
        if ("ACCOUNT".equals(request.getDataForType())) {
            putIfNotBlank(params, "account", request.getAccount());
        } else if ("ORG".equals(request.getDataForType())) {
            putIfNotBlank(params, "orgOid", request.getOrgOid());
        }
        return params.isEmpty()
                ? this.mdKpiScoreSnapshotService.selectList("PERIOD_TYPE, PERIOD_KEY, KPI_OID", "ASC").getValue()
                : this.mdKpiScoreSnapshotService.selectListByParams(params, "PERIOD_TYPE, PERIOD_KEY, KPI_OID", "ASC").getValue();
    }

    private List<MdKpiScoreSnapshot> filterKpiSnapshots(List<MdKpiScoreSnapshot> snapshots, KpiReportQueryRequest request) {
        List<MdKpiScoreSnapshot> result = new ArrayList<>();
        for (MdKpiScoreSnapshot snapshot : safeList(snapshots)) {
            if (!matchesPeriodKey(snapshot.getPeriodKey(), request.getPeriodKeyFrom(), request.getPeriodKeyTo())) {
                continue;
            }
            result.add(snapshot);
        }
        return result;
    }

    private List<MdOkrObjective> loadOkrObjectives(ManagementDashboardQuery query) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        putIfNotBlank(params, "cycleOid", query.getCycleOid());
        return params.isEmpty()
                ? this.mdOkrObjectiveService.selectList("OBJECTIVE_CODE", "ASC").getValue()
                : this.mdOkrObjectiveService.selectListByParams(params, "OBJECTIVE_CODE", "ASC").getValue();
    }

    private List<MdStrategyWorkspace> loadStrategyWorkspaces(ManagementDashboardQuery query) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        putIfNotBlank(params, "oid", query.getWorkspaceOid());
        return params.isEmpty()
                ? this.mdStrategyWorkspaceService.selectList("WORKSPACE_CODE", "ASC").getValue()
                : this.mdStrategyWorkspaceService.selectListByParams(params, "WORKSPACE_CODE", "ASC").getValue();
    }

    private List<MdOrgUnit> loadOrgUnits(ManagementDashboardQuery query) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        putIfNotBlank(params, "oid", query.getOrgOid());
        return params.isEmpty()
                ? this.mdOrgUnitService.selectList("ORG_CODE", "ASC").getValue()
                : this.mdOrgUnitService.selectListByParams(params, "ORG_CODE", "ASC").getValue();
    }

    private List<MdOkrSnapshot> filterOkrSnapshots(List<MdOkrSnapshot> snapshots, ManagementDashboardQuery query, Set<String> objectiveOids) {
        List<MdOkrSnapshot> result = new ArrayList<>();
        for (MdOkrSnapshot snapshot : safeList(snapshots)) {
            if (!objectiveOids.contains(snapshot.getObjectiveOid())) {
                continue;
            }
            if (!matchesPeriodKey(snapshot.getPeriodKey(), query.getPeriodKeyFrom(), query.getPeriodKeyTo())) {
                continue;
            }
            if (StringUtils.isNotBlank(query.getPeriodKey()) && !query.getPeriodKey().equals(snapshot.getPeriodKey())) {
                continue;
            }
            result.add(snapshot);
        }
        return result;
    }

    private List<MdStrategySnapshot> filterStrategySnapshots(List<MdStrategySnapshot> snapshots, ManagementDashboardQuery query, Set<String> workspaceOids) {
        List<MdStrategySnapshot> result = new ArrayList<>();
        for (MdStrategySnapshot snapshot : safeList(snapshots)) {
            if (!workspaceOids.contains(snapshot.getWorkspaceOid())) {
                continue;
            }
            if (StringUtils.isNotBlank(query.getPeriodType()) && !query.getPeriodType().equals(snapshot.getPeriodType())) {
                continue;
            }
            if (!matchesPeriodKey(snapshot.getPeriodKey(), query.getPeriodKeyFrom(), query.getPeriodKeyTo())) {
                continue;
            }
            if (StringUtils.isNotBlank(query.getPeriodKey()) && !query.getPeriodKey().equals(snapshot.getPeriodKey())) {
                continue;
            }
            result.add(snapshot);
        }
        return result;
    }

    private Map<String, MdOkrSnapshot> latestOkrSnapshotMap(List<MdOkrSnapshot> snapshots) {
        Map<String, MdOkrSnapshot> map = new HashMap<>();
        List<MdOkrSnapshot> sorted = new ArrayList<>(safeList(snapshots));
        sorted.sort(Comparator.comparing(MdOkrSnapshot::getPeriodKey, Comparator.nullsFirst(String::compareTo))
                .thenComparing(MdOkrSnapshot::getSnapshotAt, Comparator.nullsFirst(java.util.Date::compareTo)));
        for (MdOkrSnapshot snapshot : sorted) {
            map.put(snapshot.getObjectiveOid(), snapshot);
        }
        return map;
    }

    private Map<String, MdStrategySnapshot> latestStrategySnapshotMap(List<MdStrategySnapshot> snapshots) {
        Map<String, MdStrategySnapshot> map = new HashMap<>();
        List<MdStrategySnapshot> sorted = new ArrayList<>(safeList(snapshots));
        sorted.sort(Comparator.comparing(MdStrategySnapshot::getPeriodKey, Comparator.nullsFirst(String::compareTo))
                .thenComparing(MdStrategySnapshot::getSnapshotAt, Comparator.nullsFirst(java.util.Date::compareTo)));
        for (MdStrategySnapshot snapshot : sorted) {
            map.put(snapshot.getWorkspaceOid(), snapshot);
        }
        return map;
    }

    private MdKpi loadKpi(String kpiOid) throws ServiceException {
        if (StringUtils.isBlank(kpiOid)) {
            return null;
        }
        MdKpi key = new MdKpi();
        key.setOid(kpiOid);
        return this.mdKpiService.selectByEntityPrimaryKey(key).getValue();
    }

    private void applyStatusCount(ManagementDashboardDomainSummary summary, String status) {
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

    private void applyOrgStatusCount(ManagementDashboardOrgSummary summary, String status) {
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

    private ManagementDashboardOrgSummary unknownOrgSummary(String orgOid) {
        ManagementDashboardOrgSummary summary = new ManagementDashboardOrgSummary();
        summary.setOrgOid(orgOid);
        summary.setOrgCode(orgOid);
        summary.setOrgName(orgOid);
        return summary;
    }

    private boolean matchesPeriodKey(String value, String from, String to) {
        if (StringUtils.isBlank(value)) {
            return StringUtils.isBlank(from) && StringUtils.isBlank(to);
        }
        if (StringUtils.isNotBlank(from) && value.compareTo(from) < 0) {
            return false;
        }
        return StringUtils.isBlank(to) || value.compareTo(to) <= 0;
    }

    private boolean isWarnStatus(String status) {
        return "BAD".equals(status) || "WARNING".equals(status);
    }

    private String severity(String status) {
        return "BAD".equals(status) ? "BAD" : "WARNING";
    }

    private String strategySeverity(BigDecimal score) {
        if (score == null) {
            return "";
        }
        if (score.compareTo(STRATEGY_BAD_SCORE) < 0) {
            return "BAD";
        }
        if (score.compareTo(STRATEGY_WARNING_SCORE) < 0) {
            return "WARNING";
        }
        return "";
    }

    private ManagementDashboardAlert alert(String domain, String severity, String title, String summary,
            String periodKey, BigDecimal scoreValue, String sourceOid) {
        ManagementDashboardAlert alert = new ManagementDashboardAlert();
        alert.setDomain(domain);
        alert.setSeverity(severity);
        alert.setTitle(StringUtils.defaultIfBlank(title, sourceOid));
        alert.setSummary(summary);
        alert.setPeriodKey(periodKey);
        alert.setScoreValue(scoreValue);
        alert.setSourceOid(sourceOid);
        return alert;
    }

    private List<ManagementDashboardAlert> limitAlerts(List<ManagementDashboardAlert> alerts) {
        if (CollectionUtils.isEmpty(alerts) || alerts.size() <= ALERT_LIMIT) {
            return alerts == null ? new ArrayList<>() : alerts;
        }
        return new ArrayList<>(alerts.subList(0, ALERT_LIMIT));
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private int nullToZero(Integer value) {
        return value == null ? 0 : value;
    }

    private void putIfNotBlank(Map<String, Object> params, String key, String value) {
        if (StringUtils.isNotBlank(value)) {
            params.put(key, StringUtils.trim(value));
        }
    }

    private String trim(String value) {
        return StringUtils.trimToNull(value);
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }
}
