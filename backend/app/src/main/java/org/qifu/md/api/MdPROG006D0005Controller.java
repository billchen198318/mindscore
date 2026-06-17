package org.qifu.md.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.PleaseSelect;
import org.qifu.base.model.QueryResult;
import org.qifu.base.model.SearchBody;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdOkrCycle;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.entity.MdOkrSnapshot;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.service.IMdOkrCycleService;
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
    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;
    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;

    public MdPROG006D0005Controller(IMdOkrSnapshotService<MdOkrSnapshot, String> mdOkrSnapshotService,
            IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService,
            IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService,
            IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService,
            IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService) {
        super();
        this.mdOkrSnapshotService = mdOkrSnapshotService;
        this.mdOkrCycleService = mdOkrCycleService;
        this.mdOkrObjectiveService = mdOkrObjectiveService;
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
}
