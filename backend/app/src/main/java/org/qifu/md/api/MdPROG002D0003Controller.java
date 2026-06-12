package org.qifu.md.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
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
import org.qifu.md.entity.MdFormulaRecommendRule;
import org.qifu.md.service.IMdFormulaRecommendRuleService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG002D0003", description = "Formula Recommend Rule")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG002D0003")
public class MdPROG002D0003Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdFormulaRecommendRuleService<MdFormulaRecommendRule, String> mdFormulaRecommendRuleService;

    public MdPROG002D0003Controller(IMdFormulaRecommendRuleService<MdFormulaRecommendRule, String> mdFormulaRecommendRuleService) {
        super();
        this.mdFormulaRecommendRuleService = mdFormulaRecommendRuleService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0003Q", check = true)
    @Operation(summary = "MD_PROG002D0003 - findPage", description = "Formula Recommend Rule query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdFormulaRecommendRule>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdFormulaRecommendRule>> result = this.initResult();
        try {
            QueryResult<List<MdFormulaRecommendRule>> queryResult = this.mdFormulaRecommendRuleService.findPage(
                    this.queryParameter(searchBody)
                        .fullLink("ruleCodeLike")
                        .fullLink("ruleNameLike")
                        .fullEquals("managementMode")
                        .fullEquals("compareMode")
                        .fullEquals("periodType")
                        .fullEquals("dataType")
                        .fullEquals("isDefault")
                        .fullEquals("enabled")
                        .value(),
                    searchBody.getPageOf().orderBy("PRIORITY_NO").sortTypeAsc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0003Q", check = true)
    @Operation(summary = "MD_PROG002D0003 - findList", description = "Formula Recommend Rule list")
    @PostMapping(value = "/findList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdFormulaRecommendRule>>> findList(@RequestBody MdFormulaRecommendRule entity) {
        DefaultControllerJsonResultObj<List<MdFormulaRecommendRule>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdFormulaRecommendRule>> listResult = this.mdFormulaRecommendRuleService.selectList("PRIORITY_NO", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0003A", check = true)
    @Operation(summary = "MD_PROG002D0003 - save", description = "Formula Recommend Rule create")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdFormulaRecommendRule>> doSave(@RequestBody MdFormulaRecommendRule entity) {
        DefaultControllerJsonResultObj<MdFormulaRecommendRule> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            this.checkSingleEnabledDefault(entity);
            DefaultResult<MdFormulaRecommendRule> cResult = this.mdFormulaRecommendRuleService.insert(entity);
            this.setDefaultResponseJsonResult(cResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0003E", check = true)
    @Operation(summary = "MD_PROG002D0003 - load", description = "Formula Recommend Rule load")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdFormulaRecommendRule>> doLoad(@RequestBody MdFormulaRecommendRule entity) {
        DefaultControllerJsonResultObj<MdFormulaRecommendRule> result = this.initDefaultJsonResult();
        try {
            DefaultResult<MdFormulaRecommendRule> lResult = this.mdFormulaRecommendRuleService.selectByEntityPrimaryKey(entity);
            this.setDefaultResponseJsonResult(lResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0003E", check = true)
    @Operation(summary = "MD_PROG002D0003 - update", description = "Formula Recommend Rule update")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdFormulaRecommendRule>> doUpdate(@RequestBody MdFormulaRecommendRule entity) {
        DefaultControllerJsonResultObj<MdFormulaRecommendRule> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            this.checkSingleEnabledDefault(entity);
            DefaultResult<MdFormulaRecommendRule> uResult = this.mdFormulaRecommendRuleService.update(entity);
            this.setDefaultResponseJsonResult(uResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG002D0003D", check = true)
    @Operation(summary = "MD_PROG002D0003 - delete", description = "Formula Recommend Rule delete")
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> doDelete(@RequestBody MdFormulaRecommendRule entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            DefaultResult<Boolean> delResult = this.mdFormulaRecommendRuleService.delete(entity);
            this.setDefaultResponseJsonResult(delResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    private void handlerCheck(DefaultControllerJsonResultObj<MdFormulaRecommendRule> result, MdFormulaRecommendRule entity) throws ControllerException, ServiceException {
        CheckControllerFieldHandler<MdFormulaRecommendRule> chk = this.getCheckControllerFieldHandler(result);
        chk.testField("ruleCode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(ruleCode)", "請輸入推薦規則代碼")
           .testField("ruleCode", entity, "!@org.qifu.util.SimpleUtils@checkBeTrueOfAZaz09Id(ruleCode)", "推薦規則代碼只允許輸入0-9,a-z,A-Z,-,_,.")
           .testField("ruleName", entity, "@org.apache.commons.lang3.StringUtils@isBlank(ruleName)", "請輸入推薦規則名稱")
           .testField("recommendedFormulaOid", PleaseSelect.noSelect(entity.getRecommendedFormulaOid()), "請選擇推薦公式")
           .testField("priorityNo", entity, "priorityNo == null || priorityNo < 1", "優先序需大於0")
           .testField("isDefault", entity, "@org.apache.commons.lang3.StringUtils@isBlank(isDefault)", "請選擇是否預設規則")
           .testField("enabled", entity, "@org.apache.commons.lang3.StringUtils@isBlank(enabled)", "請選擇是否啟用")
           .throwHtmlMessage();
    }

    private void checkSingleEnabledDefault(MdFormulaRecommendRule entity) throws ServiceException, ControllerException {
        if (!Strings.CS.equals("Y", entity.getIsDefault()) || !Strings.CS.equals("Y", entity.getEnabled())) {
            return;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("isDefault", "Y");
        param.put("enabled", "Y");
        List<MdFormulaRecommendRule> defaultRules = this.mdFormulaRecommendRuleService.selectListByParams(param).getValue();
        if (CollectionUtils.isEmpty(defaultRules)) {
            return;
        }
        for (MdFormulaRecommendRule defaultRule : defaultRules) {
            if (defaultRule != null && !Strings.CS.equals(defaultRule.getOid(), entity.getOid())) {
                throw new ControllerException("只能存在一筆啟用中的預設推薦規則。");
            }
        }
    }
}
