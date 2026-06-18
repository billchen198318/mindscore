package org.qifu.md.api;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import org.qifu.md.entity.MdActionItem;
import org.qifu.md.entity.MdActionPlan;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.entity.MdStrategyObjective;
import org.qifu.md.logic.IActionItemLogicService;
import org.qifu.md.model.ActionItemRequest;
import org.qifu.md.service.IMdActionItemService;
import org.qifu.md.service.IMdActionPlanService;
import org.qifu.md.service.IMdKpiService;
import org.qifu.md.service.IMdOkrObjectiveService;
import org.qifu.md.service.IMdOrgMemberService;
import org.qifu.md.service.IMdOrgUnitService;
import org.qifu.md.service.IMdStrategyObjectiveService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG008D0002", description = "Action Item")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG008D0002")
public class MdPROG008D0002Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdActionItemService<MdActionItem, String> mdActionItemService;
    private final IMdActionPlanService<MdActionPlan, String> mdActionPlanService;
    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;
    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;
    private final IMdKpiService<MdKpi, String> mdKpiService;
    private final IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService;
    private final IMdStrategyObjectiveService<MdStrategyObjective, String> mdStrategyObjectiveService;
    private final IActionItemLogicService actionItemLogicService;

    public MdPROG008D0002Controller(IMdActionItemService<MdActionItem, String> mdActionItemService,
            IMdActionPlanService<MdActionPlan, String> mdActionPlanService,
            IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService,
            IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService,
            IMdKpiService<MdKpi, String> mdKpiService,
            IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService,
            IMdStrategyObjectiveService<MdStrategyObjective, String> mdStrategyObjectiveService,
            IActionItemLogicService actionItemLogicService) {
        super();
        this.mdActionItemService = mdActionItemService;
        this.mdActionPlanService = mdActionPlanService;
        this.mdOrgUnitService = mdOrgUnitService;
        this.mdOrgMemberService = mdOrgMemberService;
        this.mdKpiService = mdKpiService;
        this.mdOkrObjectiveService = mdOkrObjectiveService;
        this.mdStrategyObjectiveService = mdStrategyObjectiveService;
        this.actionItemLogicService = actionItemLogicService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0002Q", check = true)
    @Operation(summary = "MD_PROG008D0002 - findPage", description = "Action Item query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdActionItem>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdActionItem>> result = this.initResult();
        try {
            QueryResult<List<MdActionItem>> queryResult = this.mdActionItemService.findPage(
                    this.queryParameter(searchBody)
                        .fullEquals("planOid")
                        .fullLink("itemNameLike")
                        .fullEquals("actionStage")
                        .fullEquals("status")
                        .value(),
                    searchBody.getPageOf().orderBy("SORT_NO").sortTypeAsc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0002Q", check = true)
    @Operation(summary = "MD_PROG008D0002 - findList", description = "Action Item list")
    @PostMapping(value = "/findList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdActionItem>>> findList(@RequestBody MdActionItem entity) {
        DefaultControllerJsonResultObj<List<MdActionItem>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdActionItem>> listResult;
            if (entity != null && StringUtils.isNotBlank(entity.getPlanOid())) {
                Map<String, Object> params = new HashMap<>();
                params.put("planOid", entity.getPlanOid());
                listResult = this.mdActionItemService.selectListByParams(params, "SORT_NO, ITEM_NAME", "ASC");
            } else {
                listResult = this.mdActionItemService.selectList("SORT_NO", "ASC");
            }
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0002Q", check = true)
    @Operation(summary = "MD_PROG008D0002 - findPlanList", description = "Action Item plan option list")
    @PostMapping(value = "/findPlanList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdActionPlan>>> findPlanList(@RequestBody MdActionPlan entity) {
        DefaultControllerJsonResultObj<List<MdActionPlan>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdActionPlan>> listResult = this.mdActionPlanService.selectList("PLAN_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0002Q", check = true)
    @Operation(summary = "MD_PROG008D0002 - findOrgList", description = "Action Item organization option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG008D0002Q", check = true)
    @Operation(summary = "MD_PROG008D0002 - findMemberList", description = "Action Item member option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG008D0002Q", check = true)
    @Operation(summary = "MD_PROG008D0002 - findKpiList", description = "Action Item KPI source option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG008D0002Q", check = true)
    @Operation(summary = "MD_PROG008D0002 - findOkrObjectiveList", description = "Action Item OKR objective source option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG008D0002Q", check = true)
    @Operation(summary = "MD_PROG008D0002 - findStrategyObjectiveList", description = "Action Item strategy source option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG008D0002A", check = true)
    @Operation(summary = "MD_PROG008D0002 - save", description = "Action Item create")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<ActionItemRequest>> doSave(@RequestBody ActionItemRequest request) {
        DefaultControllerJsonResultObj<ActionItemRequest> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, request);
            DefaultResult<ActionItemRequest> cResult = this.actionItemLogicService.create(request);
            this.setDefaultResponseJsonResult(cResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0002E", check = true)
    @Operation(summary = "MD_PROG008D0002 - load", description = "Action Item load")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<ActionItemRequest>> doLoad(@RequestBody MdActionItem entity) {
        DefaultControllerJsonResultObj<ActionItemRequest> result = this.initDefaultJsonResult();
        try {
            DefaultResult<ActionItemRequest> lResult = this.actionItemLogicService.load(entity);
            this.setDefaultResponseJsonResult(lResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0002E", check = true)
    @Operation(summary = "MD_PROG008D0002 - update", description = "Action Item update")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<ActionItemRequest>> doUpdate(@RequestBody ActionItemRequest request) {
        DefaultControllerJsonResultObj<ActionItemRequest> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, request);
            DefaultResult<ActionItemRequest> uResult = this.actionItemLogicService.update(request);
            this.setDefaultResponseJsonResult(uResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0002D", check = true)
    @Operation(summary = "MD_PROG008D0002 - delete", description = "Action Item delete")
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> doDelete(@RequestBody MdActionItem entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            DefaultResult<Boolean> delResult = this.actionItemLogicService.delete(entity);
            this.setDefaultResponseJsonResult(delResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handlerCheck(DefaultControllerJsonResultObj<ActionItemRequest> result, ActionItemRequest request) throws ControllerException {
        MdActionItem entity = request == null ? null : request.getActionItem();
        if (entity == null) {
            result.getCheckFields().put("actionItem", "Action item is required.");
            throw new ControllerException("Action item is required.");
        }
        CheckControllerFieldHandler<MdActionItem> chk = CheckControllerFieldHandler.build((DefaultControllerJsonResultObj) result);
        chk.testField("planOid", PleaseSelect.noSelect(entity.getPlanOid()), "Action plan is required.")
           .testField("itemName", entity, "@org.apache.commons.lang3.StringUtils@isBlank(itemName)", "Item name is required.")
           .testField("actionStage", PleaseSelect.noSelect(entity.getActionStage()), "Action stage is required.")
           .testField("progressValue", entity, "progressValue == null", "Progress is required.")
           .testField("status", PleaseSelect.noSelect(entity.getStatus()), "Status is required.")
           .throwHtmlMessage();

        if (!Strings.CS.equalsAny(entity.getActionStage(), "PLAN", "DO", "CHECK", "ACT")) {
            result.getCheckFields().put("actionStage", "Unsupported action stage.");
            throw new ControllerException("Unsupported action stage.");
        }
        if (!Strings.CS.equalsAny(entity.getStatus(), "DRAFT", "ACTIVE", "CLOSED", "ARCHIVED")) {
            result.getCheckFields().put("status", "Unsupported action item status.");
            throw new ControllerException("Unsupported action item status.");
        }
        if (entity.getProgressValue().compareTo(BigDecimal.ZERO) < 0 || entity.getProgressValue().compareTo(new BigDecimal("100")) > 0) {
            result.getCheckFields().put("progressValue", "Progress must be between 0 and 100.");
            throw new ControllerException("Progress must be between 0 and 100.");
        }
        if (entity.getStartDate() != null && entity.getEndDate() != null && entity.getEndDate().before(entity.getStartDate())) {
            result.getCheckFields().put("endDate", "End date cannot be earlier than start date.");
            throw new ControllerException("End date cannot be earlier than start date.");
        }
        if (entity.getDoneDate() != null && entity.getStartDate() != null && entity.getDoneDate().before(entity.getStartDate())) {
            result.getCheckFields().put("doneDate", "Done date cannot be earlier than start date.");
            throw new ControllerException("Done date cannot be earlier than start date.");
        }
    }
}
