package org.qifu.md.logic.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.ServiceAuthority;
import org.qifu.base.model.ServiceMethodAuthority;
import org.qifu.base.model.ServiceMethodType;
import org.qifu.base.model.YesNoKeyProvide;
import org.qifu.md.entity.MdActionItem;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiScoreSnapshot;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.entity.MdOkrSnapshot;
import org.qifu.md.entity.MdPerformanceSignal;
import org.qifu.md.entity.MdStrategySnapshot;
import org.qifu.md.entity.MdStrategyWorkspace;
import org.qifu.md.logic.IPerformanceSignalLogicService;
import org.qifu.md.mapper.MdPerformanceSignalMapper;
import org.qifu.md.model.PerformanceSignalGenerationResult;
import org.qifu.md.service.IMdActionItemService;
import org.qifu.md.service.IMdKpiScoreSnapshotService;
import org.qifu.md.service.IMdKpiService;
import org.qifu.md.service.IMdOkrObjectiveService;
import org.qifu.md.service.IMdOkrSnapshotService;
import org.qifu.md.service.IMdPerformanceSignalService;
import org.qifu.md.service.IMdStrategySnapshotService;
import org.qifu.md.service.IMdStrategyWorkspaceService;
import org.qifu.md.util.PeriodKeyUtils;
import org.qifu.util.LoadResources;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@ServiceAuthority(check = true)
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class PerformanceSignalLogicServiceImpl implements IPerformanceSignalLogicService {
    private static final String SOURCE_KPI = "KPI";
    private static final String SOURCE_OKR = "OKR";
    private static final String SOURCE_STRATEGY = "STRATEGY";
    private static final String SOURCE_ACTION = "ACTION";
    private static final String SIGNAL_SCORE_STATUS = "SCORE_STATUS";
    private static final String SIGNAL_TARGET_VARIANCE = "TARGET_VARIANCE";
    private static final String SIGNAL_TREND_DOWN = "TREND_DOWN";
    private static final String SIGNAL_OVERDUE = "OVERDUE";
    private static final String OPEN = "OPEN";
    private static final String RESOLVED = "RESOLVED";
    private static final String KPI_GENERATOR_VERSION = "KPI_V1";
    private static final String OKR_GENERATOR_VERSION = "OKR_V1";
    private static final String STRATEGY_GENERATOR_VERSION = "STRATEGY_V1";
    private static final String ACTION_GENERATOR_VERSION = "ACTION_V1";

    private final IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> snapshotService;
    private final IMdKpiService<MdKpi, String> kpiService;
    private final IMdOkrSnapshotService<MdOkrSnapshot, String> okrSnapshotService;
    private final IMdOkrObjectiveService<MdOkrObjective, String> okrObjectiveService;
    private final IMdStrategySnapshotService<MdStrategySnapshot, String> strategySnapshotService;
    private final IMdStrategyWorkspaceService<MdStrategyWorkspace, String> strategyWorkspaceService;
    private final IMdActionItemService<MdActionItem, String> actionItemService;
    private final IMdPerformanceSignalService<MdPerformanceSignal, String> signalService;
    private final MdPerformanceSignalMapper signalMapper;

    public PerformanceSignalLogicServiceImpl(
            IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> snapshotService,
            IMdKpiService<MdKpi, String> kpiService,
            IMdOkrSnapshotService<MdOkrSnapshot, String> okrSnapshotService,
            IMdOkrObjectiveService<MdOkrObjective, String> okrObjectiveService,
            IMdStrategySnapshotService<MdStrategySnapshot, String> strategySnapshotService,
            IMdStrategyWorkspaceService<MdStrategyWorkspace, String> strategyWorkspaceService,
            IMdActionItemService<MdActionItem, String> actionItemService,
            IMdPerformanceSignalService<MdPerformanceSignal, String> signalService,
            MdPerformanceSignalMapper signalMapper) {
        this.snapshotService = snapshotService;
        this.kpiService = kpiService;
        this.okrSnapshotService = okrSnapshotService;
        this.okrObjectiveService = okrObjectiveService;
        this.strategySnapshotService = strategySnapshotService;
        this.strategyWorkspaceService = strategyWorkspaceService;
        this.actionItemService = actionItemService;
        this.signalService = signalService;
        this.signalMapper = signalMapper;
    }

    @Override
    @ServiceMethodAuthority(type = {ServiceMethodType.INSERT, ServiceMethodType.UPDATE})
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    public DefaultResult<PerformanceSignalGenerationResult> generateKpiSignals(Map<String, Object> params)
            throws ServiceException {
        Map<String, Object> query = params == null ? new HashMap<>() : new HashMap<>(params);
        List<MdKpiScoreSnapshot> snapshots = snapshotService
                .selectListByParams(query, "PERIOD_TYPE, PERIOD_KEY, KPI_OID", "ASC").getValue();
        PerformanceSignalGenerationResult generation = new PerformanceSignalGenerationResult();
        generation.setSnapshotCount(snapshots == null ? 0 : snapshots.size());
        for (MdKpiScoreSnapshot snapshot : safeList(snapshots)) {
            generateForKpiSnapshot(snapshot, generation);
        }
        return generationResult(generation, "KPI signals generated.");
    }

    @Override
    @ServiceMethodAuthority(type = {ServiceMethodType.INSERT, ServiceMethodType.UPDATE})
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    public DefaultResult<List<MdPerformanceSignal>> generateKpiSignalsBySnapshotOid(String snapshotOid)
            throws ServiceException {
        if (StringUtils.isBlank(snapshotOid)) {
            throw new ServiceException("Snapshot OID is required.");
        }
        MdKpiScoreSnapshot key = new MdKpiScoreSnapshot();
        key.setOid(snapshotOid);
        MdKpiScoreSnapshot snapshot = snapshotService.selectByEntityPrimaryKey(key).getValueEmptyThrowMessage();
        List<MdPerformanceSignal> signals = generateForKpiSnapshot(snapshot, null);
        DefaultResult<List<MdPerformanceSignal>> result = new DefaultResult<>();
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(signals);
        result.setMessage("KPI signals generated.");
        return result;
    }

    @Override
    @ServiceMethodAuthority(type = {ServiceMethodType.INSERT, ServiceMethodType.UPDATE})
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    public DefaultResult<PerformanceSignalGenerationResult> generateOkrSignals(Map<String, Object> params)
            throws ServiceException {
        Map<String, Object> query = params == null ? new HashMap<>() : new HashMap<>(params);
        List<MdOkrSnapshot> snapshots = okrSnapshotService
                .selectListByParams(query, "PERIOD_KEY, OBJECTIVE_OID", "ASC").getValue();
        PerformanceSignalGenerationResult generation = new PerformanceSignalGenerationResult();
        generation.setSnapshotCount(snapshots == null ? 0 : snapshots.size());
        for (MdOkrSnapshot snapshot : safeList(snapshots)) {
            saveSignal(buildOkrScoreStatusSignal(snapshot), generation);
        }
        return generationResult(generation, "OKR signals generated.");
    }

    @Override
    @ServiceMethodAuthority(type = {ServiceMethodType.INSERT, ServiceMethodType.UPDATE})
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    public DefaultResult<PerformanceSignalGenerationResult> generateStrategySignals(Map<String, Object> params)
            throws ServiceException {
        Map<String, Object> query = params == null ? new HashMap<>() : new HashMap<>(params);
        List<MdStrategySnapshot> snapshots = strategySnapshotService
                .selectListByParams(query, "PERIOD_TYPE, PERIOD_KEY, WORKSPACE_OID", "ASC").getValue();
        PerformanceSignalGenerationResult generation = new PerformanceSignalGenerationResult();
        generation.setSnapshotCount(snapshots == null ? 0 : snapshots.size());
        for (MdStrategySnapshot snapshot : safeList(snapshots)) {
            saveSignal(buildStrategyScoreStatusSignal(snapshot), generation);
        }
        return generationResult(generation, "Strategy signals generated.");
    }

    @Override
    @ServiceMethodAuthority(type = {ServiceMethodType.INSERT, ServiceMethodType.UPDATE})
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    public DefaultResult<PerformanceSignalGenerationResult> generateActionSignals(Map<String, Object> params)
            throws ServiceException {
        Map<String, Object> query = params == null ? new HashMap<>() : new HashMap<>(params);
        List<MdActionItem> items = actionItemService.selectListByParams(query, "END_DATE, PLAN_OID, SORT_NO", "ASC").getValue();
        PerformanceSignalGenerationResult generation = new PerformanceSignalGenerationResult();
        generation.setSnapshotCount(items == null ? 0 : items.size());
        for (MdActionItem item : safeList(items)) {
            if (item.getEndDate() != null) {
                saveSignal(buildActionOverdueSignal(item), generation);
            }
        }
        return generationResult(generation, "Action signals generated.");
    }

    private List<MdPerformanceSignal> generateForKpiSnapshot(MdKpiScoreSnapshot snapshot,
            PerformanceSignalGenerationResult generation) throws ServiceException {
        MdKpi kpi = loadKpi(snapshot.getKpiOid());
        MdKpiScoreSnapshot previous = loadPreviousSnapshot(snapshot);
        List<MdPerformanceSignal> signals = new ArrayList<>();
        signals.add(saveSignal(buildScoreStatusSignal(kpi, snapshot), generation));
        if (snapshot.getRawTarget() != null && snapshot.getRawActual() != null) {
            signals.add(saveSignal(buildVarianceSignal(kpi, snapshot), generation));
        }
        if (previous != null && previous.getScoreValue() != null && snapshot.getScoreValue() != null) {
            signals.add(saveSignal(buildTrendSignal(kpi, snapshot, previous), generation));
        }
        return signals;
    }

    private MdPerformanceSignal buildScoreStatusSignal(MdKpi kpi, MdKpiScoreSnapshot snapshot) throws ServiceException {
        String status = StringUtils.defaultIfBlank(snapshot.getScoreStatus(), "UNKNOWN").toUpperCase(Locale.ROOT);
        MdPerformanceSignal signal = baseSignal(SOURCE_KPI, snapshot.getOid(), kpi.getKpiCode(), kpi.getKpiName(),
                snapshot.getPeriodType(), snapshot.getPeriodKey(), SIGNAL_SCORE_STATUS, KPI_GENERATOR_VERSION);
        signal.setOwnerAccount(snapshot.getAccount());
        signal.setOrgOid(snapshot.getOrgOid());
        signal.setSnapshotOid(snapshot.getOid());
        signal.setScoreValue(snapshot.getScoreValue());
        signal.setTargetValue(snapshot.getRawTarget());
        signal.setActualValue(snapshot.getRawActual());
        signal.setStatusCode(status);
        applyStatusLifecycle(signal, status);
        signal.setEvidenceJson(toJson(kpiEvidence(kpi, snapshot, null)));
        signal.setExplanationInput("KPI " + kpi.getKpiCode() + " score status is " + status + ".");
        return signal;
    }

    private MdPerformanceSignal buildVarianceSignal(MdKpi kpi, MdKpiScoreSnapshot snapshot) throws ServiceException {
        BigDecimal target = snapshot.getRawTarget();
        BigDecimal actual = snapshot.getRawActual();
        BigDecimal variance = actual.subtract(target);
        BigDecimal varianceRate = target.compareTo(BigDecimal.ZERO) == 0 ? null
                : variance.divide(target.abs(), 6, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        String mode = StringUtils.defaultIfBlank(kpi.getManagementMode(), "BIGGER").toUpperCase(Locale.ROOT);
        boolean normal;
        String status;
        if ("SMALLER".equals(mode)) {
            normal = actual.compareTo(target) <= 0;
            status = normal ? "ON_TARGET" : "ABOVE_TARGET";
        } else if ("QUASI".equals(mode)) {
            BigDecimal range = kpi.getQuasiRange() == null ? BigDecimal.ZERO : kpi.getQuasiRange();
            normal = variance.abs().compareTo(range) <= 0;
            status = normal ? "IN_RANGE" : "OUT_OF_RANGE";
        } else {
            normal = actual.compareTo(target) >= 0;
            status = normal ? "ON_TARGET" : "BELOW_TARGET";
        }
        BigDecimal unfavorableRate = varianceRate == null ? BigDecimal.ZERO : varianceRate.abs();
        String risk = normal ? "LOW" : riskByMagnitude(unfavorableRate);
        MdPerformanceSignal signal = baseSignal(SOURCE_KPI, snapshot.getOid(), kpi.getKpiCode(), kpi.getKpiName(),
                snapshot.getPeriodType(), snapshot.getPeriodKey(), SIGNAL_TARGET_VARIANCE, KPI_GENERATOR_VERSION);
        signal.setOwnerAccount(snapshot.getAccount());
        signal.setOrgOid(snapshot.getOrgOid());
        signal.setSnapshotOid(snapshot.getOid());
        signal.setScoreValue(snapshot.getScoreValue());
        signal.setTargetValue(target);
        signal.setActualValue(actual);
        signal.setVarianceValue(variance);
        signal.setVarianceRate(varianceRate);
        signal.setStatusCode(status);
        applyLifecycle(signal, risk, normal);
        Map<String, Object> evidence = kpiEvidence(kpi, snapshot, null);
        evidence.put("managementMode", mode);
        evidence.put("varianceValue", variance);
        evidence.put("varianceRate", varianceRate);
        signal.setEvidenceJson(toJson(evidence));
        signal.setExplanationInput("KPI " + kpi.getKpiCode() + " target variance status is " + status + ".");
        return signal;
    }

    private MdPerformanceSignal buildTrendSignal(MdKpi kpi, MdKpiScoreSnapshot snapshot,
            MdKpiScoreSnapshot previous) throws ServiceException {
        BigDecimal delta = snapshot.getScoreValue().subtract(previous.getScoreValue());
        String trend = delta.compareTo(BigDecimal.ZERO) < 0 ? "DOWN"
                : delta.compareTo(BigDecimal.ZERO) > 0 ? "UP" : "STABLE";
        boolean resolved = !"DOWN".equals(trend);
        String risk = resolved ? "LOW" : riskByMagnitude(delta.abs());
        MdPerformanceSignal signal = baseSignal(SOURCE_KPI, snapshot.getOid(), kpi.getKpiCode(), kpi.getKpiName(),
                snapshot.getPeriodType(), snapshot.getPeriodKey(), SIGNAL_TREND_DOWN, KPI_GENERATOR_VERSION);
        signal.setOwnerAccount(snapshot.getAccount());
        signal.setOrgOid(snapshot.getOrgOid());
        signal.setSnapshotOid(snapshot.getOid());
        signal.setScoreValue(snapshot.getScoreValue());
        signal.setVarianceValue(delta);
        signal.setTrendCode(trend);
        signal.setStatusCode(trend);
        applyLifecycle(signal, risk, resolved);
        Map<String, Object> evidence = kpiEvidence(kpi, snapshot, previous);
        evidence.put("previousScore", previous.getScoreValue());
        evidence.put("scoreDelta", delta);
        signal.setEvidenceJson(toJson(evidence));
        signal.setExplanationInput("KPI " + kpi.getKpiCode() + " score trend is " + trend + ".");
        return signal;
    }

    private MdPerformanceSignal buildOkrScoreStatusSignal(MdOkrSnapshot snapshot) throws ServiceException {
        MdOkrObjective objective = loadOkrObjective(snapshot.getObjectiveOid());
        String status = StringUtils.defaultIfBlank(snapshot.getScoreStatus(), "UNKNOWN").toUpperCase(Locale.ROOT);
        MdPerformanceSignal signal = baseSignal(SOURCE_OKR, snapshot.getOid(), snapshot.getObjectiveOid(),
                objective.getObjectiveName(), PeriodKeyUtils.DAY, snapshot.getPeriodKey(), SIGNAL_SCORE_STATUS,
                OKR_GENERATOR_VERSION);
        signal.setSnapshotOid(snapshot.getOid());
        signal.setScoreValue(snapshot.getProgressValue());
        signal.setActualValue(snapshot.getProgressValue());
        signal.setStatusCode(status);
        applyStatusLifecycle(signal, status);
        Map<String, Object> evidence = new LinkedHashMap<>();
        evidence.put("source", "OKR_SNAPSHOT");
        evidence.put("objectiveOid", snapshot.getObjectiveOid());
        evidence.put("snapshotOid", snapshot.getOid());
        evidence.put("progressValue", snapshot.getProgressValue());
        evidence.put("confidenceScore", snapshot.getConfidenceScore());
        evidence.put("scoreStatus", snapshot.getScoreStatus());
        evidence.put("snapshotAt", snapshot.getSnapshotAt());
        signal.setEvidenceJson(toJson(evidence));
        signal.setExplanationInput("OKR objective " + snapshot.getObjectiveOid() + " score status is " + status + ".");
        return signal;
    }

    private MdPerformanceSignal buildStrategyScoreStatusSignal(MdStrategySnapshot snapshot) throws ServiceException {
        MdStrategyWorkspace workspace = loadStrategyWorkspace(snapshot.getWorkspaceOid());
        String status = scoreStatusByValue(snapshot.getScoreValue());
        MdPerformanceSignal signal = baseSignal(SOURCE_STRATEGY, snapshot.getOid(), workspace.getWorkspaceCode(),
                workspace.getWorkspaceName(), snapshot.getPeriodType(), snapshot.getPeriodKey(), SIGNAL_SCORE_STATUS,
                STRATEGY_GENERATOR_VERSION);
        signal.setSnapshotOid(snapshot.getOid());
        signal.setScoreValue(snapshot.getScoreValue());
        signal.setActualValue(snapshot.getScoreValue());
        signal.setStatusCode(status);
        applyStatusLifecycle(signal, status);
        Map<String, Object> evidence = new LinkedHashMap<>();
        evidence.put("source", "STRATEGY_SNAPSHOT");
        evidence.put("workspaceOid", snapshot.getWorkspaceOid());
        evidence.put("snapshotOid", snapshot.getOid());
        evidence.put("scoreValue", snapshot.getScoreValue());
        evidence.put("kpiCount", snapshot.getKpiCount());
        evidence.put("okrCount", snapshot.getOkrCount());
        evidence.put("snapshotAt", snapshot.getSnapshotAt());
        signal.setEvidenceJson(toJson(evidence));
        signal.setExplanationInput("Strategy workspace " + workspace.getWorkspaceCode() + " score status is " + status + ".");
        return signal;
    }

    private MdPerformanceSignal buildActionOverdueSignal(MdActionItem item) throws ServiceException {
        LocalDate endDate = toLocalDate(item.getEndDate());
        LocalDate today = LocalDate.now();
        boolean done = item.getDoneDate() != null || isCompletedStatus(item.getStatus());
        boolean overdue = !done && endDate.isBefore(today);
        long daysOverdue = overdue ? ChronoUnit.DAYS.between(endDate, today) : 0L;
        String status = done ? "DONE" : overdue ? "OVERDUE" : "ON_TRACK";
        MdPerformanceSignal signal = baseSignal(SOURCE_ACTION, item.getOid(), item.getOid(), item.getItemName(),
                PeriodKeyUtils.DAY, endDate.toString(), SIGNAL_OVERDUE, ACTION_GENERATOR_VERSION);
        signal.setRelatedActionOid(item.getOid());
        signal.setActualValue(item.getProgressValue());
        signal.setVarianceValue(BigDecimal.valueOf(daysOverdue));
        signal.setStatusCode(status);
        applyLifecycle(signal, overdue ? overdueRisk(daysOverdue) : "LOW", !overdue);
        Map<String, Object> evidence = new LinkedHashMap<>();
        evidence.put("source", "ACTION_ITEM");
        evidence.put("actionItemOid", item.getOid());
        evidence.put("planOid", item.getPlanOid());
        evidence.put("actionStage", item.getActionStage());
        evidence.put("status", item.getStatus());
        evidence.put("progressValue", item.getProgressValue());
        evidence.put("startDate", item.getStartDate());
        evidence.put("endDate", item.getEndDate());
        evidence.put("doneDate", item.getDoneDate());
        evidence.put("daysOverdue", daysOverdue);
        signal.setEvidenceJson(toJson(evidence));
        signal.setExplanationInput("Action item " + item.getItemName() + " overdue status is " + status + ".");
        return signal;
    }

    private MdPerformanceSignal baseSignal(String sourceType, String sourceOid, String sourceCode, String sourceName,
            String periodType, String periodKey, String signalType, String generatorVersion) throws ServiceException {
        DateRange range = resolveDateRange(periodType, periodKey);
        MdPerformanceSignal signal = new MdPerformanceSignal();
        signal.setSignalType(signalType);
        signal.setSourceType(sourceType);
        signal.setSourceOid(sourceOid);
        signal.setSourceCode(sourceCode);
        signal.setSourceName(sourceName);
        signal.setPeriodType(periodType);
        signal.setPeriodKey(periodKey);
        signal.setStartDate(java.sql.Date.valueOf(range.start()));
        signal.setEndDate(java.sql.Date.valueOf(range.end()));
        signal.setGeneratorVersion(generatorVersion);
        signal.setGeneratedAt(new Date());
        return signal;
    }

    private void applyStatusLifecycle(MdPerformanceSignal signal, String status) {
        boolean resolved = "GOOD".equals(status);
        String risk = switch (status) {
            case "BAD" -> "HIGH";
            case "WARNING", "UNKNOWN" -> "MEDIUM";
            default -> "LOW";
        };
        applyLifecycle(signal, risk, resolved);
    }

    private void applyLifecycle(MdPerformanceSignal signal, String risk, boolean resolved) {
        signal.setRiskLevel(risk);
        signal.setSignalStatus(resolved ? RESOLVED : OPEN);
        signal.setResolvedAt(resolved ? new Date() : null);
    }

    private MdPerformanceSignal saveSignal(MdPerformanceSignal signal, PerformanceSignalGenerationResult generation)
            throws ServiceException {
        MdPerformanceSignal existing = loadExisting(signal);
        boolean inserted = existing == null;
        if (inserted) {
            signal = signalService.insert(signal).getValueEmptyThrowMessage();
        } else {
            signal.setOid(existing.getOid());
            signalService.update(signal).getValueEmptyThrowMessage();
            if (OPEN.equals(signal.getSignalStatus())) {
                signalMapper.reopenSignal(signal);
            }
            signal = signalService.selectByEntityPrimaryKey(signal).getValueEmptyThrowMessage();
        }
        if (generation != null) {
            if (inserted) generation.setInsertedCount(generation.getInsertedCount() + 1);
            else generation.setUpdatedCount(generation.getUpdatedCount() + 1);
            if (OPEN.equals(signal.getSignalStatus())) generation.setOpenCount(generation.getOpenCount() + 1);
            else generation.setResolvedCount(generation.getResolvedCount() + 1);
        }
        return signal;
    }

    private MdPerformanceSignal loadExisting(MdPerformanceSignal signal) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("sourceType", signal.getSourceType());
        params.put("sourceOid", signal.getSourceOid());
        params.put("signalType", signal.getSignalType());
        params.put("periodType", signal.getPeriodType());
        params.put("periodKey", signal.getPeriodKey());
        List<MdPerformanceSignal> list = signalService.selectListByParams(params).getValue();
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    private MdKpi loadKpi(String kpiOid) throws ServiceException {
        MdKpi key = new MdKpi();
        key.setOid(kpiOid);
        return kpiService.selectByEntityPrimaryKey(key).getValueEmptyThrowMessage();
    }

    private MdOkrObjective loadOkrObjective(String objectiveOid) throws ServiceException {
        MdOkrObjective key = new MdOkrObjective();
        key.setOid(objectiveOid);
        return okrObjectiveService.selectByEntityPrimaryKey(key).getValueEmptyThrowMessage();
    }

    private MdStrategyWorkspace loadStrategyWorkspace(String workspaceOid) throws ServiceException {
        MdStrategyWorkspace key = new MdStrategyWorkspace();
        key.setOid(workspaceOid);
        return strategyWorkspaceService.selectByEntityPrimaryKey(key).getValueEmptyThrowMessage();
    }

    private MdKpiScoreSnapshot loadPreviousSnapshot(MdKpiScoreSnapshot current) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("kpiOid", current.getKpiOid());
        params.put("periodType", current.getPeriodType());
        params.put("dataForType", current.getDataForType());
        if (StringUtils.isNotBlank(current.getAccount())) params.put("account", current.getAccount());
        if (StringUtils.isNotBlank(current.getOrgOid())) params.put("orgOid", current.getOrgOid());
        List<MdKpiScoreSnapshot> list = snapshotService.selectListByParams(params, "PERIOD_KEY", "ASC").getValue();
        MdKpiScoreSnapshot previous = null;
        for (MdKpiScoreSnapshot item : safeList(list)) {
            if (Strings.CS.equals(item.getOid(), current.getOid())) return previous;
            if (StringUtils.defaultString(item.getPeriodKey()).compareTo(StringUtils.defaultString(current.getPeriodKey())) < 0) previous = item;
        }
        return previous;
    }

    private Map<String, Object> kpiEvidence(MdKpi kpi, MdKpiScoreSnapshot snapshot,
            MdKpiScoreSnapshot previous) {
        Map<String, Object> evidence = new LinkedHashMap<>();
        evidence.put("source", "KPI_SCORE_SNAPSHOT");
        evidence.put("kpiOid", kpi.getOid());
        evidence.put("kpiCode", kpi.getKpiCode());
        evidence.put("snapshotOid", snapshot.getOid());
        evidence.put("dataForType", snapshot.getDataForType());
        evidence.put("account", snapshot.getAccount());
        evidence.put("orgOid", snapshot.getOrgOid());
        evidence.put("scoreStatus", snapshot.getScoreStatus());
        evidence.put("scoreValue", snapshot.getScoreValue());
        evidence.put("targetValue", snapshot.getRawTarget());
        evidence.put("actualValue", snapshot.getRawActual());
        evidence.put("formulaOid", snapshot.getFormulaOid());
        evidence.put("formulaVersionNo", snapshot.getFormulaVersionNo());
        evidence.put("calculatedAt", snapshot.getCalculatedAt());
        if (previous != null) evidence.put("previousSnapshotOid", previous.getOid());
        return evidence;
    }

    private String toJson(Map<String, Object> value) throws ServiceException {
        try {
            return LoadResources.getObjectMapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new ServiceException("Unable to serialize signal evidence.");
        }
    }

    private DefaultResult<PerformanceSignalGenerationResult> generationResult(
            PerformanceSignalGenerationResult generation, String message) {
        DefaultResult<PerformanceSignalGenerationResult> result = new DefaultResult<>();
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(generation);
        result.setMessage(message);
        return result;
    }

    private String scoreStatusByValue(BigDecimal value) {
        if (value == null) return "UNKNOWN";
        if (value.compareTo(new BigDecimal("70")) >= 0) return "GOOD";
        if (value.compareTo(new BigDecimal("40")) >= 0) return "WARNING";
        return "BAD";
    }

    private String riskByMagnitude(BigDecimal magnitude) {
        if (magnitude.compareTo(new BigDecimal("20")) >= 0) return "HIGH";
        if (magnitude.compareTo(new BigDecimal("10")) >= 0) return "MEDIUM";
        return "LOW";
    }

    private String overdueRisk(long daysOverdue) {
        if (daysOverdue >= 30) return "HIGH";
        if (daysOverdue >= 7) return "MEDIUM";
        return "LOW";
    }

    private boolean isCompletedStatus(String status) {
        String value = StringUtils.defaultString(status).toUpperCase(Locale.ROOT);
        return "DONE".equals(value) || "COMPLETED".equals(value) || "CLOSED".equals(value) || "RESOLVED".equals(value);
    }

    private DateRange resolveDateRange(String periodType, String periodKey) throws ServiceException {
        try {
            return new DateRange(
                    PeriodKeyUtils.parseStart(periodType, periodKey),
                    PeriodKeyUtils.end(periodType, periodKey));
        } catch (Exception e) {
            throw new ServiceException("Invalid performance signal period: " + periodType + " / " + periodKey);
        }
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private <T> List<T> safeList(List<T> values) {
        return values == null ? List.of() : values;
    }

    private record DateRange(LocalDate start, LocalDate end) {
    }
}