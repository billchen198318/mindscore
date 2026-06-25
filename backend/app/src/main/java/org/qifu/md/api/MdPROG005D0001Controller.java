package org.qifu.md.api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.QueryResult;
import org.qifu.base.model.SearchBody;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiMeasureData;
import org.qifu.md.entity.MdKpiScoreSnapshot;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.logic.IKpiReportLogicService;
import org.qifu.md.logic.IKpiScoreCalculationLogicService;
import org.qifu.md.model.KpiReportQueryRequest;
import org.qifu.md.model.KpiReportScoreView;
import org.qifu.md.model.KpiReportSummary;
import org.qifu.md.service.IMdKpiScoreSnapshotService;
import org.qifu.md.service.IMdKpiService;
import org.qifu.md.service.IMdOrgMemberService;
import org.qifu.md.service.IMdOrgUnitService;
import org.qifu.md.util.PeriodKeyUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG005D0001", description = "KPI Report")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG005D0001")
public class MdPROG005D0001Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;
    private static final Map<String, PeriodRangeLimit> PERIOD_RANGE_LIMITS = Map.of(
            "DAY", new PeriodRangeLimit(365, "days"),
            "WEEK", new PeriodRangeLimit(104, "weeks"),
            "MONTH", new PeriodRangeLimit(32, "months"),
            "QUARTER", new PeriodRangeLimit(12, "quarters"),
            "HALFYEAR", new PeriodRangeLimit(12, "half-years"),
            "YEAR", new PeriodRangeLimit(6, "years"));

    private final IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> mdKpiScoreSnapshotService;
    private final IMdKpiService<MdKpi, String> mdKpiService;
    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;
    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;
    private final IKpiReportLogicService kpiReportLogicService;
    private final IKpiScoreCalculationLogicService kpiScoreCalculationLogicService;

    public MdPROG005D0001Controller(IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> mdKpiScoreSnapshotService,
            IMdKpiService<MdKpi, String> mdKpiService,
            IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService,
            IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService,
            IKpiReportLogicService kpiReportLogicService,
            IKpiScoreCalculationLogicService kpiScoreCalculationLogicService) {
        super();
        this.mdKpiScoreSnapshotService = mdKpiScoreSnapshotService;
        this.mdKpiService = mdKpiService;
        this.mdOrgUnitService = mdOrgUnitService;
        this.mdOrgMemberService = mdOrgMemberService;
        this.kpiReportLogicService = kpiReportLogicService;
        this.kpiScoreCalculationLogicService = kpiScoreCalculationLogicService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG005D0001Q", check = true)
    @Operation(summary = "MD_PROG005D0001 - reportQuery", description = "KPI score snapshot report query")
    @PostMapping(value = "/reportQuery", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<KpiReportScoreView>>> reportQuery(@RequestBody SearchBody searchBody) {
        QueryResult<List<KpiReportScoreView>> result = this.initResult();
        try {
            KpiReportQueryRequest request = toRequest(searchBody);
            validateReportRequest(request);
            recalculateBeforeReport(request);
            Map<String, Object> params = this.queryParameter(searchBody)
                    .fullEquals("kpiOid")
                    .fullEquals("periodType")
                    .fullEquals("periodKey")
                    .fullEquals("dataForType")
                    .fullEquals("account")
                    .fullEquals("orgOid")
                    .value();
            applyReportRangeParams(params, searchBody);
            QueryResult<List<MdKpiScoreSnapshot>> snapshotResult = this.mdKpiScoreSnapshotService.findPage(
                    params,
                    searchBody.getPageOf().orderBy("PERIOD_TYPE, PERIOD_KEY, KPI_OID").sortTypeDesc());
            DefaultResult<List<KpiReportScoreView>> viewResult = this.kpiReportLogicService.enrich(snapshotResult.getValue());
            this.setQueryResponseJsonResult(viewResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG005D0001Q", check = true)
    @Operation(summary = "MD_PROG005D0001 - trend", description = "KPI score trend")
    @PostMapping(value = "/trend", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<KpiReportScoreView>>> trend(@RequestBody KpiReportQueryRequest request) {
        DefaultControllerJsonResultObj<List<KpiReportScoreView>> result = this.initDefaultJsonResult();
        try {
            validateReportRequest(request);
            DefaultResult<List<KpiReportScoreView>> trendResult = this.kpiReportLogicService.trend(request);
            this.setDefaultResponseJsonResult(trendResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG005D0001Q", check = true)
    @Operation(summary = "MD_PROG005D0001 - targetActual", description = "KPI target versus actual")
    @PostMapping(value = "/targetActual", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<KpiReportScoreView>>> targetActual(@RequestBody KpiReportQueryRequest request) {
        DefaultControllerJsonResultObj<List<KpiReportScoreView>> result = this.initDefaultJsonResult();
        try {
            validateReportRequest(request);
            DefaultResult<List<KpiReportScoreView>> targetActualResult = this.kpiReportLogicService.targetActual(request);
            this.setDefaultResponseJsonResult(targetActualResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG005D0001Q", check = true)
    @Operation(summary = "MD_PROG005D0001 - summary", description = "KPI report summary")
    @PostMapping(value = "/summary", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<KpiReportSummary>> summary(@RequestBody KpiReportQueryRequest request) {
        DefaultControllerJsonResultObj<KpiReportSummary> result = this.initDefaultJsonResult();
        try {
            validateReportRequest(request);
            DefaultResult<KpiReportSummary> summaryResult = this.kpiReportLogicService.summary(request);
            this.setDefaultResponseJsonResult(summaryResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG005D0001Q", check = true)
    @Operation(summary = "MD_PROG005D0001 - findKpiList", description = "KPI option list")
    @PostMapping(value = "/findKpiList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdKpi>>> findKpiList(@RequestBody Map<String, Object> entity) {
        DefaultControllerJsonResultObj<List<MdKpi>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdKpi>> listResult = this.mdKpiService.selectList("KPI_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG005D0001Q", check = true)
    @Operation(summary = "MD_PROG005D0001 - findOrgList", description = "Organization option list")
    @PostMapping(value = "/findOrgList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdOrgUnit>>> findOrgList(@RequestBody Map<String, Object> entity) {
        DefaultControllerJsonResultObj<List<MdOrgUnit>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdOrgUnit>> listResult = this.mdOrgUnitService.selectList("ORG_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG005D0001Q", check = true)
    @Operation(summary = "MD_PROG005D0001 - findMemberList", description = "Member option list")
    @PostMapping(value = "/findMemberList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdOrgMember>>> findMemberList(@RequestBody Map<String, Object> entity) {
        DefaultControllerJsonResultObj<List<MdOrgMember>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdOrgMember>> listResult = this.mdOrgMemberService.selectList("ACCOUNT", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    private KpiReportQueryRequest toRequest(SearchBody searchBody) {
        KpiReportQueryRequest request = new KpiReportQueryRequest();
        if (searchBody == null || searchBody.getField() == null) {
            return request;
        }
        Map<String, String> field = searchBody.getField();
        request.setKpiOid(field.get("kpiOid"));
        request.setPeriodType(field.get("periodType"));
        request.setPeriodKey(field.get("periodKey"));
        request.setPeriodKeyFrom(field.get("periodKeyFrom"));
        request.setPeriodKeyTo(field.get("periodKeyTo"));
        request.setDataForType(field.get("dataForType"));
        request.setAccount(field.get("account"));
        request.setOrgOid(field.get("orgOid"));
        return request;
    }

    private void applyReportRangeParams(Map<String, Object> params, SearchBody searchBody) {
        if (params == null || searchBody == null || searchBody.getField() == null) {
            return;
        }
        String periodKeyFrom = searchBody.getField().get("periodKeyFrom");
        String periodKeyTo = searchBody.getField().get("periodKeyTo");
        if (StringUtils.isNotBlank(periodKeyFrom) && StringUtils.isNotBlank(periodKeyTo)) {
            params.remove("periodKey");
            params.put("periodKeyFrom", StringUtils.trim(periodKeyFrom));
            params.put("periodKeyTo", StringUtils.trim(periodKeyTo));
        }
    }

    private void validateReportRequest(KpiReportQueryRequest request) throws ServiceException {
        if (request == null || StringUtils.isBlank(request.getPeriodType())) {
            throw new ServiceException("Please select period type.");
        }
        boolean hasFrom = StringUtils.isNotBlank(request.getPeriodKeyFrom());
        boolean hasTo = StringUtils.isNotBlank(request.getPeriodKeyTo());
        if (hasFrom != hasTo) {
            throw new ServiceException("Period From and Period To must be entered together.");
        }
        if (hasFrom) {
            expandPeriodKeys(request.getPeriodType(), request.getPeriodKeyFrom(), request.getPeriodKeyTo());
            return;
        }
        if (StringUtils.isNotBlank(request.getPeriodKey())) {
            PeriodKeyUtils.parseStart(request.getPeriodType(), request.getPeriodKey());
            return;
        }
        throw new ServiceException("Please select period.");
    }

    private void recalculateBeforeReport(KpiReportQueryRequest request) throws ServiceException {
        if (request == null || StringUtils.isBlank(request.getPeriodType())) {
            return;
        }
        List<String> periodKeys = resolveRealtimePeriodKeys(request);
        for (String periodKey : periodKeys) {
            MdKpiMeasureData criteria = new MdKpiMeasureData();
            criteria.setKpiOid(StringUtils.trimToNull(request.getKpiOid()));
            criteria.setPeriodType(StringUtils.trimToNull(request.getPeriodType()));
            criteria.setPeriodKey(periodKey);
            applyDataForCriteria(criteria, request);
            this.kpiScoreCalculationLogicService.recalculateByPeriod(criteria);
        }
    }

    private void applyDataForCriteria(MdKpiMeasureData criteria, KpiReportQueryRequest request) {
        String dataForType = StringUtils.trimToNull(request.getDataForType());
        criteria.setDataForType(dataForType);
        if ("ACCOUNT".equals(dataForType)) {
            criteria.setAccount(StringUtils.trimToNull(request.getAccount()));
            return;
        }
        if ("ORG".equals(dataForType)) {
            criteria.setOrgOid(StringUtils.trimToNull(request.getOrgOid()));
        }
    }

    private List<String> resolveRealtimePeriodKeys(KpiReportQueryRequest request) throws ServiceException {
        List<String> periodKeys = new ArrayList<>();
        if (StringUtils.isNotBlank(request.getPeriodKeyFrom()) && StringUtils.isNotBlank(request.getPeriodKeyTo())) {
            return expandPeriodKeys(request.getPeriodType(), request.getPeriodKeyFrom(), request.getPeriodKeyTo());
        }
        if (StringUtils.isNotBlank(request.getPeriodKey())) {
            periodKeys.add(StringUtils.trim(request.getPeriodKey()));
            return periodKeys;
        }
        return periodKeys;
    }

    private List<String> expandPeriodKeys(String periodType, String from, String to) throws ServiceException {
        List<String> keys = new ArrayList<>();
        PeriodRangeLimit limit = PERIOD_RANGE_LIMITS.get(periodType);
        if (limit == null) {
            throw new ServiceException("Unsupported period type: " + periodType);
        }
        LocalDate current = PeriodKeyUtils.parseStart(periodType, from);
        LocalDate end = PeriodKeyUtils.parseStart(periodType, to);
        if (current.isAfter(end)) {
            throw new ServiceException("Period From must be earlier than or equal to Period To.");
        }
        while (!current.isAfter(end)) {
            if (keys.size() >= limit.max()) {
                throw new ServiceException("Period range cannot exceed " + limit.max() + " " + limit.unit() + ".");
            }
            keys.add(PeriodKeyUtils.format(periodType, current));
            current = PeriodKeyUtils.next(periodType, current);
        }
        return keys;
    }

    private record PeriodRangeLimit(int max, String unit) {
    }
}
