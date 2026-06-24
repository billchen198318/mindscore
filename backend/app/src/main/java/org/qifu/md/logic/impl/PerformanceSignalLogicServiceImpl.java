package org.qifu.md.logic.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
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
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiScoreSnapshot;
import org.qifu.md.entity.MdPerformanceSignal;
import org.qifu.md.logic.IPerformanceSignalLogicService;
import org.qifu.md.mapper.MdPerformanceSignalMapper;
import org.qifu.md.model.PerformanceSignalGenerationResult;
import org.qifu.md.service.IMdKpiScoreSnapshotService;
import org.qifu.md.service.IMdKpiService;
import org.qifu.md.service.IMdPerformanceSignalService;
import org.qifu.util.LoadResources;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@ServiceAuthority(check = true)
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class PerformanceSignalLogicServiceImpl implements IPerformanceSignalLogicService {
    private static final String SOURCE_KPI = "KPI";
    private static final String SIGNAL_SCORE_STATUS = "SCORE_STATUS";
    private static final String SIGNAL_TARGET_VARIANCE = "TARGET_VARIANCE";
    private static final String SIGNAL_TREND_DOWN = "TREND_DOWN";
    private static final String OPEN = "OPEN";
    private static final String RESOLVED = "RESOLVED";
    private static final String GENERATOR_VERSION = "KPI_V1";

    private final IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> snapshotService;
    private final IMdKpiService<MdKpi, String> kpiService;
    private final IMdPerformanceSignalService<MdPerformanceSignal, String> signalService;
    private final MdPerformanceSignalMapper signalMapper;

    public PerformanceSignalLogicServiceImpl(
            IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> snapshotService,
            IMdKpiService<MdKpi, String> kpiService,
            IMdPerformanceSignalService<MdPerformanceSignal, String> signalService,
            MdPerformanceSignalMapper signalMapper) {
        this.snapshotService = snapshotService;
        this.kpiService = kpiService;
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
            generateForSnapshot(snapshot, generation);
        }
        DefaultResult<PerformanceSignalGenerationResult> result = new DefaultResult<>();
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(generation);
        result.setMessage("KPI signals generated.");
        return result;
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
        List<MdPerformanceSignal> signals = generateForSnapshot(snapshot, null);
        DefaultResult<List<MdPerformanceSignal>> result = new DefaultResult<>();
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(signals);
        result.setMessage("KPI signals generated.");
        return result;
    }

    private List<MdPerformanceSignal> generateForSnapshot(MdKpiScoreSnapshot snapshot,
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
        boolean resolved = "GOOD".equals(status);
        String risk = switch (status) {
            case "BAD" -> "HIGH";
            case "WARNING", "UNKNOWN" -> "MEDIUM";
            default -> "LOW";
        };
        MdPerformanceSignal signal = baseSignal(kpi, snapshot, SIGNAL_SCORE_STATUS);
        signal.setScoreValue(snapshot.getScoreValue());
        signal.setTargetValue(snapshot.getRawTarget());
        signal.setActualValue(snapshot.getRawActual());
        signal.setStatusCode(status);
        applyLifecycle(signal, risk, resolved);
        signal.setEvidenceJson(toJson(evidence(kpi, snapshot, null)));
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
        MdPerformanceSignal signal = baseSignal(kpi, snapshot, SIGNAL_TARGET_VARIANCE);
        signal.setScoreValue(snapshot.getScoreValue());
        signal.setTargetValue(target);
        signal.setActualValue(actual);
        signal.setVarianceValue(variance);
        signal.setVarianceRate(varianceRate);
        signal.setStatusCode(status);
        applyLifecycle(signal, risk, normal);
        Map<String, Object> evidence = evidence(kpi, snapshot, null);
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
        MdPerformanceSignal signal = baseSignal(kpi, snapshot, SIGNAL_TREND_DOWN);
        signal.setScoreValue(snapshot.getScoreValue());
        signal.setVarianceValue(delta);
        signal.setTrendCode(trend);
        signal.setStatusCode(trend);
        applyLifecycle(signal, risk, resolved);
        Map<String, Object> evidence = evidence(kpi, snapshot, previous);
        evidence.put("previousScore", previous.getScoreValue());
        evidence.put("scoreDelta", delta);
        signal.setEvidenceJson(toJson(evidence));
        signal.setExplanationInput("KPI " + kpi.getKpiCode() + " score trend is " + trend + ".");
        return signal;
    }

    private MdPerformanceSignal baseSignal(MdKpi kpi, MdKpiScoreSnapshot snapshot, String signalType)
            throws ServiceException {
        DateRange range = resolveDateRange(snapshot.getPeriodType(), snapshot.getPeriodKey());
        MdPerformanceSignal signal = new MdPerformanceSignal();
        signal.setSignalType(signalType);
        signal.setSourceType(SOURCE_KPI);
        signal.setSourceOid(snapshot.getOid());
        signal.setSourceCode(kpi.getKpiCode());
        signal.setSourceName(kpi.getKpiName());
        signal.setPeriodType(snapshot.getPeriodType());
        signal.setPeriodKey(snapshot.getPeriodKey());
        signal.setStartDate(java.sql.Date.valueOf(range.start()));
        signal.setEndDate(java.sql.Date.valueOf(range.end()));
        signal.setOwnerAccount(snapshot.getAccount());
        signal.setOrgOid(snapshot.getOrgOid());
        signal.setSnapshotOid(snapshot.getOid());
        signal.setGeneratorVersion(GENERATOR_VERSION);
        signal.setGeneratedAt(new Date());
        return signal;
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

    private Map<String, Object> evidence(MdKpi kpi, MdKpiScoreSnapshot snapshot,
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

    private String riskByMagnitude(BigDecimal magnitude) {
        if (magnitude.compareTo(new BigDecimal("20")) >= 0) return "HIGH";
        if (magnitude.compareTo(new BigDecimal("10")) >= 0) return "MEDIUM";
        return "LOW";
    }

    private DateRange resolveDateRange(String periodType, String periodKey) throws ServiceException {
        try {
            String type = StringUtils.defaultString(periodType).toUpperCase(Locale.ROOT);
            return switch (type) {
                case "DAY" -> {
                    LocalDate date = LocalDate.parse(periodKey);
                    yield new DateRange(date, date);
                }
                case "WEEK" -> {
                    String[] parts = periodKey.split("-W");
                    int year = Integer.parseInt(parts[0]);
                    int week = Integer.parseInt(parts[1]);
                    LocalDate start = LocalDate.of(year, 1, 4)
                            .with(WeekFields.ISO.weekOfWeekBasedYear(), week)
                            .with(WeekFields.ISO.dayOfWeek(), 1);
                    yield new DateRange(start, start.plusDays(6));
                }
                case "MONTH" -> {
                    LocalDate start = LocalDate.parse(periodKey + "-01");
                    yield new DateRange(start, start.with(TemporalAdjusters.lastDayOfMonth()));
                }
                case "QUARTER" -> {
                    String[] parts = periodKey.split("-Q");
                    int month = (Integer.parseInt(parts[1]) - 1) * 3 + 1;
                    LocalDate start = LocalDate.of(Integer.parseInt(parts[0]), month, 1);
                    yield new DateRange(start, start.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth()));
                }
                case "HALFYEAR" -> {
                    String[] parts = periodKey.split("-H");
                    int month = "1".equals(parts[1]) ? 1 : 7;
                    LocalDate start = LocalDate.of(Integer.parseInt(parts[0]), month, 1);
                    yield new DateRange(start, start.plusMonths(5).with(TemporalAdjusters.lastDayOfMonth()));
                }
                case "YEAR" -> {
                    LocalDate start = LocalDate.of(Integer.parseInt(periodKey), 1, 1);
                    yield new DateRange(start, start.with(TemporalAdjusters.lastDayOfYear()));
                }
                default -> throw new IllegalArgumentException("Unsupported period type");
            };
        } catch (Exception e) {
            throw new ServiceException("Invalid KPI snapshot period: " + periodType + " / " + periodKey);
        }
    }

    private <T> List<T> safeList(List<T> values) {
        return values == null ? List.of() : values;
    }

    private record DateRange(LocalDate start, LocalDate end) {
    }
}