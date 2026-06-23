package org.qifu.md.logic.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.ServiceAuthority;
import org.qifu.base.model.ServiceMethodAuthority;
import org.qifu.base.model.ServiceMethodType;
import org.qifu.base.model.YesNoKeyProvide;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiScoreSnapshot;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.entity.MdOkrSnapshot;
import org.qifu.md.entity.MdStrategyObjective;
import org.qifu.md.entity.MdStrategyObjectiveLink;
import org.qifu.md.entity.MdStrategySnapshot;
import org.qifu.md.entity.MdStrategyTheme;
import org.qifu.md.entity.MdStrategyWorkspace;
import org.qifu.md.logic.IStrategyReportLogicService;
import org.qifu.md.model.StrategyReportLinkView;
import org.qifu.md.model.StrategyReportObjectiveView;
import org.qifu.md.model.StrategyReportQueryRequest;
import org.qifu.md.model.StrategyReportResult;
import org.qifu.md.model.StrategyReportThemeView;
import org.qifu.md.service.IMdKpiScoreSnapshotService;
import org.qifu.md.service.IMdKpiService;
import org.qifu.md.service.IMdOkrObjectiveService;
import org.qifu.md.service.IMdOkrSnapshotService;
import org.qifu.md.service.IMdStrategyObjectiveLinkService;
import org.qifu.md.service.IMdStrategyObjectiveService;
import org.qifu.md.service.IMdStrategySnapshotService;
import org.qifu.md.service.IMdStrategyThemeService;
import org.qifu.md.service.IMdStrategyWorkspaceService;
import org.qifu.util.LoadResources;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@ServiceAuthority(check = true)
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class StrategyReportLogicServiceImpl implements IStrategyReportLogicService {

    private static final String LINK_TYPE_KPI = "KPI";
    private static final String LINK_TYPE_OKR_OBJECTIVE = "OKR_OBJECTIVE";
    private static final String DATA_FOR_GLOBAL = "GLOBAL";
    private static final String DATA_FOR_ACCOUNT = "ACCOUNT";
    private static final String DATA_FOR_ORG = "ORG";
    private static final DateTimeFormatter OKR_SNAPSHOT_PERIOD_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService;
    private final IMdStrategyThemeService<MdStrategyTheme, String> mdStrategyThemeService;
    private final IMdStrategyObjectiveService<MdStrategyObjective, String> mdStrategyObjectiveService;
    private final IMdStrategyObjectiveLinkService<MdStrategyObjectiveLink, String> mdStrategyObjectiveLinkService;
    private final IMdStrategySnapshotService<MdStrategySnapshot, String> mdStrategySnapshotService;
    private final IMdKpiService<MdKpi, String> mdKpiService;
    private final IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> mdKpiScoreSnapshotService;
    private final IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService;
    private final IMdOkrSnapshotService<MdOkrSnapshot, String> mdOkrSnapshotService;

    public StrategyReportLogicServiceImpl(IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService,
            IMdStrategyThemeService<MdStrategyTheme, String> mdStrategyThemeService,
            IMdStrategyObjectiveService<MdStrategyObjective, String> mdStrategyObjectiveService,
            IMdStrategyObjectiveLinkService<MdStrategyObjectiveLink, String> mdStrategyObjectiveLinkService,
            IMdStrategySnapshotService<MdStrategySnapshot, String> mdStrategySnapshotService,
            IMdKpiService<MdKpi, String> mdKpiService,
            IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> mdKpiScoreSnapshotService,
            IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService,
            IMdOkrSnapshotService<MdOkrSnapshot, String> mdOkrSnapshotService) {
        this.mdStrategyWorkspaceService = mdStrategyWorkspaceService;
        this.mdStrategyThemeService = mdStrategyThemeService;
        this.mdStrategyObjectiveService = mdStrategyObjectiveService;
        this.mdStrategyObjectiveLinkService = mdStrategyObjectiveLinkService;
        this.mdStrategySnapshotService = mdStrategySnapshotService;
        this.mdKpiService = mdKpiService;
        this.mdKpiScoreSnapshotService = mdKpiScoreSnapshotService;
        this.mdOkrObjectiveService = mdOkrObjectiveService;
        this.mdOkrSnapshotService = mdOkrSnapshotService;
    }

    @ServiceMethodAuthority(type = ServiceMethodType.INSERT)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<StrategyReportResult> generate(StrategyReportQueryRequest request) throws ServiceException {
        validate(request);
        MdStrategyWorkspace workspace = loadWorkspace(request.getWorkspaceOid());
        StrategyReportResult report = new StrategyReportResult();
        report.setWorkspace(workspace);

        SnapshotAccumulator totalAccumulator = new SnapshotAccumulator();
        List<StrategyReportThemeView> themeViews = new ArrayList<>();
        List<MdStrategyTheme> themes = loadThemes(request.getWorkspaceOid());
        for (MdStrategyTheme theme : themes) {
            StrategyReportThemeView themeView = buildThemeView(theme, totalAccumulator, request);
            themeViews.add(themeView);
        }
        report.setThemeList(themeViews);

        MdStrategySnapshot snapshot = toSnapshot(request, totalAccumulator, themeViews);
        report.setSnapshot(saveOrUpdateSnapshot(snapshot));

        DefaultResult<StrategyReportResult> result = new DefaultResult<>();
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(report);
        return result;
    }

    private void validate(StrategyReportQueryRequest request) throws ServiceException {
        if (request == null || StringUtils.isBlank(request.getWorkspaceOid())) {
            throw new ServiceException("Please select workspace.");
        }
        if (StringUtils.isBlank(request.getPeriodType())) {
            throw new ServiceException("Please select period type.");
        }
        if (StringUtils.isBlank(request.getPeriodKey())) {
            throw new ServiceException("Please enter period key.");
        }
        String dataForType = StringUtils.defaultIfBlank(request.getDataForType(), DATA_FOR_GLOBAL);
        request.setDataForType(dataForType);
        if (DATA_FOR_ACCOUNT.equals(dataForType) && StringUtils.isBlank(request.getAccount())) {
            throw new ServiceException("Please select account.");
        }
        if (DATA_FOR_ORG.equals(dataForType) && StringUtils.isBlank(request.getOrgOid())) {
            throw new ServiceException("Please select organization.");
        }
    }

    private MdStrategyWorkspace loadWorkspace(String workspaceOid) throws ServiceException {
        MdStrategyWorkspace key = new MdStrategyWorkspace();
        key.setOid(workspaceOid);
        return this.mdStrategyWorkspaceService.selectByEntityPrimaryKey(key).getValueEmptyThrowMessage();
    }

    private List<MdStrategyTheme> loadThemes(String workspaceOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("workspaceOid", workspaceOid);
        List<MdStrategyTheme> themes = this.mdStrategyThemeService.selectListByParams(params, "SORT_NO, THEME_CODE", "ASC").getValue();
        return themes == null ? new ArrayList<>() : themes;
    }

    private StrategyReportThemeView buildThemeView(MdStrategyTheme theme, SnapshotAccumulator totalAccumulator,
            StrategyReportQueryRequest request) throws ServiceException {
        StrategyReportThemeView themeView = new StrategyReportThemeView();
        themeView.setTheme(theme);
        SnapshotAccumulator themeAccumulator = new SnapshotAccumulator();

        List<MdStrategyObjective> objectives = loadObjectives(theme.getOid());
        List<StrategyReportObjectiveView> objectiveViews = new ArrayList<>();
        for (MdStrategyObjective objective : objectives) {
            StrategyReportObjectiveView objectiveView = buildObjectiveView(objective, request);
            objectiveViews.add(objectiveView);
            themeAccumulator.addScore(objectiveView.getScoreValue(), objective.getWeightValue());
            totalAccumulator.addKpiCount(objectiveView.getKpiCount());
            totalAccumulator.addOkrCount(objectiveView.getOkrCount());
        }
        themeView.setObjectiveList(objectiveViews);
        themeView.setObjectiveCount(objectiveViews.size());
        themeView.setScoreValue(themeAccumulator.score());
        totalAccumulator.addScore(themeView.getScoreValue(), theme.getWeightValue());
        return themeView;
    }

    private List<MdStrategyObjective> loadObjectives(String themeOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("themeOid", themeOid);
        List<MdStrategyObjective> objectives = this.mdStrategyObjectiveService.selectListByParams(params, "SORT_NO, OBJECTIVE_CODE", "ASC").getValue();
        return objectives == null ? new ArrayList<>() : objectives;
    }

    private StrategyReportObjectiveView buildObjectiveView(MdStrategyObjective objective, StrategyReportQueryRequest request)
            throws ServiceException {
        StrategyReportObjectiveView objectiveView = new StrategyReportObjectiveView();
        objectiveView.setObjective(objective);
        SnapshotAccumulator objectiveAccumulator = new SnapshotAccumulator();
        List<StrategyReportLinkView> linkViews = new ArrayList<>();
        for (MdStrategyObjectiveLink link : loadLinks(objective.getOid())) {
            StrategyReportLinkView linkView = buildLinkView(link, request);
            linkViews.add(linkView);
            objectiveAccumulator.addScore(linkView.getScoreValue(), link.getWeightValue());
            if (LINK_TYPE_KPI.equals(link.getLinkType())) {
                objectiveView.setKpiCount(objectiveView.getKpiCount() + 1);
            } else if (LINK_TYPE_OKR_OBJECTIVE.equals(link.getLinkType())) {
                objectiveView.setOkrCount(objectiveView.getOkrCount() + 1);
            }
        }
        objectiveView.setLinkList(linkViews);
        objectiveView.setScoreValue(objectiveAccumulator.score());
        return objectiveView;
    }

    private List<MdStrategyObjectiveLink> loadLinks(String objectiveOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("strategyObjectiveOid", objectiveOid);
        List<MdStrategyObjectiveLink> links = this.mdStrategyObjectiveLinkService.selectListByParams(params, "SORT_NO, LINK_TYPE", "ASC").getValue();
        return links == null ? new ArrayList<>() : links;
    }

    private StrategyReportLinkView buildLinkView(MdStrategyObjectiveLink link, StrategyReportQueryRequest request) throws ServiceException {
        StrategyReportLinkView view = new StrategyReportLinkView();
        view.setLink(link);
        if (LINK_TYPE_KPI.equals(link.getLinkType())) {
            applyKpiScore(view, link, request);
        } else if (LINK_TYPE_OKR_OBJECTIVE.equals(link.getLinkType())) {
            applyOkrObjectiveScore(view, link, request);
        } else {
            view.setSourceCode(link.getLinkOid());
            view.setSourceName("Unsupported link type");
            view.setMissingScore(true);
        }
        view.setMissingScore(view.getScoreValue() == null);
        return view;
    }

    private void applyKpiScore(StrategyReportLinkView view, MdStrategyObjectiveLink link, StrategyReportQueryRequest request)
            throws ServiceException {
        MdKpi kpi = loadKpi(link.getLinkOid());
        if (kpi != null) {
            view.setSourceCode(kpi.getKpiCode());
            view.setSourceName(kpi.getKpiName());
        }
        MdKpiScoreSnapshot snapshot = loadKpiSnapshot(link.getLinkOid(), request);
        if (snapshot != null) {
            view.setScoreValue(snapshot.getScoreValue());
            view.setScoreStatus(snapshot.getScoreStatus());
            view.setDataForType(snapshot.getDataForType());
            view.setAccount(snapshot.getAccount());
            view.setOrgOid(snapshot.getOrgOid());
            view.setCalculationTrace(snapshot.getCalculationTrace());
            view.setCalculatedAt(snapshot.getCalculatedAt());
        }
    }

    private MdKpi loadKpi(String kpiOid) throws ServiceException {
        if (StringUtils.isBlank(kpiOid)) {
            return null;
        }
        MdKpi key = new MdKpi();
        key.setOid(kpiOid);
        return this.mdKpiService.selectByEntityPrimaryKey(key).getValue();
    }

    private MdKpiScoreSnapshot loadKpiSnapshot(String kpiOid, StrategyReportQueryRequest request) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("kpiOid", kpiOid);
        params.put("periodType", request.getPeriodType());
        params.put("periodKey", request.getPeriodKey());
        params.put("dataForType", StringUtils.defaultIfBlank(request.getDataForType(), DATA_FOR_GLOBAL));
        if (DATA_FOR_ACCOUNT.equals(request.getDataForType())) {
            params.put("account", request.getAccount());
        }
        if (DATA_FOR_ORG.equals(request.getDataForType())) {
            params.put("orgOid", request.getOrgOid());
        }
        List<MdKpiScoreSnapshot> snapshots = this.mdKpiScoreSnapshotService.selectListByParams(params, "CALCULATED_AT", "DESC").getValue();
        return CollectionUtils.isEmpty(snapshots) ? null : snapshots.get(0);
    }

    private void applyOkrObjectiveScore(StrategyReportLinkView view, MdStrategyObjectiveLink link, StrategyReportQueryRequest request) throws ServiceException {
        MdOkrObjective key = new MdOkrObjective();
        key.setOid(link.getLinkOid());
        MdOkrObjective objective = this.mdOkrObjectiveService.selectByEntityPrimaryKey(key).getValue();
        if (objective == null) {
            view.setSourceCode(link.getLinkOid());
            return;
        }
        view.setSourceCode(objective.getObjectiveCode());
        view.setSourceName(objective.getObjectiveName());
        MdOkrSnapshot snapshot = loadOkrSnapshot(link.getLinkOid(), request);
        if (snapshot != null) {
            view.setScoreValue(snapshot.getProgressValue());
            view.setScoreStatus(snapshot.getScoreStatus());
            view.setCalculationTrace(snapshot.getCalculationTrace());
            view.setCalculatedAt(snapshot.getSnapshotAt());
        }
    }

    private MdOkrSnapshot loadOkrSnapshot(String objectiveOid, StrategyReportQueryRequest request) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("objectiveOid", objectiveOid);
        OkrSnapshotPeriodRange periodRange = resolveOkrSnapshotPeriodRange(request);
        params.put("periodKeyFrom", periodRange.from());
        params.put("periodKeyTo", periodRange.to());
        if (DATA_FOR_ACCOUNT.equals(request.getDataForType())) {
            params.put("account", request.getAccount());
        }
        if (DATA_FOR_ORG.equals(request.getDataForType())) {
            params.put("orgOid", request.getOrgOid());
        }
        List<MdOkrSnapshot> snapshots = this.mdOkrSnapshotService.selectListByParams(params, "PERIOD_KEY, SNAPSHOT_AT", "DESC").getValue();
        return CollectionUtils.isEmpty(snapshots) ? null : snapshots.get(0);
    }

    private OkrSnapshotPeriodRange resolveOkrSnapshotPeriodRange(StrategyReportQueryRequest request) throws ServiceException {
        String periodType = StringUtils.trimToEmpty(request.getPeriodType()).toUpperCase();
        String periodKey = StringUtils.trimToEmpty(request.getPeriodKey());
        try {
            if ("DAY".equals(periodType)) {
                LocalDate date = LocalDate.parse(periodKey);
                return toOkrSnapshotPeriodRange(date, date);
            }
            if ("WEEK".equals(periodType)) {
                String[] parts = periodKey.split("-W");
                LocalDate monday = LocalDate.of(Integer.parseInt(parts[0]), 1, 4)
                        .with(WeekFields.ISO.weekOfWeekBasedYear(), Integer.parseInt(parts[1]))
                        .with(WeekFields.ISO.dayOfWeek(), 1);
                return toOkrSnapshotPeriodRange(monday, monday.plusDays(6));
            }
            if ("MONTH".equals(periodType)) {
                LocalDate firstDay = LocalDate.parse(periodKey + "-01");
                return toOkrSnapshotPeriodRange(firstDay, firstDay.with(TemporalAdjusters.lastDayOfMonth()));
            }
            if ("QUARTER".equals(periodType)) {
                String[] parts = periodKey.split("-Q");
                LocalDate firstDay = LocalDate.of(Integer.parseInt(parts[0]), (Integer.parseInt(parts[1]) - 1) * 3 + 1, 1);
                return toOkrSnapshotPeriodRange(firstDay, firstDay.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth()));
            }
            if ("HALFYEAR".equals(periodType)) {
                String[] parts = periodKey.split("-H");
                LocalDate firstDay = LocalDate.of(Integer.parseInt(parts[0]), "1".equals(parts[1]) ? 1 : 7, 1);
                return toOkrSnapshotPeriodRange(firstDay, firstDay.plusMonths(5).with(TemporalAdjusters.lastDayOfMonth()));
            }
            if ("YEAR".equals(periodType)) {
                LocalDate firstDay = LocalDate.of(Integer.parseInt(periodKey), 1, 1);
                return toOkrSnapshotPeriodRange(firstDay, firstDay.with(TemporalAdjusters.lastDayOfYear()));
            }
        } catch (RuntimeException e) {
            throw new ServiceException("Invalid period key: " + periodKey);
        }
        throw new ServiceException("Unsupported period type: " + request.getPeriodType());
    }

    private OkrSnapshotPeriodRange toOkrSnapshotPeriodRange(LocalDate from, LocalDate to) {
        return new OkrSnapshotPeriodRange(
                from.format(OKR_SNAPSHOT_PERIOD_FORMAT),
                to.format(OKR_SNAPSHOT_PERIOD_FORMAT));
    }

    private record OkrSnapshotPeriodRange(String from, String to) {
    }

    private MdStrategySnapshot toSnapshot(StrategyReportQueryRequest request, SnapshotAccumulator accumulator,
            List<StrategyReportThemeView> themeViews) {
        MdStrategySnapshot snapshot = new MdStrategySnapshot();
        snapshot.setWorkspaceOid(request.getWorkspaceOid());
        snapshot.setPeriodType(StringUtils.trim(request.getPeriodType()));
        snapshot.setPeriodKey(StringUtils.trim(request.getPeriodKey()));
        snapshot.setScoreValue(accumulator.score());
        snapshot.setKpiCount(accumulator.getKpiCount());
        snapshot.setOkrCount(accumulator.getOkrCount());
        snapshot.setCalculationTrace(toCalculationTrace(themeViews));
        snapshot.setSnapshotAt(new Date());
        return snapshot;
    }

    private MdStrategySnapshot saveOrUpdateSnapshot(MdStrategySnapshot snapshot) throws ServiceException {
        MdStrategySnapshot existing = loadSnapshotByKey(snapshot);
        if (existing == null) {
            return this.mdStrategySnapshotService.insert(snapshot).getValueEmptyThrowMessage();
        }
        snapshot.setOid(existing.getOid());
        this.mdStrategySnapshotService.update(snapshot).getValueEmptyThrowMessage();
        return this.mdStrategySnapshotService.selectByEntityPrimaryKey(snapshot).getValueEmptyThrowMessage();
    }

    private MdStrategySnapshot loadSnapshotByKey(MdStrategySnapshot snapshot) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("workspaceOid", snapshot.getWorkspaceOid());
        params.put("periodType", snapshot.getPeriodType());
        params.put("periodKey", snapshot.getPeriodKey());
        List<MdStrategySnapshot> snapshots = this.mdStrategySnapshotService.selectListByParams(params).getValue();
        return CollectionUtils.isEmpty(snapshots) ? null : snapshots.get(0);
    }

    private String toCalculationTrace(List<StrategyReportThemeView> themeViews) {
        Map<String, Object> trace = new LinkedHashMap<>();
        List<Map<String, Object>> themes = new ArrayList<>();
        trace.put("source", "STRATEGY_REPORT");
        trace.put("themes", themes);
        for (StrategyReportThemeView themeView : themeViews) {
            Map<String, Object> themeTrace = new LinkedHashMap<>();
            List<Map<String, Object>> objectives = new ArrayList<>();
            themeTrace.put("themeOid", themeView.getTheme().getOid());
            themeTrace.put("scoreValue", themeView.getScoreValue());
            themeTrace.put("objectives", objectives);
            themes.add(themeTrace);
            for (StrategyReportObjectiveView objectiveView : themeView.getObjectiveList()) {
                Map<String, Object> objectiveTrace = new LinkedHashMap<>();
                objectiveTrace.put("objectiveOid", objectiveView.getObjective().getOid());
                objectiveTrace.put("scoreValue", objectiveView.getScoreValue());
                objectiveTrace.put("linkCount", objectiveView.getLinkList().size());
                objectives.add(objectiveTrace);
            }
        }
        return toJson(trace);
    }

    private String toJson(Map<String, Object> trace) {
        try {
            return LoadResources.getObjectMapper().writeValueAsString(trace);
        } catch (Exception e) {
            throw new IllegalStateException("Build strategy calculation trace failed.", e);
        }
    }

    private static final class SnapshotAccumulator {
        private BigDecimal weightedScore = BigDecimal.ZERO;
        private BigDecimal weightTotal = BigDecimal.ZERO;
        private BigDecimal simpleScore = BigDecimal.ZERO;
        private int scoreCount;
        private int kpiCount;
        private int okrCount;

        void addScore(BigDecimal score, BigDecimal weight) {
            if (score == null) {
                return;
            }
            simpleScore = simpleScore.add(score);
            scoreCount++;
            BigDecimal normalizedWeight = weight == null ? BigDecimal.ZERO : weight;
            if (normalizedWeight.compareTo(BigDecimal.ZERO) > 0) {
                weightedScore = weightedScore.add(score.multiply(normalizedWeight));
                weightTotal = weightTotal.add(normalizedWeight);
            }
        }

        BigDecimal score() {
            if (weightTotal.compareTo(BigDecimal.ZERO) > 0) {
                return weightedScore.divide(weightTotal, 4, RoundingMode.HALF_UP);
            }
            if (scoreCount == 0) {
                return BigDecimal.ZERO;
            }
            return simpleScore.divide(BigDecimal.valueOf(scoreCount), 4, RoundingMode.HALF_UP);
        }

        int getKpiCount() {
            return kpiCount;
        }

        void addKpiCount(int count) {
            this.kpiCount += count;
        }

        int getOkrCount() {
            return okrCount;
        }

        void addOkrCount(int count) {
            this.okrCount += count;
        }
    }
}
