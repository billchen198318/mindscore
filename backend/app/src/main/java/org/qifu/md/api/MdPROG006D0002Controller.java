package org.qifu.md.api;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.CheckControllerFieldHandler;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.PleaseSelect;
import org.qifu.base.model.QueryResult;
import org.qifu.base.model.SearchBody;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdOkrCycle;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.logic.IOkrObjectiveLogicService;
import org.qifu.md.model.OkrObjectiveRequest;
import org.qifu.md.service.IMdOkrCycleService;
import org.qifu.md.service.IMdOkrObjectiveService;
import org.qifu.md.service.IMdOrgMemberService;
import org.qifu.md.service.IMdOrgUnitService;
import org.qifu.util.SimpleUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG006D0002", description = "OKR Objective")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG006D0002")
public class MdPROG006D0002Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService;
    private final IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService;
    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;
    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;
    private final IOkrObjectiveLogicService okrObjectiveLogicService;

    public MdPROG006D0002Controller(IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService,
            IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService,
            IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService,
            IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService,
            IOkrObjectiveLogicService okrObjectiveLogicService) {
        super();
        this.mdOkrObjectiveService = mdOkrObjectiveService;
        this.mdOkrCycleService = mdOkrCycleService;
        this.mdOrgUnitService = mdOrgUnitService;
        this.mdOrgMemberService = mdOrgMemberService;
        this.okrObjectiveLogicService = okrObjectiveLogicService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0002Q", check = true)
    @Operation(summary = "MD_PROG006D0002 - findPage", description = "OKR objective query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdOkrObjective>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdOkrObjective>> result = this.initResult();
        try {
            QueryResult<List<MdOkrObjective>> queryResult = this.mdOkrObjectiveService.findPage(
                    this.queryParameter(searchBody)
                        .fullEquals("cycleOid")
                        .fullLink("objectiveCodeLike")
                        .fullLink("objectiveNameLike")
                        .fullEquals("parentOid")
                        .fullEquals("status")
                        .value(),
                    searchBody.getPageOf().orderBy("OBJECTIVE_CODE").sortTypeAsc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0002Q", check = true)
    @Operation(summary = "MD_PROG006D0002 - findCycleList", description = "OKR cycle option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG006D0002Q", check = true)
    @Operation(summary = "MD_PROG006D0002 - findObjectiveList", description = "OKR objective option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG006D0002Q", check = true)
    @Operation(summary = "MD_PROG006D0002 - findOrgList", description = "Organization option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG006D0002Q", check = true)
    @Operation(summary = "MD_PROG006D0002 - findMemberList", description = "Member option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG006D0002A", check = true)
    @Operation(summary = "MD_PROG006D0002 - save", description = "OKR objective create")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<OkrObjectiveRequest>> doSave(@RequestBody OkrObjectiveRequest request) {
        DefaultControllerJsonResultObj<OkrObjectiveRequest> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, request);
            DefaultResult<OkrObjectiveRequest> saveResult = this.okrObjectiveLogicService.create(request);
            this.setDefaultResponseJsonResult(saveResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0002E", check = true)
    @Operation(summary = "MD_PROG006D0002 - load", description = "OKR objective load")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<OkrObjectiveRequest>> doLoad(@RequestBody MdOkrObjective entity) {
        DefaultControllerJsonResultObj<OkrObjectiveRequest> result = this.initDefaultJsonResult();
        try {
            DefaultResult<OkrObjectiveRequest> loadResult = this.okrObjectiveLogicService.load(entity);
            this.setDefaultResponseJsonResult(loadResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0002E", check = true)
    @Operation(summary = "MD_PROG006D0002 - update", description = "OKR objective update")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<OkrObjectiveRequest>> doUpdate(@RequestBody OkrObjectiveRequest request) {
        DefaultControllerJsonResultObj<OkrObjectiveRequest> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, request);
            DefaultResult<OkrObjectiveRequest> updateResult = this.okrObjectiveLogicService.update(request);
            this.setDefaultResponseJsonResult(updateResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0002D", check = true)
    @Operation(summary = "MD_PROG006D0002 - delete", description = "OKR objective delete")
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> doDelete(@RequestBody MdOkrObjective entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            DefaultResult<Boolean> deleteResult = this.okrObjectiveLogicService.delete(entity);
            this.setDefaultResponseJsonResult(deleteResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handlerCheck(DefaultControllerJsonResultObj<OkrObjectiveRequest> result, OkrObjectiveRequest request) throws ControllerException, ServiceException {
        MdOkrObjective entity = request == null ? null : request.getObjective();
        if (entity == null) {
            result.getCheckFields().put("objectiveCode", "Please enter objective data.");
            throw new ControllerException("Please enter objective data.");
        }
        CheckControllerFieldHandler<MdOkrObjective> chk = CheckControllerFieldHandler.build((DefaultControllerJsonResultObj) result);
        chk.testField("cycleOid", PleaseSelect.noSelect(entity.getCycleOid()), "Please select OKR cycle.")
           .testField("objectiveCode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(objectiveCode)", "Please enter objective code.")
           .testField("objectiveCode", StringUtils.isNotBlank(entity.getObjectiveCode()) && !SimpleUtils.checkBeTrueOfAZaz09Id(entity.getObjectiveCode()), "Objective code only allows 0-9, a-z, A-Z, dash, underscore, and dot.")
           .testField("objectiveName", entity, "@org.apache.commons.lang3.StringUtils@isBlank(objectiveName)", "Please enter objective name.")
           .testField("progressValue", entity, "progressValue == null", "Please enter progress.")
           .testField("progressValue", entity.getProgressValue() != null && (entity.getProgressValue().compareTo(BigDecimal.ZERO) < 0 || entity.getProgressValue().compareTo(new BigDecimal("100")) > 0), "Progress must be between 0 and 100.")
           .testField("confidenceScore", entity.getConfidenceScore() != null && (entity.getConfidenceScore().compareTo(BigDecimal.ZERO) < 0 || entity.getConfidenceScore().compareTo(new BigDecimal("100")) > 0), "Confidence must be between 0 and 100.")
           .testField("status", PleaseSelect.noSelect(entity.getStatus()), "Please select status.")
           .testField("status", !this.isValidStatus(entity.getStatus()), "Status only allows DRAFT, ACTIVE, CLOSED, or ARCHIVED.")
           .testField("parentOid", StringUtils.isNotBlank(entity.getOid()) && StringUtils.equals(entity.getOid(), entity.getParentOid()), "Parent objective cannot be itself.")
           .throwHtmlMessage();
    }

    private boolean isValidStatus(String status) {
        return StringUtils.equalsAny(status, "DRAFT", "ACTIVE", "CLOSED", "ARCHIVED");
    }
}
