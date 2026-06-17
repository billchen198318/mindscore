package org.qifu.md.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;

import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.PleaseSelect;
import org.qifu.base.model.QueryResult;
import org.qifu.base.model.SearchBody;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdOkrCheckin;
import org.qifu.md.entity.MdOkrCycle;
import org.qifu.md.entity.MdOkrKeyResult;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.entity.MdOkrObjectiveOwner;
import org.qifu.md.entity.MdOkrSnapshot;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.model.OkrSnapshotDetail;
import org.qifu.md.model.OkrSnapshotKeyResultDetail;
import org.qifu.md.service.IMdOkrCheckinService;
import org.qifu.md.service.IMdOkrCycleService;
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

@Tag(name = "MD_PROG006D0005", description = "OKR Snapshot")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG006D0005")
public class MdPROG006D0005Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdOkrSnapshotService<MdOkrSnapshot, String> mdOkrSnapshotService;
    private final IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService;
    private final IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService;
    private final IMdOkrObjectiveOwnerService<MdOkrObjectiveOwner, String> mdOkrObjectiveOwnerService;
    private final IMdOkrKeyResultService<MdOkrKeyResult, String> mdOkrKeyResultService;
    private final IMdOkrCheckinService<MdOkrCheckin, String> mdOkrCheckinService;
    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;
    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;

    public MdPROG006D0005Controller(IMdOkrSnapshotService<MdOkrSnapshot, String> mdOkrSnapshotService,
            IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService,
            IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService,
            IMdOkrObjectiveOwnerService<MdOkrObjectiveOwner, String> mdOkrObjectiveOwnerService,
            IMdOkrKeyResultService<MdOkrKeyResult, String> mdOkrKeyResultService,
            IMdOkrCheckinService<MdOkrCheckin, String> mdOkrCheckinService,
            IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService,
            IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService) {
        super();
        this.mdOkrSnapshotService = mdOkrSnapshotService;
        this.mdOkrCycleService = mdOkrCycleService;
        this.mdOkrObjectiveService = mdOkrObjectiveService;
        this.mdOkrObjectiveOwnerService = mdOkrObjectiveOwnerService;
        this.mdOkrKeyResultService = mdOkrKeyResultService;
        this.mdOkrCheckinService = mdOkrCheckinService;
        this.mdOrgUnitService = mdOrgUnitService;
        this.mdOrgMemberService = mdOrgMemberService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0005Q", check = true)
    @Operation(summary = "MD_PROG006D0005 - findPage", description = "OKR snapshot query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdOkrSnapshot>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdOkrSnapshot>> result = this.initResult();
        try {
            QueryResult<List<MdOkrSnapshot>> queryResult = this.mdOkrSnapshotService.findPage(
                    this.queryParameter(searchBody)
                        .fullEquals("cycleOid")
                        .fullEquals("objectiveOid")
                        .fullEquals("periodKey")
                        .fullEquals("periodKeyFrom")
                        .fullEquals("periodKeyTo")
                        .fullEquals("scoreStatus")
                        .fullEquals("orgOid")
                        .fullEquals("account")
                        .value(),
                    searchBody.getPageOf().orderBy("PERIOD_KEY, SNAPSHOT_AT").sortTypeDesc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0005Q", check = true)
    @Operation(summary = "MD_PROG006D0005 - findSnapshotDetail", description = "OKR snapshot objective and KR drill-down")
    @PostMapping(value = "/findSnapshotDetail", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<OkrSnapshotDetail>> findSnapshotDetail(@RequestBody MdOkrSnapshot entity) {
        DefaultControllerJsonResultObj<OkrSnapshotDetail> result = this.initDefaultJsonResult();
        try {
            OkrSnapshotDetail detail = this.buildSnapshotDetail(entity);
            result.setValue(detail);
            result.setSuccess(YES);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0005Q", check = true)
    @Operation(summary = "MD_PROG006D0005 - findCycleList", description = "OKR cycle option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG006D0005Q", check = true)
    @Operation(summary = "MD_PROG006D0005 - findObjectiveList", description = "OKR objective option list")
    @PostMapping(value = "/findObjectiveList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdOkrObjective>>> findObjectiveList(@RequestBody MdOkrObjective entity) {
        DefaultControllerJsonResultObj<List<MdOkrObjective>> result = this.initDefaultJsonResult();
        try {
            Map<String, Object> params = new HashMap<>();
            if (!PleaseSelect.noSelect(entity.getCycleOid())) {
                params.put("cycleOid", entity.getCycleOid());
            }
            DefaultResult<List<MdOkrObjective>> listResult = this.mdOkrObjectiveService.selectListByParams(params, "OBJECTIVE_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0005Q", check = true)
    @Operation(summary = "MD_PROG006D0005 - findOrgList", description = "Organization option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG006D0005Q", check = true)
    @Operation(summary = "MD_PROG006D0005 - findMemberList", description = "Member option list")
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

    private OkrSnapshotDetail buildSnapshotDetail(MdOkrSnapshot entity) throws ServiceException, ControllerException {
        if (entity == null || PleaseSelect.noSelect(entity.getOid())) {
            throw new ControllerException("Please select snapshot.");
        }

        MdOkrSnapshot snapshotKey = new MdOkrSnapshot();
        snapshotKey.setOid(entity.getOid());
        MdOkrSnapshot snapshot = this.mdOkrSnapshotService.selectByEntityPrimaryKey(snapshotKey).getValueEmptyThrowMessage();

        MdOkrObjective objectiveKey = new MdOkrObjective();
        objectiveKey.setOid(snapshot.getObjectiveOid());
        MdOkrObjective objective = this.mdOkrObjectiveService.selectByEntityPrimaryKey(objectiveKey).getValueEmptyThrowMessage();

        OkrSnapshotDetail detail = new OkrSnapshotDetail();
        detail.setSnapshot(snapshot);
        detail.setObjective(objective);
        detail.setOwnerList(this.loadOwnerList(snapshot.getObjectiveOid()));
        detail.setKeyResultDetailList(this.loadKeyResultDetailList(snapshot));
        return detail;
    }

    private List<MdOkrObjectiveOwner> loadOwnerList(String objectiveOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("objectiveOid", objectiveOid);
        List<MdOkrObjectiveOwner> ownerList = this.mdOkrObjectiveOwnerService.selectListByParams(params, "OWNER_TYPE, OWNER_ROLE", "ASC").getValue();
        return ownerList == null ? new ArrayList<>() : ownerList;
    }

    private List<OkrSnapshotKeyResultDetail> loadKeyResultDetailList(MdOkrSnapshot snapshot) throws ServiceException {
        Map<String, Object> krParams = new HashMap<>();
        krParams.put("objectiveOid", snapshot.getObjectiveOid());
        List<MdOkrKeyResult> keyResultList = this.mdOkrKeyResultService.selectListByParams(krParams, "SORT_NO, KR_CODE", "ASC").getValue();
        List<OkrSnapshotKeyResultDetail> detailList = new ArrayList<>();
        if (keyResultList == null || keyResultList.isEmpty()) {
            return detailList;
        }

        Date periodDate = this.parsePeriodKey(snapshot.getPeriodKey());
        for (MdOkrKeyResult keyResult : keyResultList) {
            OkrSnapshotKeyResultDetail detail = new OkrSnapshotKeyResultDetail();
            detail.setKeyResult(keyResult);
            detail.setCheckinList(this.loadCheckinList(keyResult.getOid(), periodDate));
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

    private Date parsePeriodKey(String periodKey) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setLenient(false);
            return format.parse(periodKey);
        } catch (ParseException | NullPointerException e) {
            return null;
        }
    }
}
