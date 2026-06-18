package org.qifu.md.api;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.Strings;
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
import org.qifu.md.entity.MdActionPlan;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.entity.MdStrategyObjective;
import org.qifu.md.logic.IActionPlanLogicService;
import org.qifu.md.model.ActionPlanRequest;
import org.qifu.md.service.IMdActionPlanService;
import org.qifu.md.service.IMdKpiService;
import org.qifu.md.service.IMdOkrObjectiveService;
import org.qifu.md.service.IMdOrgMemberService;
import org.qifu.md.service.IMdOrgUnitService;
import org.qifu.md.service.IMdStrategyObjectiveService;
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

@Tag(name = "MD_PROG008D0001", description = "Action Plan")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG008D0001")
public class MdPROG008D0001Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdActionPlanService<MdActionPlan, String> mdActionPlanService;
    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;
    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;
    private final IMdKpiService<MdKpi, String> mdKpiService;
    private final IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService;
    private final IMdStrategyObjectiveService<MdStrategyObjective, String> mdStrategyObjectiveService;
    private final IActionPlanLogicService actionPlanLogicService;

    public MdPROG008D0001Controller(IMdActionPlanService<MdActionPlan, String> mdActionPlanService,
            IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService,
            IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService,
            IMdKpiService<MdKpi, String> mdKpiService,
            IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService,
            IMdStrategyObjectiveService<MdStrategyObjective, String> mdStrategyObjectiveService,
            IActionPlanLogicService actionPlanLogicService) {
        super();
        this.mdActionPlanService = mdActionPlanService;
        this.mdOrgUnitService = mdOrgUnitService;
        this.mdOrgMemberService = mdOrgMemberService;
        this.mdKpiService = mdKpiService;
        this.mdOkrObjectiveService = mdOkrObjectiveService;
        this.mdStrategyObjectiveService = mdStrategyObjectiveService;
        this.actionPlanLogicService = actionPlanLogicService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0001Q", check = true)
    @Operation(summary = "MD_PROG008D0001 - findPage", description = "Action Plan query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdActionPlan>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdActionPlan>> result = this.initResult();
        try {
            QueryResult<List<MdActionPlan>> queryResult = this.mdActionPlanService.findPage(
                    this.queryParameter(searchBody)
                        .fullEquals("planCode")
                        .fullLink("planNameLike")
                        .fullEquals("status")
                        .value(),
                    searchBody.getPageOf().orderBy("PLAN_CODE").sortTypeAsc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0001Q", check = true)
    @Operation(summary = "MD_PROG008D0001 - findList", description = "Action Plan list")
    @PostMapping(value = "/findList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdActionPlan>>> findList(@RequestBody MdActionPlan entity) {
        DefaultControllerJsonResultObj<List<MdActionPlan>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdActionPlan>> listResult = this.mdActionPlanService.selectList("PLAN_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0001Q", check = true)
    @Operation(summary = "MD_PROG008D0001 - findOrgList", description = "Action Plan organization option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG008D0001Q", check = true)
    @Operation(summary = "MD_PROG008D0001 - findMemberList", description = "Action Plan member option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG008D0001Q", check = true)
    @Operation(summary = "MD_PROG008D0001 - findKpiList", description = "Action Plan KPI source option list")
    @PostMapping(value = "/findKpiList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdKpi>>> findKpiList(@RequestBody MdKpi entity) {
        DefaultControllerJsonResultObj<List<MdKpi>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdKpi>> listResult = this.mdKpiService.selectList("KPI_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0001Q", check = true)
    @Operation(summary = "MD_PROG008D0001 - findOkrObjectiveList", description = "Action Plan OKR objective source option list")
    @PostMapping(value = "/findOkrObjectiveList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdOkrObjective>>> findOkrObjectiveList(@RequestBody MdOkrObjective entity) {
        DefaultControllerJsonResultObj<List<MdOkrObjective>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdOkrObjective>> listResult = this.mdOkrObjectiveService.selectList("OBJECTIVE_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0001Q", check = true)
    @Operation(summary = "MD_PROG008D0001 - findStrategyObjectiveList", description = "Action Plan strategy source option list")
    @PostMapping(value = "/findStrategyObjectiveList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdStrategyObjective>>> findStrategyObjectiveList(@RequestBody MdStrategyObjective entity) {
        DefaultControllerJsonResultObj<List<MdStrategyObjective>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdStrategyObjective>> listResult = this.mdStrategyObjectiveService.selectList("OBJECTIVE_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0001A", check = true)
    @Operation(summary = "MD_PROG008D0001 - save", description = "Action Plan create")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<ActionPlanRequest>> doSave(@RequestBody ActionPlanRequest request) {
        DefaultControllerJsonResultObj<ActionPlanRequest> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, request);
            DefaultResult<ActionPlanRequest> cResult = this.actionPlanLogicService.create(request);
            this.setDefaultResponseJsonResult(cResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0001E", check = true)
    @Operation(summary = "MD_PROG008D0001 - load", description = "Action Plan load")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<ActionPlanRequest>> doLoad(@RequestBody MdActionPlan entity) {
        DefaultControllerJsonResultObj<ActionPlanRequest> result = this.initDefaultJsonResult();
        try {
            DefaultResult<ActionPlanRequest> lResult = this.actionPlanLogicService.load(entity);
            this.setDefaultResponseJsonResult(lResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0001E", check = true)
    @Operation(summary = "MD_PROG008D0001 - update", description = "Action Plan update")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<ActionPlanRequest>> doUpdate(@RequestBody ActionPlanRequest request) {
        DefaultControllerJsonResultObj<ActionPlanRequest> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, request);
            DefaultResult<ActionPlanRequest> uResult = this.actionPlanLogicService.update(request);
            this.setDefaultResponseJsonResult(uResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0001D", check = true)
    @Operation(summary = "MD_PROG008D0001 - delete", description = "Action Plan delete")
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> doDelete(@RequestBody MdActionPlan entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            DefaultResult<Boolean> delResult = this.actionPlanLogicService.delete(entity);
            this.setDefaultResponseJsonResult(delResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handlerCheck(DefaultControllerJsonResultObj<ActionPlanRequest> result, ActionPlanRequest request) throws ControllerException {
        MdActionPlan entity = request == null ? null : request.getActionPlan();
        if (entity == null) {
            result.getCheckFields().put("actionPlan", "Action plan is required.");
            throw new ControllerException("Action plan is required.");
        }
        CheckControllerFieldHandler<MdActionPlan> chk = CheckControllerFieldHandler.build((DefaultControllerJsonResultObj) result);
        chk.testField("planCode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(planCode)", "Plan code is required.")
           .testField("planCode", entity, "!@org.qifu.util.SimpleUtils@checkBeTrueOfAZaz09Id(planCode)", "Plan code only allows 0-9, a-z, A-Z, dash, underscore, and dot.")
           .testField("planName", entity, "@org.apache.commons.lang3.StringUtils@isBlank(planName)", "Plan name is required.")
           .testField("progressValue", entity, "progressValue == null", "Progress is required.")
           .testField("status", PleaseSelect.noSelect(entity.getStatus()), "Status is required.")
           .throwHtmlMessage();

        if (!SimpleUtils.checkBeTrueOfAZaz09Id(entity.getPlanCode())) {
            result.getCheckFields().put("planCode", "Plan code only allows 0-9, a-z, A-Z, dash, underscore, and dot.");
            throw new ControllerException("Plan code only allows 0-9, a-z, A-Z, dash, underscore, and dot.");
        }
        if (!Strings.CS.equalsAny(entity.getStatus(), "DRAFT", "ACTIVE", "CLOSED", "ARCHIVED")) {
            result.getCheckFields().put("status", "Unsupported action plan status.");
            throw new ControllerException("Unsupported action plan status.");
        }
        if (entity.getProgressValue().compareTo(BigDecimal.ZERO) < 0 || entity.getProgressValue().compareTo(new BigDecimal("100")) > 0) {
            result.getCheckFields().put("progressValue", "Progress must be between 0 and 100.");
            throw new ControllerException("Progress must be between 0 and 100.");
        }
        if (entity.getStartDate() != null && entity.getEndDate() != null && entity.getEndDate().before(entity.getStartDate())) {
            result.getCheckFields().put("endDate", "End date cannot be earlier than start date.");
            throw new ControllerException("End date cannot be earlier than start date.");
        }
    }
}
