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
import org.qifu.md.entity.MdOkrCycle;
import org.qifu.md.entity.MdOkrKeyResult;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.service.IMdOkrCycleService;
import org.qifu.md.service.IMdOkrKeyResultService;
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

@Tag(name = "MD_PROG006D0003", description = "OKR Key Result")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG006D0003")
public class MdPROG006D0003Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdOkrKeyResultService<MdOkrKeyResult, String> mdOkrKeyResultService;
    private final IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService;
    private final IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService;

    public MdPROG006D0003Controller(IMdOkrKeyResultService<MdOkrKeyResult, String> mdOkrKeyResultService,
            IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService,
            IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService) {
        super();
        this.mdOkrKeyResultService = mdOkrKeyResultService;
        this.mdOkrObjectiveService = mdOkrObjectiveService;
        this.mdOkrCycleService = mdOkrCycleService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0003Q", check = true)
    @Operation(summary = "MD_PROG006D0003 - findPage", description = "OKR key result query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdOkrKeyResult>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdOkrKeyResult>> result = this.initResult();
        try {
            QueryResult<List<MdOkrKeyResult>> queryResult = this.mdOkrKeyResultService.findPage(
                    this.queryParameter(searchBody)
                        .fullEquals("objectiveOid")
                        .fullLink("krCodeLike")
                        .fullLink("krNameLike")
                        .fullEquals("krType")
                        .fullEquals("status")
                        .value(),
                    searchBody.getPageOf().orderBy("SORT_NO, KR_CODE").sortTypeAsc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0003Q", check = true)
    @Operation(summary = "MD_PROG006D0003 - findCycleList", description = "OKR cycle option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG006D0003Q", check = true)
    @Operation(summary = "MD_PROG006D0003 - findObjectiveList", description = "OKR objective option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG006D0003A", check = true)
    @Operation(summary = "MD_PROG006D0003 - save", description = "OKR key result create")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdOkrKeyResult>> doSave(@RequestBody MdOkrKeyResult entity) {
        DefaultControllerJsonResultObj<MdOkrKeyResult> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            this.normalize(entity);
            DefaultResult<MdOkrKeyResult> saveResult = this.mdOkrKeyResultService.insert(entity);
            this.setDefaultResponseJsonResult(saveResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0003E", check = true)
    @Operation(summary = "MD_PROG006D0003 - load", description = "OKR key result load")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdOkrKeyResult>> doLoad(@RequestBody MdOkrKeyResult entity) {
        DefaultControllerJsonResultObj<MdOkrKeyResult> result = this.initDefaultJsonResult();
        try {
            DefaultResult<MdOkrKeyResult> loadResult = this.mdOkrKeyResultService.selectByEntityPrimaryKey(entity);
            this.setDefaultResponseJsonResult(loadResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0003E", check = true)
    @Operation(summary = "MD_PROG006D0003 - update", description = "OKR key result update")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdOkrKeyResult>> doUpdate(@RequestBody MdOkrKeyResult entity) {
        DefaultControllerJsonResultObj<MdOkrKeyResult> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            this.normalize(entity);
            DefaultResult<MdOkrKeyResult> updateResult = this.mdOkrKeyResultService.update(entity);
            this.setDefaultResponseJsonResult(updateResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG006D0003D", check = true)
    @Operation(summary = "MD_PROG006D0003 - delete", description = "OKR key result delete")
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> doDelete(@RequestBody MdOkrKeyResult entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            DefaultResult<Boolean> deleteResult = this.mdOkrKeyResultService.delete(entity);
            this.setDefaultResponseJsonResult(deleteResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handlerCheck(DefaultControllerJsonResultObj<MdOkrKeyResult> result, MdOkrKeyResult entity) throws ControllerException, ServiceException {
        CheckControllerFieldHandler<MdOkrKeyResult> chk = CheckControllerFieldHandler.build((DefaultControllerJsonResultObj) result);
        chk.testField("objectiveOid", PleaseSelect.noSelect(entity.getObjectiveOid()), "Please select objective.")
           .testField("krCode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(krCode)", "Please enter KR code.")
           .testField("krCode", StringUtils.isNotBlank(entity.getKrCode()) && !SimpleUtils.checkBeTrueOfAZaz09Id(entity.getKrCode()), "KR code only allows 0-9, a-z, A-Z, dash, underscore, and dot.")
           .testField("krName", entity, "@org.apache.commons.lang3.StringUtils@isBlank(krName)", "Please enter KR name.")
           .testField("krType", PleaseSelect.noSelect(entity.getKrType()), "Please select KR type.")
           .testField("progressValue", entity, "progressValue == null", "Please enter progress.")
           .testField("progressValue", entity.getProgressValue() != null && !betweenZeroAndHundred(entity.getProgressValue()), "Progress must be between 0 and 100.")
           .testField("weightValue", entity, "weightValue == null", "Please enter weight.")
           .testField("weightValue", entity.getWeightValue() != null && !betweenZeroAndHundred(entity.getWeightValue()), "Weight must be between 0 and 100.")
           .testField("sortNo", entity, "sortNo == null", "Please enter sort no.")
           .testField("sortNo", entity.getSortNo() != null && entity.getSortNo() < 0, "Sort no must be greater than or equal to 0.")
           .testField("status", PleaseSelect.noSelect(entity.getStatus()), "Please select status.")
           .testField("krType", !isValidKrType(entity.getKrType()), "KR type only allows INCREASE, DECREASE, PERCENT, MILESTONE, BINARY, or MANUAL.")
           .testField("status", !isValidStatus(entity.getStatus()), "Status only allows DRAFT, ACTIVE, CLOSED, or ARCHIVED.")
           .throwHtmlMessage();
    }

    private void normalize(MdOkrKeyResult entity) {
        if (StringUtils.isBlank(entity.getUnitName())) {
            entity.setUnitName(null);
        }
    }

    private boolean betweenZeroAndHundred(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) >= 0 && value.compareTo(new BigDecimal("100")) <= 0;
    }

    private boolean isValidKrType(String krType) {
        return Strings.CS.equalsAny(krType, "INCREASE", "DECREASE", "PERCENT", "MILESTONE", "BINARY", "MANUAL");
    }

    private boolean isValidStatus(String status) {
        return Strings.CS.equalsAny(status, "DRAFT", "ACTIVE", "CLOSED", "ARCHIVED");
    }
}
