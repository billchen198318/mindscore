package org.qifu.md.api;

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
import org.qifu.md.entity.MdOkrCycle;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.service.IMdOkrCycleService;
import org.qifu.md.service.IMdOkrObjectiveService;
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

@Tag(name = "MD_PROG006D0001", description = "OKR Cycle")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG006D0001")
public class MdPROG006D0001Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService;
    private final IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService;

    public MdPROG006D0001Controller(IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService,
            IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService) {
        super();
        this.mdOkrCycleService = mdOkrCycleService;
        this.mdOkrObjectiveService = mdOkrObjectiveService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0001Q", check = true)
    @Operation(summary = "MD_PROG006D0001 - findPage", description = "OKR cycle query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdOkrCycle>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdOkrCycle>> result = this.initResult();
        try {
            QueryResult<List<MdOkrCycle>> queryResult = this.mdOkrCycleService.findPage(
                    this.queryParameter(searchBody)
                        .fullLink("cycleCodeLike")
                        .fullLink("cycleNameLike")
                        .fullEquals("periodType")
                        .fullEquals("status")
                        .value(),
                    searchBody.getPageOf().orderBy("START_DATE, CYCLE_CODE").sortTypeDesc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0001Q", check = true)
    @Operation(summary = "MD_PROG006D0001 - findList", description = "OKR cycle list")
    @PostMapping(value = "/findList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdOkrCycle>>> findList(@RequestBody MdOkrCycle entity) {
        DefaultControllerJsonResultObj<List<MdOkrCycle>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdOkrCycle>> listResult = this.mdOkrCycleService.selectList("START_DATE", "DESC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0001A", check = true)
    @Operation(summary = "MD_PROG006D0001 - save", description = "OKR cycle create")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdOkrCycle>> doSave(@RequestBody MdOkrCycle entity) {
        DefaultControllerJsonResultObj<MdOkrCycle> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            DefaultResult<MdOkrCycle> saveResult = this.mdOkrCycleService.insert(entity);
            this.setDefaultResponseJsonResult(saveResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0001E", check = true)
    @Operation(summary = "MD_PROG006D0001 - load", description = "OKR cycle load")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdOkrCycle>> doLoad(@RequestBody MdOkrCycle entity) {
        DefaultControllerJsonResultObj<MdOkrCycle> result = this.initDefaultJsonResult();
        try {
            DefaultResult<MdOkrCycle> loadResult = this.mdOkrCycleService.selectByEntityPrimaryKey(entity);
            this.setDefaultResponseJsonResult(loadResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0001E", check = true)
    @Operation(summary = "MD_PROG006D0001 - update", description = "OKR cycle update")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdOkrCycle>> doUpdate(@RequestBody MdOkrCycle entity) {
        DefaultControllerJsonResultObj<MdOkrCycle> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            DefaultResult<MdOkrCycle> updateResult = this.mdOkrCycleService.update(entity);
            this.setDefaultResponseJsonResult(updateResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0001D", check = true)
    @Operation(summary = "MD_PROG006D0001 - delete", description = "OKR cycle delete")
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> doDelete(@RequestBody MdOkrCycle entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            this.checkCycleNotUsed(entity);
            DefaultResult<Boolean> deleteResult = this.mdOkrCycleService.delete(entity);
            this.setDefaultResponseJsonResult(deleteResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handlerCheck(DefaultControllerJsonResultObj<MdOkrCycle> result, MdOkrCycle entity) throws ControllerException, ServiceException {
        CheckControllerFieldHandler<MdOkrCycle> chk = CheckControllerFieldHandler.build((DefaultControllerJsonResultObj) result);
        chk.testField("cycleCode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(cycleCode)", "Please enter cycle code.")
           .testField("cycleCode", StringUtils.isNotBlank(entity.getCycleCode()) && !SimpleUtils.checkBeTrueOfAZaz09Id(entity.getCycleCode()), "Cycle code only allows 0-9, a-z, A-Z, dash, underscore, and dot.")
           .testField("cycleName", entity, "@org.apache.commons.lang3.StringUtils@isBlank(cycleName)", "Please enter cycle name.")
           .testField("periodType", PleaseSelect.noSelect(entity.getPeriodType()), "Please select period type.")
           .testField("startDate", entity.getStartDate() == null, "Please enter start date.")
           .testField("endDate", entity.getEndDate() == null, "Please enter end date.")
           .testField("status", PleaseSelect.noSelect(entity.getStatus()), "Please select status.")
           .testField("endDate", entity.getStartDate() != null && entity.getEndDate() != null && entity.getEndDate().before(entity.getStartDate()), "End date must be greater than or equal to start date.")
           .testField("status", !this.isValidStatus(entity.getStatus()), "Status only allows DRAFT, ACTIVE, CLOSED, or ARCHIVED.")
           .throwHtmlMessage();
    }

    private boolean isValidStatus(String status) {
        return Strings.CS.equalsAny(status, "DRAFT", "ACTIVE", "CLOSED", "ARCHIVED");
    }

    private void checkCycleNotUsed(MdOkrCycle entity) throws ServiceException, ControllerException {
        DefaultResult<List<MdOkrObjective>> objectiveListResult = this.mdOkrObjectiveService.selectListByParams(Map.of("cycleOid", entity.getOid()));
        List<MdOkrObjective> objectiveList = objectiveListResult.getValue();
        if (objectiveList != null && !objectiveList.isEmpty()) {
            throw new ControllerException("This OKR cycle is used by objective and cannot be deleted.");
        }
    }
}
