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
import org.qifu.md.entity.MdFormula;
import org.qifu.md.model.FormulaTestRequest;
import org.qifu.md.service.IMdFormulaService;
import org.qifu.md.util.FormulaUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG002D0001", description = "Formula")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG002D0001")
public class MD_PROG002D0001Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdFormulaService<MdFormula, String> mdFormulaService;

    public MD_PROG002D0001Controller(IMdFormulaService<MdFormula, String> mdFormulaService) {
        super();
        this.mdFormulaService = mdFormulaService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0001Q", check = true)
    @Operation(summary = "MD_PROG002D0001 - findPage", description = "Formula query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdFormula>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdFormula>> result = this.initResult();
        try {
            QueryResult<List<MdFormula>> queryResult = this.mdFormulaService.findPage(
                    this.queryParameter(searchBody)
                        .fullLink("formulaCodeLike")
                        .fullLink("formulaNameLike")
                        .fullEquals("formulaType")
                        .fullEquals("enabled")
                        .value(),
                    searchBody.getPageOf().orderBy("FORMULA_CODE").sortTypeAsc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0001Q", check = true)
    @Operation(summary = "MD_PROG002D0001 - findList", description = "Formula list")
    @PostMapping(value = "/findList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdFormula>>> findList(@RequestBody MdFormula entity) {
        DefaultControllerJsonResultObj<List<MdFormula>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdFormula>> listResult = this.mdFormulaService.selectList();
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0001C", check = true)
    @Operation(summary = "MD_PROG002D0001 - save", description = "Formula create")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdFormula>> doSave(@RequestBody MdFormula entity) {
        DefaultControllerJsonResultObj<MdFormula> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            DefaultResult<MdFormula> cResult = this.mdFormulaService.insert(entity);
            this.setDefaultResponseJsonResult(cResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0001E", check = true)
    @Operation(summary = "MD_PROG002D0001 - load", description = "Formula load")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdFormula>> doLoad(@RequestBody MdFormula entity) {
        DefaultControllerJsonResultObj<MdFormula> result = this.initDefaultJsonResult();
        try {
            DefaultResult<MdFormula> lResult = this.mdFormulaService.selectByEntityPrimaryKey(entity);
            this.setDefaultResponseJsonResult(lResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0001U", check = true)
    @Operation(summary = "MD_PROG002D0001 - update", description = "Formula update")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdFormula>> doUpdate(@RequestBody MdFormula entity) {
        DefaultControllerJsonResultObj<MdFormula> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            this.checkBuiltinReadonly(entity);
            DefaultResult<MdFormula> uResult = this.mdFormulaService.update(entity);
            this.setDefaultResponseJsonResult(uResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0001D", check = true)
    @Operation(summary = "MD_PROG002D0001 - delete", description = "Formula delete")
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> doDelete(@RequestBody MdFormula entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            this.checkBuiltinReadonly(entity);
            DefaultResult<Boolean> delResult = this.mdFormulaService.delete(entity);
            this.setDefaultResponseJsonResult(delResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0001Q", check = true)
    @Operation(summary = "MD_PROG002D0001 - test", description = "Formula test")
    @PostMapping(value = "/test", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Object>> doTest(@RequestBody FormulaTestRequest request) {
        DefaultControllerJsonResultObj<Object> result = this.initDefaultJsonResult();
        try {
            Object testResult = FormulaUtils.test(request);
            result.setValue(testResult);
            result.setSuccess(YesNoKeyProvide.YES);
            result.setMessage("公式測試成功：" + testResult);
        } catch (Exception e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    private void handlerCheck(DefaultControllerJsonResultObj<MdFormula> result, MdFormula entity) throws ControllerException, ServiceException {
        CheckControllerFieldHandler<MdFormula> chk = this.getCheckControllerFieldHandler(result);
        chk.testField("formulaCode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(formulaCode)", "請輸入Formula代碼")
           .testField("formulaCode", entity, "!@org.qifu.util.SimpleUtils@checkBeTrueOfAZaz09Id(formulaCode)", "Formula代碼只允許輸入0-9,a-z,A-Z,-,_,.")
           .testField("formulaName", entity, "@org.apache.commons.lang3.StringUtils@isBlank(formulaName)", "請輸入Formula名稱")
           .testField("formulaType", entity, "@org.apache.commons.lang3.StringUtils@isBlank(formulaType)", "請選擇Formula類型")
           .testField("scriptType", entity, "@org.apache.commons.lang3.StringUtils@isBlank(scriptType)", "請選擇Script類型")
           .testField("scriptType", entity, "!@org.apache.commons.lang3.StringUtils@equals(scriptType, 'GROOVY')", "Script類型只允許GROOVY")
           .testField("returnType", entity, "@org.apache.commons.lang3.StringUtils@isBlank(returnType)", "請選擇回傳類型")
           .testField("versionNo", entity, "versionNo == null || versionNo < 1", "版本號需大於0")
           .testField("isSystem", entity, "@org.apache.commons.lang3.StringUtils@isBlank(isSystem)", "請選擇是否系統公式")
           .testField("isRecommendable", entity, "@org.apache.commons.lang3.StringUtils@isBlank(isRecommendable)", "請選擇是否可推薦")
           .testField("enabled", entity, "@org.apache.commons.lang3.StringUtils@isBlank(enabled)", "請選擇是否啟用")
           .throwHtmlMessage();
    }

    private void checkBuiltinReadonly(MdFormula entity) throws ServiceException, ControllerException {
        DefaultResult<MdFormula> loadResult = this.mdFormulaService.selectByEntityPrimaryKey(entity);
        MdFormula dbEntity = loadResult.getValue();
        if (dbEntity != null && StringUtils.equals("BUILTIN", dbEntity.getFormulaType())) {
            throw new ControllerException("BUILTIN公式為系統內建資料，不能由維護畫面修改或刪除。");
        }
    }
}
