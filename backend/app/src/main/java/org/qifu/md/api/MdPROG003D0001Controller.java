package org.qifu.md.api;

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
import org.qifu.base.model.YesNoKeyProvide;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdFormula;
import org.qifu.md.entity.MdFormulaRecommendRule;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.logic.IKpiMasterLogicService;
import org.qifu.md.model.KpiMasterRequest;
import org.qifu.md.service.IMdFormulaRecommendRuleService;
import org.qifu.md.service.IMdFormulaService;
import org.qifu.md.service.IMdKpiService;
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

@Tag(name = "MD_PROG003D0001", description = "KPI Master")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG003D0001")
public class MdPROG003D0001Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdKpiService<MdKpi, String> mdKpiService;
    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;
    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;
    private final IMdFormulaService<MdFormula, String> mdFormulaService;
    private final IMdFormulaRecommendRuleService<MdFormulaRecommendRule, String> mdFormulaRecommendRuleService;
    private final IKpiMasterLogicService kpiMasterLogicService;

    public MdPROG003D0001Controller(IMdKpiService<MdKpi, String> mdKpiService,
            IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService,
            IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService,
            IMdFormulaService<MdFormula, String> mdFormulaService,
            IMdFormulaRecommendRuleService<MdFormulaRecommendRule, String> mdFormulaRecommendRuleService,
            IKpiMasterLogicService kpiMasterLogicService) {
        super();
        this.mdKpiService = mdKpiService;
        this.mdOrgUnitService = mdOrgUnitService;
        this.mdOrgMemberService = mdOrgMemberService;
        this.mdFormulaService = mdFormulaService;
        this.mdFormulaRecommendRuleService = mdFormulaRecommendRuleService;
        this.kpiMasterLogicService = kpiMasterLogicService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG003D0001Q", check = true)
    @Operation(summary = "MD_PROG003D0001 - findPage", description = "KPI Master query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdKpi>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdKpi>> result = this.initResult();
        try {
            QueryResult<List<MdKpi>> queryResult = this.mdKpiService.findPage(
                    this.queryParameter(searchBody)
                        .fullLink("kpiCodeLike")
                        .fullLink("kpiNameLike")
                        .fullEquals("dataType")
                        .fullEquals("periodType")
                        .fullEquals("managementMode")
                        .fullEquals("compareMode")
                        .fullEquals("formulaSelectionMode")
                        .fullEquals("enabled")
                        .value(),
                    searchBody.getPageOf().orderBy("KPI_CODE").sortTypeAsc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG003D0001Q", check = true)
    @Operation(summary = "MD_PROG003D0001 - findList", description = "KPI Master list")
    @PostMapping(value = "/findList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdKpi>>> findList(@RequestBody MdKpi entity) {
        DefaultControllerJsonResultObj<List<MdKpi>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdKpi>> listResult = this.mdKpiService.selectList("KPI_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG003D0001Q", check = true)
    @Operation(summary = "MD_PROG003D0001 - findOrgList", description = "KPI Master organization option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG003D0001Q", check = true)
    @Operation(summary = "MD_PROG003D0001 - findMemberList", description = "KPI Master member option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG003D0001Q", check = true)
    @Operation(summary = "MD_PROG003D0001 - recommendFormula", description = "Recommend KPI formula by KPI metadata")
    @PostMapping(value = "/recommendFormula", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Map<String, Object>>> recommendFormula(@RequestBody MdKpi entity) {
        DefaultControllerJsonResultObj<Map<String, Object>> result = this.initDefaultJsonResult();
        try {
            MdFormulaRecommendRule rule = findFormulaRecommendRule(entity);
            MdFormula formula = null;
            if (rule != null && StringUtils.isNotBlank(rule.getRecommendedFormulaOid())) {
                MdFormula formulaKey = new MdFormula();
                formulaKey.setOid(rule.getRecommendedFormulaOid());
                formula = this.mdFormulaService.selectByEntityPrimaryKey(formulaKey).getValue();
            }

            Map<String, Object> value = new HashMap<>();
            value.put("rule", rule);
            value.put("formula", formula);
            value.put("recommendedFormulaOid", formula == null ? null : formula.getOid());
            result.setSuccess(YesNoKeyProvide.YES);
            result.setValue(value);
        } catch (ServiceException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG003D0001A", check = true)
    @Operation(summary = "MD_PROG003D0001 - save", description = "KPI Master create")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<KpiMasterRequest>> doSave(@RequestBody KpiMasterRequest request) {
        DefaultControllerJsonResultObj<KpiMasterRequest> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, request);
            DefaultResult<KpiMasterRequest> cResult = this.kpiMasterLogicService.create(request);
            this.setDefaultResponseJsonResult(cResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG003D0001E", check = true)
    @Operation(summary = "MD_PROG003D0001 - load", description = "KPI Master load")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<KpiMasterRequest>> doLoad(@RequestBody MdKpi entity) {
        DefaultControllerJsonResultObj<KpiMasterRequest> result = this.initDefaultJsonResult();
        try {
            DefaultResult<KpiMasterRequest> lResult = this.kpiMasterLogicService.load(entity);
            this.setDefaultResponseJsonResult(lResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG003D0001E", check = true)
    @Operation(summary = "MD_PROG003D0001 - update", description = "KPI Master update")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<KpiMasterRequest>> doUpdate(@RequestBody KpiMasterRequest request) {
        DefaultControllerJsonResultObj<KpiMasterRequest> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, request);
            DefaultResult<KpiMasterRequest> uResult = this.kpiMasterLogicService.update(request);
            this.setDefaultResponseJsonResult(uResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG003D0001D", check = true)
    @Operation(summary = "MD_PROG003D0001 - delete", description = "KPI Master delete")
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> doDelete(@RequestBody MdKpi entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            DefaultResult<Boolean> delResult = this.kpiMasterLogicService.delete(entity);
            this.setDefaultResponseJsonResult(delResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handlerCheck(DefaultControllerJsonResultObj<KpiMasterRequest> result, KpiMasterRequest request) throws ControllerException, ServiceException {
        MdKpi entity = request == null ? null : request.getKpi();
        if (entity == null) {
            result.getCheckFields().put("kpiCode", "請輸入KPI資料");
            throw new ControllerException("請輸入KPI資料");
        }
        CheckControllerFieldHandler<MdKpi> chk = CheckControllerFieldHandler.build((DefaultControllerJsonResultObj) result);
        chk.testField("kpiCode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(kpiCode)", "請輸入KPI代碼")
           .testField("kpiCode", entity, "!@org.qifu.util.SimpleUtils@checkBeTrueOfAZaz09Id(kpiCode)", "KPI代碼只允許輸入0-9,a-z,A-Z,-,_,.")
           .testField("kpiName", entity, "@org.apache.commons.lang3.StringUtils@isBlank(kpiName)", "請輸入KPI名稱")
           .testField("dataType", PleaseSelect.noSelect(entity.getDataType()), "請選擇資料型態")
           .testField("periodType", PleaseSelect.noSelect(entity.getPeriodType()), "請選擇週期")
           .testField("managementMode", PleaseSelect.noSelect(entity.getManagementMode()), "請選擇管理模式")
           .testField("compareMode", PleaseSelect.noSelect(entity.getCompareMode()), "請選擇比較模式")
           .testField("scoreCapMode", PleaseSelect.noSelect(entity.getScoreCapMode()), "請選擇分數封頂方式")
           .testField("formulaOid", PleaseSelect.noSelect(entity.getFormulaOid()), "請選擇公式")
           .testField("formulaSelectionMode", PleaseSelect.noSelect(entity.getFormulaSelectionMode()), "請選擇公式選取方式")
           .testField("aggrMethodOid", PleaseSelect.noSelect(entity.getAggrMethodOid()), "請選擇彙總方法")
           .testField("formulaVersionNo", entity, "formulaVersionNo == null || formulaVersionNo < 1", "公式版本需大於0")
           .testField("weightValue", entity, "weightValue == null", "請輸入權重")
           .testField("quasiRange", entity, "quasiRange == null", "請輸入準目標容忍範圍")
           .testField("enabled", PleaseSelect.noSelect(entity.getEnabled()), "請選擇是否啟用")
            .throwHtmlMessage();
    }

    private MdFormulaRecommendRule findFormulaRecommendRule(MdKpi entity) throws ServiceException {
        if (entity == null || PleaseSelect.noSelect(entity.getManagementMode()) || PleaseSelect.noSelect(entity.getCompareMode())) {
            return null;
        }

        MdFormulaRecommendRule rule = firstRecommendRule(recommendParams(entity, true, true, false));
        if (rule != null) {
            return rule;
        }
        rule = firstRecommendRule(recommendParams(entity, false, true, false));
        if (rule != null) {
            return rule;
        }
        rule = firstRecommendRule(recommendParams(entity, true, false, false));
        if (rule != null) {
            return rule;
        }
        rule = firstRecommendRule(recommendParams(entity, false, false, false));
        if (rule != null) {
            return rule;
        }
        return firstRecommendRule(recommendParams(entity, false, false, true));
    }

    private Map<String, Object> recommendParams(MdKpi entity, boolean includePeriodType, boolean includeDataType, boolean defaultOnly) {
        Map<String, Object> params = new HashMap<>();
        params.put("enabled", YesNoKeyProvide.YES);
        params.put("managementMode", entity.getManagementMode());
        params.put("compareMode", entity.getCompareMode());
        if (includePeriodType && !PleaseSelect.noSelect(entity.getPeriodType())) {
            params.put("periodType", entity.getPeriodType());
        }
        if (includeDataType && !PleaseSelect.noSelect(entity.getDataType())) {
            params.put("dataType", entity.getDataType());
        }
        if (defaultOnly) {
            params.put("isDefault", YesNoKeyProvide.YES);
        }
        return params;
    }

    private MdFormulaRecommendRule firstRecommendRule(Map<String, Object> params) throws ServiceException {
        DefaultResult<List<MdFormulaRecommendRule>> ruleResult = this.mdFormulaRecommendRuleService.selectListByParams(params, "PRIORITY_NO", "ASC");
        List<MdFormulaRecommendRule> ruleList = ruleResult.getValue();
        return ruleList == null || ruleList.isEmpty() ? null : ruleList.get(0);
    }
}
