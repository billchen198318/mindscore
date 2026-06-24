package org.qifu.md.api;

import java.util.List;
import java.util.Map;

import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.QueryResult;
import org.qifu.base.model.SearchBody;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdPerformanceSignal;
import org.qifu.md.logic.IPerformanceSignalLogicService;
import org.qifu.md.model.PerformanceSignalGenerationResult;
import org.qifu.md.service.IMdKpiService;
import org.qifu.md.service.IMdPerformanceSignalService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG010D0002", description = "Performance Signal")
@RestController
@RequestMapping("/api/MD_PROG010D0002")
public class MdPROG010D0002Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdPerformanceSignalService<MdPerformanceSignal, String> signalService;
    private final IMdKpiService<MdKpi, String> kpiService;
    private final IPerformanceSignalLogicService signalLogicService;

    public MdPROG010D0002Controller(
            IMdPerformanceSignalService<MdPerformanceSignal, String> signalService,
            IMdKpiService<MdKpi, String> kpiService,
            IPerformanceSignalLogicService signalLogicService) {
        this.signalService = signalService;
        this.kpiService = kpiService;
        this.signalLogicService = signalLogicService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0002Q", check = true)
    @Operation(summary = "Performance signal query")
    @PostMapping(value = "/findPage", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QueryResult<List<MdPerformanceSignal>>> findPage(@RequestBody SearchBody body) {
        QueryResult<List<MdPerformanceSignal>> result = initResult();
        try {
            Map<String, Object> params = queryParameter(body)
                    .fullEquals("signalType").fullEquals("sourceType")
                    .fullLink("sourceCodeLike").fullLink("sourceNameLike")
                    .fullEquals("periodType").fullEquals("periodKey")
                    .fullEquals("ownerAccount").fullEquals("orgOid")
                    .fullEquals("statusCode").fullEquals("riskLevel")
                    .fullEquals("signalStatus").fullEquals("snapshotOid").value();
            QueryResult<List<MdPerformanceSignal>> query = signalService.findPage(
                    params, body.getPageOf().orderBy("GENERATED_AT").sortTypeDesc());
            setQueryResponseJsonResult(query, result, body.getPageOf());
        } catch (ServiceException | ControllerException e) {
            noSuccessResult(result, e);
        }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0002Q", check = true)
    @Operation(summary = "KPI option list")
    @PostMapping(value = "/findKpiList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdKpi>>> findKpiList(
            @RequestBody Map<String, Object> request) {
        DefaultControllerJsonResultObj<List<MdKpi>> result = initDefaultJsonResult();
        try {
            setDefaultResponseJsonResult(kpiService.selectList("KPI_CODE", "ASC"), result);
        } catch (ServiceException | ControllerException e) {
            exceptionResult(result, e);
        }
        return ResponseEntity.ok(result);
    }
    @ControllerMethodAuthority(programId = "MD_PROG010D0002U", check = true)
    @Operation(summary = "Generate KPI signals")
    @PostMapping(value = "/generateKpi", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<PerformanceSignalGenerationResult>> generateKpi(
            @RequestBody Map<String, Object> request) {
        DefaultControllerJsonResultObj<PerformanceSignalGenerationResult> result = initDefaultJsonResult();
        try {
            DefaultResult<PerformanceSignalGenerationResult> generation = signalLogicService.generateKpiSignals(request);
            setDefaultResponseJsonResult(generation, result);
        } catch (ServiceException | ControllerException e) {
            exceptionResult(result, e);
        }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0002U", check = true)
    @Operation(summary = "Generate KPI signals by snapshot")
    @PostMapping(value = "/generateKpiBySnapshot", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdPerformanceSignal>>> generateKpiBySnapshot(
            @RequestBody Map<String, Object> request) {
        DefaultControllerJsonResultObj<List<MdPerformanceSignal>> result = initDefaultJsonResult();
        try {
            DefaultResult<List<MdPerformanceSignal>> generation = signalLogicService
                    .generateKpiSignalsBySnapshotOid(request == null || request.get("snapshotOid") == null ? null : String.valueOf(request.get("snapshotOid")));
            setDefaultResponseJsonResult(generation, result);
        } catch (ServiceException | ControllerException e) {
            exceptionResult(result, e);
        }
        return ResponseEntity.ok(result);
    }
}