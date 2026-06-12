package org.qifu.md.api;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.CheckControllerFieldHandler;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.QueryResult;
import org.qifu.base.model.SearchBody;
import org.qifu.base.model.YesNoKeyProvide;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdAggregationMethod;
import org.qifu.md.model.AggregationMethodTestRequest;
import org.qifu.md.service.IMdAggregationMethodService;
import org.qifu.md.util.AggregationMethodUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG002D0002", description = "Aggregation Method")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG002D0002")
public class MD_PROG002D0002Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdAggregationMethodService<MdAggregationMethod, String> mdAggregationMethodService;

    public MD_PROG002D0002Controller(IMdAggregationMethodService<MdAggregationMethod, String> mdAggregationMethodService) {
        super();
        this.mdAggregationMethodService = mdAggregationMethodService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0002Q", check = true)
    @Operation(summary = "MD_PROG002D0002 - findPage", description = "Aggregation Method query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdAggregationMethod>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdAggregationMethod>> result = this.initResult();
        try {
            QueryResult<List<MdAggregationMethod>> queryResult = this.mdAggregationMethodService.findPage(
                    this.queryParameter(searchBody)
                        .fullLink("aggrCodeLike")
                        .fullLink("aggrNameLike")
                        .fullEquals("aggrType")
                        .fullEquals("enabled")
                        .value(),
                    searchBody.getPageOf().orderBy("AGGR_CODE").sortTypeAsc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0002Q", check = true)
    @Operation(summary = "MD_PROG002D0002 - findList", description = "Aggregation Method list")
    @PostMapping(value = "/findList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdAggregationMethod>>> findList(@RequestBody MdAggregationMethod entity) {
        DefaultControllerJsonResultObj<List<MdAggregationMethod>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdAggregationMethod>> listResult = this.mdAggregationMethodService.selectList();
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0002C", check = true)
    @Operation(summary = "MD_PROG002D0002 - save", description = "Aggregation Method create")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdAggregationMethod>> doSave(@RequestBody MdAggregationMethod entity) {
        DefaultControllerJsonResultObj<MdAggregationMethod> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            DefaultResult<MdAggregationMethod> cResult = this.mdAggregationMethodService.insert(entity);
            this.setDefaultResponseJsonResult(cResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0002E", check = true)
    @Operation(summary = "MD_PROG002D0002 - load", description = "Aggregation Method load")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdAggregationMethod>> doLoad(@RequestBody MdAggregationMethod entity) {
        DefaultControllerJsonResultObj<MdAggregationMethod> result = this.initDefaultJsonResult();
        try {
            DefaultResult<MdAggregationMethod> lResult = this.mdAggregationMethodService.selectByEntityPrimaryKey(entity);
            this.setDefaultResponseJsonResult(lResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0002E", check = true)
    @Operation(summary = "MD_PROG002D0002 - update", description = "Aggregation Method update")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdAggregationMethod>> doUpdate(@RequestBody MdAggregationMethod entity) {
        DefaultControllerJsonResultObj<MdAggregationMethod> result = this.initDefaultJsonResult();
        try {
            this.checkBuiltinReadonly(entity);
            this.handlerCheck(result, entity);
            DefaultResult<MdAggregationMethod> uResult = this.mdAggregationMethodService.update(entity);
            this.setDefaultResponseJsonResult(uResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0002D", check = true)
    @Operation(summary = "MD_PROG002D0002 - delete", description = "Aggregation Method delete")
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> doDelete(@RequestBody MdAggregationMethod entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            this.checkBuiltinReadonly(entity);
            DefaultResult<Boolean> delResult = this.mdAggregationMethodService.delete(entity);
            this.setDefaultResponseJsonResult(delResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0002Q", check = true)
    @Operation(summary = "MD_PROG002D0002 - test", description = "Aggregation Method test")
    @PostMapping(value = "/test", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Object>> doTest(@RequestBody AggregationMethodTestRequest request) {
        DefaultControllerJsonResultObj<Object> result = this.initDefaultJsonResult();
        try {
            Object testResult = AggregationMethodUtils.test(request);
            result.setValue(testResult);
            result.setSuccess(YesNoKeyProvide.YES);
            result.setMessage("彙總測試成功：" + testResult);
        } catch (Exception e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    private void handlerCheck(DefaultControllerJsonResultObj<MdAggregationMethod> result, MdAggregationMethod entity) throws ControllerException, ServiceException {
        CheckControllerFieldHandler<MdAggregationMethod> chk = this.getCheckControllerFieldHandler(result);
        chk.testField("aggrCode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(aggrCode)", "請輸入彙總代碼")
           .testField("aggrCode", entity, "!@org.qifu.util.SimpleUtils@checkBeTrueOfAZaz09Id(aggrCode)", "彙總代碼只允許輸入0-9,a-z,A-Z,-,_,.")
           .testField("aggrName", entity, "@org.apache.commons.lang3.StringUtils@isBlank(aggrName)", "請輸入彙總名稱")
           .testField("aggrType", entity, "@org.apache.commons.lang3.StringUtils@isBlank(aggrType)", "請選擇彙總類型")
           .testField("enabled", entity, "@org.apache.commons.lang3.StringUtils@isBlank(enabled)", "請選擇是否啟用")
           .throwHtmlMessage();
    }

    private void checkBuiltinReadonly(MdAggregationMethod entity) throws ServiceException, ControllerException {
        DefaultResult<MdAggregationMethod> loadResult = this.mdAggregationMethodService.selectByEntityPrimaryKey(entity);
        MdAggregationMethod dbEntity = loadResult.getValue();
        if (dbEntity != null && StringUtils.equals("BUILTIN", dbEntity.getAggrType())) {
            throw new ControllerException("BUILTIN彙總方法為系統內建資料，不能由維護畫面修改或刪除。");
        }
    }
}
