package org.qifu.md.api;

import java.util.List;

import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.CheckControllerFieldHandler;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.QueryResult;
import org.qifu.base.model.SearchBody;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.service.IMdKpiService;
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

    public MdPROG003D0001Controller(IMdKpiService<MdKpi, String> mdKpiService) {
        super();
        this.mdKpiService = mdKpiService;
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

    @ControllerMethodAuthority(programId = "MD_PROG003D0001A", check = true)
    @Operation(summary = "MD_PROG003D0001 - save", description = "KPI Master create")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdKpi>> doSave(@RequestBody MdKpi entity) {
        DefaultControllerJsonResultObj<MdKpi> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            DefaultResult<MdKpi> cResult = this.mdKpiService.insert(entity);
            this.setDefaultResponseJsonResult(cResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG003D0001E", check = true)
    @Operation(summary = "MD_PROG003D0001 - load", description = "KPI Master load")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdKpi>> doLoad(@RequestBody MdKpi entity) {
        DefaultControllerJsonResultObj<MdKpi> result = this.initDefaultJsonResult();
        try {
            DefaultResult<MdKpi> lResult = this.mdKpiService.selectByEntityPrimaryKey(entity);
            this.setDefaultResponseJsonResult(lResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG003D0001E", check = true)
    @Operation(summary = "MD_PROG003D0001 - update", description = "KPI Master update")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdKpi>> doUpdate(@RequestBody MdKpi entity) {
        DefaultControllerJsonResultObj<MdKpi> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            DefaultResult<MdKpi> uResult = this.mdKpiService.update(entity);
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
            DefaultResult<Boolean> delResult = this.mdKpiService.delete(entity);
            this.setDefaultResponseJsonResult(delResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    private void handlerCheck(DefaultControllerJsonResultObj<MdKpi> result, MdKpi entity) throws ControllerException, ServiceException {
        CheckControllerFieldHandler<MdKpi> chk = this.getCheckControllerFieldHandler(result);
        chk.testField("kpiCode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(kpiCode)", "請輸入KPI代碼")
           .testField("kpiCode", entity, "!@org.qifu.util.SimpleUtils@checkBeTrueOfAZaz09Id(kpiCode)", "KPI代碼只允許輸入0-9,a-z,A-Z,-,_,.")
           .testField("kpiName", entity, "@org.apache.commons.lang3.StringUtils@isBlank(kpiName)", "請輸入KPI名稱")
           .testField("dataType", entity, "@org.apache.commons.lang3.StringUtils@isBlank(dataType)", "請選擇資料型態")
           .testField("periodType", entity, "@org.apache.commons.lang3.StringUtils@isBlank(periodType)", "請選擇週期")
           .testField("managementMode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(managementMode)", "請選擇管理模式")
           .testField("compareMode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(compareMode)", "請選擇比較模式")
           .testField("scoreCapMode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(scoreCapMode)", "請選擇分數封頂方式")
           .testField("formulaOid", entity, "@org.apache.commons.lang3.StringUtils@isBlank(formulaOid)", "請選擇公式")
           .testField("formulaSelectionMode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(formulaSelectionMode)", "請選擇公式選取方式")
           .testField("aggrMethodOid", entity, "@org.apache.commons.lang3.StringUtils@isBlank(aggrMethodOid)", "請選擇彙總方法")
           .testField("formulaVersionNo", entity, "formulaVersionNo == null || formulaVersionNo < 1", "公式版本需大於0")
           .testField("weightValue", entity, "weightValue == null", "請輸入權重")
           .testField("quasiRange", entity, "quasiRange == null", "請輸入準目標容忍範圍")
           .testField("enabled", entity, "@org.apache.commons.lang3.StringUtils@isBlank(enabled)", "請選擇是否啟用")
           .throwHtmlMessage();
    }
}
