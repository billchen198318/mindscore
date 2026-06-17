package org.qifu.md.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.PleaseSelect;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdOkrCheckin;
import org.qifu.md.entity.MdOkrCycle;
import org.qifu.md.entity.MdOkrInitiative;
import org.qifu.md.entity.MdOkrKeyResult;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.entity.MdOkrObjectiveOwner;
import org.qifu.md.entity.MdOkrSnapshot;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.model.OkrReportObjectiveView;
import org.qifu.md.model.OkrReportQueryRequest;
import org.qifu.md.model.OkrReportSummary;
import org.qifu.md.model.OkrSnapshotKeyResultDetail;
import org.qifu.md.service.IMdOkrCheckinService;
import org.qifu.md.service.IMdOkrCycleService;
import org.qifu.md.service.IMdOkrInitiativeService;
import org.qifu.md.service.IMdOkrKeyResultService;
import org.qifu.md.service.IMdOkrObjectiveOwnerService;
import org.qifu.md.service.IMdOkrObjectiveService;
import org.qifu.md.service.IMdOkrSnapshotService;
import org.qifu.md.service.IMdOrgMemberService;
import org.qifu.md.service.IMdOrgUnitService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG006D0006", description = "OKR Report")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG006D0006")
public class MdPROG006D0006Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService;
    private final IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService;
    private final IMdOkrObjectiveOwnerService<MdOkrObjectiveOwner, String> mdOkrObjectiveOwnerService;
    private final IMdOkrInitiativeService<MdOkrInitiative, String> mdOkrInitiativeService;
    private final IMdOkrKeyResultService<MdOkrKeyResult, String> mdOkrKeyResultService;
    private final IMdOkrCheckinService<MdOkrCheckin, String> mdOkrCheckinService;
    private final IMdOkrSnapshotService<MdOkrSnapshot, String> mdOkrSnapshotService;
    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;
    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;

    public MdPROG006D0006Controller(IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService,
            IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService,
            IMdOkrObjectiveOwnerService<MdOkrObjectiveOwner, String> mdOkrObjectiveOwnerService,
            IMdOkrInitiativeService<MdOkrInitiative, String> mdOkrInitiativeService,
            IMdOkrKeyResultService<MdOkrKeyResult, String> mdOkrKeyResultService,
            IMdOkrCheckinService<MdOkrCheckin, String> mdOkrCheckinService,
            IMdOkrSnapshotService<MdOkrSnapshot, String> mdOkrSnapshotService,
            IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService,
            IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService) {
        super();
        this.mdOkrCycleService = mdOkrCycleService;
        this.mdOkrObjectiveService = mdOkrObjectiveService;
        this.mdOkrObjectiveOwnerService = mdOkrObjectiveOwnerService;
        this.mdOkrInitiativeService = mdOkrInitiativeService;
        this.mdOkrKeyResultService = mdOkrKeyResultService;
        this.mdOkrCheckinService = mdOkrCheckinService;
        this.mdOkrSnapshotService = mdOkrSnapshotService;
        this.mdOrgUnitService = mdOrgUnitService;
        this.mdOrgMemberService = mdOrgMemberService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0006Q", check = true)
    @Operation(summary = "MD_PROG006D0006 - hierarchy", description = "OKR report hierarchy")
    @PostMapping(value = "/hierarchy", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<OkrReportObjectiveView>>> hierarchy(@RequestBody OkrReportQueryRequest request) {
        DefaultControllerJsonResultObj<List<OkrReportObjectiveView>> result = this.initDefaultJsonResult();
        try {
            validateRequest(request);
            result.setValue(this.buildHierarchy(request));
            result.setSuccess(YES);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0006Q", check = true)
    @Operation(summary = "MD_PROG006D0006 - summary", description = "OKR report summary")
    @PostMapping(value = "/summary", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<OkrReportSummary>> summary(@RequestBody OkrReportQueryRequest request) {
        DefaultControllerJsonResultObj<OkrReportSummary> result = this.initDefaultJsonResult();
        try {
            validateRequest(request);
            List<OkrReportObjectiveView> flatViews = this.buildFlatViews(request);
            result.setValue(this.buildSummary(flatViews));
            result.setSuccess(YES);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0006Q", check = true)
    @Operation(summary = "MD_PROG006D0006 - findCycleList", description = "OKR cycle option list")
    @PostMapping(value = "/findCycleList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdOkrCycle>>> findCycleList(@RequestBody MdOkrCycle entity) {
        DefaultControllerJsonResultObj<List<MdOkrCycle>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdOkrCycle>> listResult = this.mdOkrCycleService.selectList("START_DATE", "DESC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0006Q", check = true)
    @Operation(summary = "MD_PROG006D0006 - findOrgList", description = "Organization option list")
    @PostMapping(value = "/findOrgList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdOrgUnit>>> findOrgList(@RequestBody MdOrgUnit entity) {
        DefaultControllerJsonResultObj<List<MdOrgUnit>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdOrgUnit>> listResult = this.mdOrgUnitService.selectList("ORG_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0006Q", check = true)
    @Operation(summary = "MD_PROG006D0006 - findMemberList", description = "Member option list")
    @PostMapping(value = "/findMemberList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdOrgMember>>> findMemberList(@RequestBody MdOrgMember entity) {
        DefaultControllerJsonResultObj<List<MdOrgMember>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdOrgMember>> listResult = this.mdOrgMemberService.selectList("ACCOUNT", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    private void validateRequest(OkrReportQueryRequest request) throws ServiceException {
        if (request == null || PleaseSelect.noSelect(request.getCycleOid())) {
            throw new ServiceException("Please select OKR cycle.");
        }
        if (StringUtils.isNotBlank(request.getPeriodKey())) {
            parsePeriodKey(request.getPeriodKey());
        }
    }

    private List<OkrReportObjectiveView> buildHierarchy(OkrReportQueryRequest request) throws ServiceException {
        List<OkrReportObjectiveView> flatViews = this.buildFlatViews(request);
        Map<String, OkrReportObjectiveView> viewMap = new LinkedHashMap<>();
        for (OkrReportObjectiveView view : flatViews) {
            viewMap.put(view.getObjective().getOid(), view);
        }
        List<OkrReportObjectiveView> roots = new ArrayList<>();
        for (OkrReportObjectiveView view : flatViews) {
            String parentOid = view.getObjective().getParentOid();
            if (StringUtils.isBlank(parentOid) || !viewMap.containsKey(parentOid)) {
                roots.add(view);
                continue;
            }
            viewMap.get(parentOid).getChildren().add(view);
        }
        return roots;
    }

    private List<OkrReportObjectiveView> buildFlatViews(OkrReportQueryRequest request) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("cycleOid", request.getCycleOid());
        if (StringUtils.isNotBlank(request.getStatus())) {
            params.put("status", StringUtils.trim(request.getStatus()));
        }
        List<MdOkrObjective> objectiveList = this.mdOkrObjectiveService.selectListByParams(params, "OBJECTIVE_CODE", "ASC").getValue();
        List<OkrReportObjectiveView> views = new ArrayList<>();
        if (objectiveList == null || objectiveList.isEmpty()) {
            return views;
        }
        for (MdOkrObjective objective : objectiveList) {
            List<MdOkrObjectiveOwner> ownerList = loadOwnerList(objective.getOid());
            if (!ownerMatched(ownerList, request)) {
                continue;
            }
            OkrReportObjectiveView view = new OkrReportObjectiveView();
            view.setObjective(objective);
            view.setOwnerList(ownerList);
            view.setInitiativeList(loadInitiativeList(objective.getOid()));
            view.setSnapshot(loadLatestSnapshot(objective.getOid(), request.getPeriodKey()));
            view.setKeyResultDetailList(loadKeyResultDetailList(objective.getOid(), request.getPeriodKey()));
            views.add(view);
        }
        return views;
    }

    private List<MdOkrObjectiveOwner> loadOwnerList(String objectiveOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("objectiveOid", objectiveOid);
        List<MdOkrObjectiveOwner> ownerList = this.mdOkrObjectiveOwnerService.selectListByParams(params, "OWNER_TYPE, OWNER_ROLE", "ASC").getValue();
        return ownerList == null ? new ArrayList<>() : ownerList;
    }

    private List<MdOkrInitiative> loadInitiativeList(String objectiveOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("objectiveOid", objectiveOid);
        List<MdOkrInitiative> initiativeList = this.mdOkrInitiativeService.selectListByParams(params, "SORT_NO, INITIATIVE_CODE", "ASC").getValue();
        return initiativeList == null ? new ArrayList<>() : initiativeList;
    }

    private boolean ownerMatched(List<MdOkrObjectiveOwner> ownerList, OkrReportQueryRequest request) {
        boolean hasOrg = StringUtils.isNotBlank(request.getOrgOid());
        boolean hasAccount = StringUtils.isNotBlank(request.getAccount());
        if (!hasOrg && !hasAccount) {
            return true;
        }
        if (ownerList == null || ownerList.isEmpty()) {
            return false;
        }
        for (MdOkrObjectiveOwner owner : ownerList) {
            if (hasOrg && "ORG".equals(owner.getOwnerType()) && Strings.CS.equals(request.getOrgOid(), owner.getOrgOid())) {
                return true;
            }
            if (hasAccount && "ACCOUNT".equals(owner.getOwnerType()) && Strings.CS.equals(request.getAccount(), owner.getAccount())) {
                return true;
            }
        }
        return false;
    }

    private MdOkrSnapshot loadLatestSnapshot(String objectiveOid, String periodKey) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("objectiveOid", objectiveOid);
        if (StringUtils.isNotBlank(periodKey)) {
            params.put("periodKeyTo", StringUtils.trim(periodKey));
        }
        List<MdOkrSnapshot> snapshotList = this.mdOkrSnapshotService.selectListByParams(params, "PERIOD_KEY, SNAPSHOT_AT", "DESC").getValue();
        return snapshotList == null || snapshotList.isEmpty() ? null : snapshotList.get(0);
    }

    private List<OkrSnapshotKeyResultDetail> loadKeyResultDetailList(String objectiveOid, String periodKey) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("objectiveOid", objectiveOid);
        List<MdOkrKeyResult> keyResultList = this.mdOkrKeyResultService.selectListByParams(params, "SORT_NO, KR_CODE", "ASC").getValue();
        List<OkrSnapshotKeyResultDetail> detailList = new ArrayList<>();
        if (keyResultList == null || keyResultList.isEmpty()) {
            return detailList;
        }
        Date periodDate = StringUtils.isBlank(periodKey) ? null : parsePeriodKey(periodKey);
        for (MdOkrKeyResult keyResult : keyResultList) {
            OkrSnapshotKeyResultDetail detail = new OkrSnapshotKeyResultDetail();
            detail.setKeyResult(keyResult);
            detail.setCheckinList(loadCheckinList(keyResult.getOid(), periodDate));
            detailList.add(detail);
        }
        return detailList;
    }

    private List<MdOkrCheckin> loadCheckinList(String krOid, Date periodDate) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("krOid", krOid);
        if (periodDate != null) {
            params.put("checkinDateEnd", periodDate);
        }
        List<MdOkrCheckin> checkinList = this.mdOkrCheckinService.selectListByParams(params, "CHECKIN_DATE, CDATE", "DESC").getValue();
        return checkinList == null ? new ArrayList<>() : checkinList;
    }

    private OkrReportSummary buildSummary(List<OkrReportObjectiveView> views) {
        OkrReportSummary summary = new OkrReportSummary();
        if (views == null || views.isEmpty()) {
            return summary;
        }
        BigDecimal progressTotal = BigDecimal.ZERO;
        int progressCount = 0;
        for (OkrReportObjectiveView view : views) {
            summary.setObjectiveCount(summary.getObjectiveCount() + 1);
            summary.setKeyResultCount(summary.getKeyResultCount() + view.getKeyResultDetailList().size());
            summary.setInitiativeCount(summary.getInitiativeCount() + view.getInitiativeList().size());
            BigDecimal progress = view.getSnapshot() == null ? view.getObjective().getProgressValue() : view.getSnapshot().getProgressValue();
            if (progress != null) {
                progressTotal = progressTotal.add(progress);
                progressCount++;
            }
            addStatus(summary, view.getSnapshot() == null ? null : view.getSnapshot().getScoreStatus());
        }
        if (progressCount > 0) {
            summary.setAvgProgress(progressTotal.divide(new BigDecimal(progressCount), 4, RoundingMode.HALF_UP));
        }
        return summary;
    }

    private void addStatus(OkrReportSummary summary, String status) {
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

    private Date parsePeriodKey(String periodKey) throws ServiceException {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setLenient(false);
            return format.parse(periodKey);
        } catch (ParseException | NullPointerException e) {
            throw new ServiceException("Invalid period key: " + periodKey);
        }
    }
}
