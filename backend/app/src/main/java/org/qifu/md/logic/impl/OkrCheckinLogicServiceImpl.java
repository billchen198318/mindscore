package org.qifu.md.logic.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.message.BaseSystemMessage;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.ServiceMethodAuthority;
import org.qifu.base.model.ServiceMethodType;
import org.qifu.md.entity.MdOkrCheckin;
import org.qifu.md.entity.MdOkrKeyResult;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.entity.MdOkrSnapshot;
import org.qifu.md.logic.IOkrCheckinLogicService;
import org.qifu.md.service.IMdOkrCheckinService;
import org.qifu.md.service.IMdOkrKeyResultService;
import org.qifu.md.service.IMdOkrObjectiveService;
import org.qifu.md.service.IMdOkrSnapshotService;
import org.qifu.util.LoadResources;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class OkrCheckinLogicServiceImpl implements IOkrCheckinLogicService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final IMdOkrCheckinService<MdOkrCheckin, String> mdOkrCheckinService;
    private final IMdOkrKeyResultService<MdOkrKeyResult, String> mdOkrKeyResultService;
    private final IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService;
    private final IMdOkrSnapshotService<MdOkrSnapshot, String> mdOkrSnapshotService;

    public OkrCheckinLogicServiceImpl(IMdOkrCheckinService<MdOkrCheckin, String> mdOkrCheckinService,
            IMdOkrKeyResultService<MdOkrKeyResult, String> mdOkrKeyResultService,
            IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService,
            IMdOkrSnapshotService<MdOkrSnapshot, String> mdOkrSnapshotService) {
        this.mdOkrCheckinService = mdOkrCheckinService;
        this.mdOkrKeyResultService = mdOkrKeyResultService;
        this.mdOkrObjectiveService = mdOkrObjectiveService;
        this.mdOkrSnapshotService = mdOkrSnapshotService;
    }

    @ServiceMethodAuthority(type = ServiceMethodType.INSERT)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<MdOkrCheckin> create(MdOkrCheckin entity) throws ServiceException {
        if (entity == null || StringUtils.isBlank(entity.getKrOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        normalize(entity);
        DefaultResult<MdOkrCheckin> result = this.mdOkrCheckinService.insert(entity);
        MdOkrCheckin saved = result.getValueEmptyThrowMessage();
        MdOkrKeyResult kr = syncKeyResult(saved);
        MdOkrObjective objective = rollupObjective(kr.getObjectiveOid());
        saveObjectiveSnapshot(objective, saved);
        return result;
    }

    @ServiceMethodAuthority(type = ServiceMethodType.DELETE)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<Boolean> delete(MdOkrCheckin entity) throws ServiceException {
        if (entity == null || StringUtils.isBlank(entity.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        MdOkrCheckin dbEntity = this.mdOkrCheckinService.selectByEntityPrimaryKey(entity).getValueEmptyThrowMessage();
        DefaultResult<Boolean> result = this.mdOkrCheckinService.delete(entity);
        MdOkrKeyResult kr = reloadKeyResultByLatestCheckin(dbEntity.getKrOid());
        MdOkrObjective objective = rollupObjective(kr.getObjectiveOid());
        saveObjectiveSnapshot(objective, dbEntity);
        return result;
    }

    private void normalize(MdOkrCheckin entity) {
        if (StringUtils.isBlank(entity.getCommentText())) {
            entity.setCommentText(null);
        }
    }

    private MdOkrKeyResult syncKeyResult(MdOkrCheckin checkin) throws ServiceException {
        MdOkrKeyResult key = new MdOkrKeyResult();
        key.setOid(checkin.getKrOid());
        MdOkrKeyResult kr = this.mdOkrKeyResultService.selectByEntityPrimaryKey(key).getValueEmptyThrowMessage();
        kr.setCurrentValue(checkin.getCurrentValue());
        kr.setProgressValue(checkin.getProgressValue());
        this.mdOkrKeyResultService.update(kr).getValueEmptyThrowMessage();
        return kr;
    }

    private MdOkrKeyResult reloadKeyResultByLatestCheckin(String krOid) throws ServiceException {
        MdOkrKeyResult key = new MdOkrKeyResult();
        key.setOid(krOid);
        MdOkrKeyResult kr = this.mdOkrKeyResultService.selectByEntityPrimaryKey(key).getValueEmptyThrowMessage();

        Map<String, Object> params = new HashMap<>();
        params.put("krOid", krOid);
        List<MdOkrCheckin> checkins = this.mdOkrCheckinService.selectListByParams(params, "CHECKIN_DATE, CDATE", "DESC").getValue();
        if (checkins != null && !checkins.isEmpty()) {
            MdOkrCheckin latest = checkins.get(0);
            kr.setCurrentValue(latest.getCurrentValue());
            kr.setProgressValue(latest.getProgressValue());
        } else {
            kr.setCurrentValue(null);
            kr.setProgressValue(BigDecimal.ZERO);
        }
        this.mdOkrKeyResultService.update(kr).getValueEmptyThrowMessage();
        return kr;
    }

    private MdOkrObjective rollupObjective(String objectiveOid) throws ServiceException {
        if (StringUtils.isBlank(objectiveOid)) {
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("objectiveOid", objectiveOid);
        params.put("status", "ACTIVE");
        List<MdOkrKeyResult> krList = this.mdOkrKeyResultService.selectListByParams(params).getValue();
        if (krList == null || krList.isEmpty()) {
            return loadObjective(objectiveOid);
        }

        BigDecimal weightTotal = BigDecimal.ZERO;
        BigDecimal weightedProgress = BigDecimal.ZERO;
        BigDecimal simpleProgress = BigDecimal.ZERO;
        int progressCount = 0;

        for (MdOkrKeyResult kr : krList) {
            BigDecimal progress = kr.getProgressValue();
            if (progress == null) {
                continue;
            }
            progressCount++;
            simpleProgress = simpleProgress.add(progress);
            BigDecimal weight = kr.getWeightValue() == null ? BigDecimal.ZERO : kr.getWeightValue();
            if (weight.compareTo(BigDecimal.ZERO) > 0) {
                weightTotal = weightTotal.add(weight);
                weightedProgress = weightedProgress.add(progress.multiply(weight));
            }
        }

        if (progressCount == 0) {
            return loadObjective(objectiveOid);
        }

        BigDecimal objectiveProgress = weightTotal.compareTo(BigDecimal.ZERO) > 0
                ? weightedProgress.divide(weightTotal, 4, RoundingMode.HALF_UP)
                : simpleProgress.divide(new BigDecimal(progressCount), 4, RoundingMode.HALF_UP);
        if (objectiveProgress.compareTo(ONE_HUNDRED) > 0) {
            objectiveProgress = ONE_HUNDRED;
        }

        MdOkrObjective objective = loadObjective(objectiveOid);
        objective.setProgressValue(objectiveProgress);
        this.mdOkrObjectiveService.update(objective).getValueEmptyThrowMessage();
        return loadObjective(objectiveOid);
    }

    private MdOkrObjective loadObjective(String objectiveOid) throws ServiceException {
        MdOkrObjective objectiveKey = new MdOkrObjective();
        objectiveKey.setOid(objectiveOid);
        return this.mdOkrObjectiveService.selectByEntityPrimaryKey(objectiveKey).getValueEmptyThrowMessage();
    }

    private void saveObjectiveSnapshot(MdOkrObjective objective, MdOkrCheckin checkin) throws ServiceException {
        if (objective == null || checkin == null) {
            return;
        }
        MdOkrSnapshot snapshot = new MdOkrSnapshot();
        snapshot.setObjectiveOid(objective.getOid());
        snapshot.setPeriodKey(toPeriodKey(checkin.getCheckinDate()));
        snapshot.setProgressValue(objective.getProgressValue());
        snapshot.setConfidenceScore(resolveConfidenceScore(objective, checkin));
        snapshot.setScoreStatus(resolveScoreStatus(objective.getProgressValue()));
        snapshot.setCalculationTrace(toCalculationTrace(objective, checkin));
        snapshot.setSnapshotAt(new Date());
        saveOrUpdateSnapshot(snapshot);
    }

    private BigDecimal resolveConfidenceScore(MdOkrObjective objective, MdOkrCheckin checkin) {
        return checkin.getConfidenceScore() == null ? objective.getConfidenceScore() : checkin.getConfidenceScore();
    }

    private void saveOrUpdateSnapshot(MdOkrSnapshot snapshot) throws ServiceException {
        MdOkrSnapshot existing = loadSnapshotByKey(snapshot);
        if (existing == null) {
            this.mdOkrSnapshotService.insert(snapshot).getValueEmptyThrowMessage();
            return;
        }
        snapshot.setOid(existing.getOid());
        this.mdOkrSnapshotService.update(snapshot).getValueEmptyThrowMessage();
    }

    private MdOkrSnapshot loadSnapshotByKey(MdOkrSnapshot snapshot) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("objectiveOid", snapshot.getObjectiveOid());
        params.put("periodKey", snapshot.getPeriodKey());
        List<MdOkrSnapshot> snapshots = this.mdOkrSnapshotService.selectListByParams(params).getValue();
        return snapshots == null || snapshots.isEmpty() ? null : snapshots.get(0);
    }

    private String toPeriodKey(Date checkinDate) {
        Date date = checkinDate == null ? new Date() : checkinDate;
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private String resolveScoreStatus(BigDecimal progressValue) {
        if (progressValue == null) {
            return "UNKNOWN";
        }
        if (progressValue.compareTo(new BigDecimal("70")) >= 0) {
            return "GOOD";
        }
        if (progressValue.compareTo(new BigDecimal("40")) >= 0) {
            return "WARNING";
        }
        return "BAD";
    }

    private String toCalculationTrace(MdOkrObjective objective, MdOkrCheckin checkin) {
        Map<String, Object> trace = new LinkedHashMap<>();
        trace.put("source", "OKR_CHECKIN");
        trace.put("objectiveOid", objective.getOid());
        trace.put("checkinOid", checkin.getOid());
        trace.put("krOid", checkin.getKrOid());
        return toJson(trace);
    }

    private String toJson(Map<String, Object> trace) {
        try {
            return LoadResources.getObjectMapper().writeValueAsString(trace);
        } catch (Exception e) {
            throw new IllegalStateException("Build OKR calculation trace failed.", e);
        }
    }
}
