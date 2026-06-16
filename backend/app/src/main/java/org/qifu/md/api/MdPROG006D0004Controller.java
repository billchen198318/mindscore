package org.qifu.md.api;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.qifu.md.entity.MdOkrCheckin;
import org.qifu.md.entity.MdOkrCycle;
import org.qifu.md.entity.MdOkrKeyResult;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.logic.IOkrCheckinLogicService;
import org.qifu.md.service.IMdOkrCheckinService;
import org.qifu.md.service.IMdOkrCycleService;
import org.qifu.md.service.IMdOkrKeyResultService;
import org.qifu.md.service.IMdOkrObjectiveService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG006D0004", description = "OKR Check-in")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG006D0004")
public class MdPROG006D0004Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdOkrCheckinService<MdOkrCheckin, String> mdOkrCheckinService;
    private final IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService;
    private final IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService;
    private final IMdOkrKeyResultService<MdOkrKeyResult, String> mdOkrKeyResultService;
    private final IOkrCheckinLogicService okrCheckinLogicService;

    public MdPROG006D0004Controller(IMdOkrCheckinService<MdOkrCheckin, String> mdOkrCheckinService,
            IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService,
            IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService,
            IMdOkrKeyResultService<MdOkrKeyResult, String> mdOkrKeyResultService,
            IOkrCheckinLogicService okrCheckinLogicService) {
        super();
        this.mdOkrCheckinService = mdOkrCheckinService;
        this.mdOkrCycleService = mdOkrCycleService;
        this.mdOkrObjectiveService = mdOkrObjectiveService;
        this.mdOkrKeyResultService = mdOkrKeyResultService;
        this.okrCheckinLogicService = okrCheckinLogicService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0004Q", check = true)
    @Operation(summary = "MD_PROG006D0004 - findPage", description = "OKR check-in query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdOkrCheckin>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdOkrCheckin>> result = this.initResult();
        try {
            QueryResult<List<MdOkrCheckin>> queryResult = this.mdOkrCheckinService.findPage(
                    this.queryParameter(searchBody)
                        .fullEquals("krOid")
                        .fullEquals("checkinDateStart")
                        .fullEquals("checkinDateEnd")
                        .value(),
                    searchBody.getPageOf().orderBy("CHECKIN_DATE, CDATE").sortTypeDesc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0004Q", check = true)
    @Operation(summary = "MD_PROG006D0004 - findCycleList", description = "OKR cycle option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG006D0004Q", check = true)
    @Operation(summary = "MD_PROG006D0004 - findObjectiveList", description = "OKR objective option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG006D0004Q", check = true)
    @Operation(summary = "MD_PROG006D0004 - findKrList", description = "OKR key result option list")
    @PostMapping(value = "/findKrList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdOkrKeyResult>>> findKrList(@RequestBody MdOkrKeyResult entity) {
        DefaultControllerJsonResultObj<List<MdOkrKeyResult>> result = this.initDefaultJsonResult();
        try {
            Map<String, Object> params = new HashMap<>();
            if (!PleaseSelect.noSelect(entity.getObjectiveOid())) {
                params.put("objectiveOid", entity.getObjectiveOid());
            }
            DefaultResult<List<MdOkrKeyResult>> listResult = this.mdOkrKeyResultService.selectListByParams(params, "SORT_NO, KR_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0004A", check = true)
    @Operation(summary = "MD_PROG006D0004 - save", description = "OKR check-in create")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdOkrCheckin>> doSave(@RequestBody MdOkrCheckin entity) {
        DefaultControllerJsonResultObj<MdOkrCheckin> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            DefaultResult<MdOkrCheckin> saveResult = this.okrCheckinLogicService.create(entity);
            this.setDefaultResponseJsonResult(saveResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0004D", check = true)
    @Operation(summary = "MD_PROG006D0004 - delete", description = "OKR check-in delete")
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> doDelete(@RequestBody MdOkrCheckin entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            DefaultResult<Boolean> deleteResult = this.okrCheckinLogicService.delete(entity);
            this.setDefaultResponseJsonResult(deleteResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handlerCheck(DefaultControllerJsonResultObj<MdOkrCheckin> result, MdOkrCheckin entity) throws ControllerException, ServiceException {
        CheckControllerFieldHandler<MdOkrCheckin> chk = CheckControllerFieldHandler.build((DefaultControllerJsonResultObj) result);
        chk.testField("krOid", PleaseSelect.noSelect(entity.getKrOid()), "Please select key result.")
           .testField("checkinDate", entity.getCheckinDate() == null, "Please enter check-in date.")
           .testField("progressValue", entity, "progressValue == null", "Please enter progress.")
           .testField("progressValue", entity.getProgressValue() != null && !betweenZeroAndHundred(entity.getProgressValue()), "Progress must be between 0 and 100.")
           .testField("confidenceScore", entity.getConfidenceScore() != null && !betweenZeroAndHundred(entity.getConfidenceScore()), "Confidence must be between 0 and 100.")
           .throwHtmlMessage();
    }

    private boolean betweenZeroAndHundred(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) >= 0 && value.compareTo(new BigDecimal("100")) <= 0;
    }
}
