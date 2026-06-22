package org.qifu.md.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiMeasureData;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.logic.IKpiMeasureDataLogicService;
import org.qifu.md.model.KpiMeasureDataImportPreview;
import org.qifu.md.model.KpiMeasureDataImportRequest;
import org.qifu.md.model.KpiMeasureDataImportResult;
import org.qifu.md.service.IMdKpiMeasureDataService;
import org.qifu.md.service.IMdKpiService;
import org.qifu.md.service.IMdOrgMemberService;
import org.qifu.md.service.IMdOrgUnitService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG004D0001", description = "KPI Measure Data")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG004D0001")
public class MdPROG004D0001Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;
    private static final long MAX_IMPORT_FILE_SIZE = 2L * 1024L * 1024L;
    private static final String IMPORT_TEMPLATE = "\uFEFF"
            + "kpi_code,period_type,period_key,data_for_type,org_code,account,target_value,actual_value,note\r\n"
            + "KPI_CODE_HERE,MONTH,2026-06,GLOBAL,,,1000000,980000,Global monthly example\r\n"
            + "KPI_CODE_HERE,MONTH,2026-06,ORG,ORG_CODE_HERE,,100,92,Organization example\r\n"
            + "KPI_CODE_HERE,WEEK,2026-W25,ACCOUNT,,ACCOUNT_HERE,100,88,Account example\r\n";

    private final IMdKpiMeasureDataService<MdKpiMeasureData, String> mdKpiMeasureDataService;
    private final IMdKpiService<MdKpi, String> mdKpiService;
    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;
    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;
    private final IKpiMeasureDataLogicService kpiMeasureDataLogicService;

    public MdPROG004D0001Controller(IMdKpiMeasureDataService<MdKpiMeasureData, String> mdKpiMeasureDataService,
            IMdKpiService<MdKpi, String> mdKpiService,
            IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService,
            IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService,
            IKpiMeasureDataLogicService kpiMeasureDataLogicService) {
        super();
        this.mdKpiMeasureDataService = mdKpiMeasureDataService;
        this.mdKpiService = mdKpiService;
        this.mdOrgUnitService = mdOrgUnitService;
        this.mdOrgMemberService = mdOrgMemberService;
        this.kpiMeasureDataLogicService = kpiMeasureDataLogicService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG004D0001Q", check = true)
    @Operation(summary = "MD_PROG004D0001 - findPage", description = "KPI measure data query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdKpiMeasureData>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdKpiMeasureData>> result = this.initResult();
        try {
            QueryResult<List<MdKpiMeasureData>> queryResult = this.mdKpiMeasureDataService.findPage(
                    this.queryParameter(searchBody)
                        .fullEquals("kpiOid")
                        .fullEquals("periodType")
                        .fullEquals("periodKey")
                        .fullEquals("dataForType")
                        .fullEquals("account")
                        .fullEquals("orgOid")
                        .value(),
                    searchBody.getPageOf().orderBy("PERIOD_TYPE, PERIOD_KEY").sortTypeDesc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG004D0001Q", check = true)
    @Operation(summary = "MD_PROG004D0001 - findKpiList", description = "KPI option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG004D0001Q", check = true)
    @Operation(summary = "MD_PROG004D0001 - findOrgList", description = "Organization option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG004D0001Q", check = true)
    @Operation(summary = "MD_PROG004D0001 - findMemberList", description = "Member option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG004D0001Q", check = true)
    @Operation(summary = "MD_PROG004D0001 - downloadImportTemplate", description = "Download KPI measure data CSV import template")
    @GetMapping(value = "/downloadImportTemplate", produces = "text/csv")
    public ResponseEntity<byte[]> downloadImportTemplate() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"kpi-measure-data-import.csv\"")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(IMPORT_TEMPLATE.getBytes(StandardCharsets.UTF_8));
    }

    @ControllerMethodAuthority(programId = "MD_PROG004D0001A", check = true)
    @Operation(summary = "MD_PROG004D0001 - previewImport", description = "Validate and preview KPI measure data CSV")
    @PostMapping(value = "/previewImport", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<KpiMeasureDataImportPreview>> previewImport(
            @RequestParam("file") MultipartFile file) {
        DefaultControllerJsonResultObj<KpiMeasureDataImportPreview> result = this.initDefaultJsonResult();
        try {
            validateImportFile(file);
            DefaultResult<KpiMeasureDataImportPreview> preview = this.kpiMeasureDataLogicService.previewImport(file.getInputStream());
            this.setDefaultResponseJsonResult(preview, result);
        } catch (ServiceException | ControllerException | IOException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG004D0001A", check = true)
    @Operation(summary = "MD_PROG004D0001 - importCsv", description = "Confirm and import validated KPI measure data rows")
    @PostMapping(value = "/importCsv", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<KpiMeasureDataImportResult>> importCsv(
            @RequestBody KpiMeasureDataImportRequest request) {
        DefaultControllerJsonResultObj<KpiMeasureDataImportResult> result = this.initDefaultJsonResult();
        try {
            DefaultResult<KpiMeasureDataImportResult> imported = this.kpiMeasureDataLogicService.importRows(request);
            this.setDefaultResponseJsonResult(imported, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG004D0001Q", check = true)
    @Operation(summary = "MD_PROG004D0001 - load", description = "KPI measure data load")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdKpiMeasureData>> doLoad(@RequestBody MdKpiMeasureData entity) {
        DefaultControllerJsonResultObj<MdKpiMeasureData> result = this.initDefaultJsonResult();
        try {
            DefaultResult<MdKpiMeasureData> loadResult = this.mdKpiMeasureDataService.selectByEntityPrimaryKey(entity);
            this.setDefaultResponseJsonResult(loadResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG004D0001Q", check = true)
    @Operation(summary = "MD_PROG004D0001 - loadByKey", description = "KPI measure data load by natural key")
    @PostMapping(value = "/loadByKey", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdKpiMeasureData>> doLoadByKey(@RequestBody MdKpiMeasureData entity) {
        DefaultControllerJsonResultObj<MdKpiMeasureData> result = this.initDefaultJsonResult();
        try {
            DefaultResult<MdKpiMeasureData> loadResult = this.kpiMeasureDataLogicService.loadByKey(entity);
            this.setDefaultResponseJsonResult(loadResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG004D0001A", check = true)
    @Operation(summary = "MD_PROG004D0001 - saveOrUpdate", description = "KPI measure data create or update")
    @PostMapping(value = "/saveOrUpdate", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdKpiMeasureData>> doSaveOrUpdate(@RequestBody MdKpiMeasureData entity) {
        DefaultControllerJsonResultObj<MdKpiMeasureData> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            DefaultResult<MdKpiMeasureData> saveResult = this.kpiMeasureDataLogicService.saveOrUpdate(entity);
            this.setDefaultResponseJsonResult(saveResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG004D0001D", check = true)
    @Operation(summary = "MD_PROG004D0001 - delete", description = "KPI measure data delete")
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> doDelete(@RequestBody MdKpiMeasureData entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            DefaultResult<Boolean> deleteResult = this.kpiMeasureDataLogicService.delete(entity);
            this.setDefaultResponseJsonResult(deleteResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handlerCheck(DefaultControllerJsonResultObj<MdKpiMeasureData> result, MdKpiMeasureData entity) throws ControllerException, ServiceException {
        CheckControllerFieldHandler<MdKpiMeasureData> chk = CheckControllerFieldHandler.build((DefaultControllerJsonResultObj) result);
        chk.testField("kpiOid", PleaseSelect.noSelect(entity.getKpiOid()), "Please select KPI.")
           .testField("periodType", PleaseSelect.noSelect(entity.getPeriodType()), "Please select period type.")
           .testField("periodKey", entity, "@org.apache.commons.lang3.StringUtils@isBlank(periodKey)", "Please enter period key.")
           .testField("dataForType", PleaseSelect.noSelect(entity.getDataForType()), "Please select data for type.")
           .testField("actualValue", entity, "actualValue == null", "Please enter actual value.")
           .testField("locked", PleaseSelect.noSelect(entity.getLocked()), "Please select locked flag.")
           .throwHtmlMessage();
    }

    private void validateImportFile(MultipartFile file) throws ControllerException {
        if (file == null || file.isEmpty()) {
            throw new ControllerException("Please select a CSV file.");
        }
        if (file.getSize() > MAX_IMPORT_FILE_SIZE) {
            throw new ControllerException("CSV file cannot exceed 2 MB.");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new ControllerException("Only CSV files are supported.");
        }
    }
}
