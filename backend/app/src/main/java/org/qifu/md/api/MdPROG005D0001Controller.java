package org.qifu.md.api;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
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
            recalculateBeforeReport(toRequest(searchBody));
            QueryResult<List<MdKpiScoreSnapshot>> snapshotResult = this.mdKpiScoreSnapshotService.findPage(
                    this.queryParameter(searchBody)
                        .fullEquals("kpiOid")
                        .fullEquals("periodType")
                        .fullEquals("periodKey")
                        .fullEquals("dataForType")
                        .fullEquals("account")
                        .fullEquals("orgOid")
                        .value(),
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
            recalculateBeforeReport(request);
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
            recalculateBeforeReport(request);
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
            recalculateBeforeReport(request);
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
        request.setDataForType(field.get("dataForType"));
        request.setAccount(field.get("account"));
        request.setOrgOid(field.get("orgOid"));
        return request;
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
            criteria.setDataForType(StringUtils.trimToNull(request.getDataForType()));
            criteria.setAccount(StringUtils.trimToNull(request.getAccount()));
            criteria.setOrgOid(StringUtils.trimToNull(request.getOrgOid()));
            this.kpiScoreCalculationLogicService.recalculateByPeriod(criteria);
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
        LocalDate current = parsePeriodStart(periodType, from);
        LocalDate end = parsePeriodStart(periodType, to);
        int guard = 0;
        while (!current.isAfter(end) && guard < 120) {
            keys.add(formatPeriodKey(periodType, current));
            current = nextPeriod(periodType, current);
            guard++;
        }
        return keys;
    }

    private LocalDate parsePeriodStart(String periodType, String periodKey) throws ServiceException {
        try {
            if ("DAY".equals(periodType)) {
                return LocalDate.parse(periodKey);
            }
            if ("WEEK".equals(periodType)) {
                String[] parts = periodKey.split("-W");
                return LocalDate.of(Integer.parseInt(parts[0]), 1, 4)
                        .with(WeekFields.ISO.weekOfWeekBasedYear(), Integer.parseInt(parts[1]))
                        .with(WeekFields.ISO.dayOfWeek(), 1);
            }
            if ("MONTH".equals(periodType)) {
                return LocalDate.parse(periodKey + "-01");
            }
            if ("QUARTER".equals(periodType)) {
                String[] parts = periodKey.split("-Q");
                return LocalDate.of(Integer.parseInt(parts[0]), (Integer.parseInt(parts[1]) - 1) * 3 + 1, 1);
            }
            if ("HALFYEAR".equals(periodType)) {
                String[] parts = periodKey.split("-H");
                return LocalDate.of(Integer.parseInt(parts[0]), "1".equals(parts[1]) ? 1 : 7, 1);
            }
            if ("YEAR".equals(periodType)) {
                return LocalDate.of(Integer.parseInt(periodKey), 1, 1);
            }
        } catch (RuntimeException e) {
            throw new ServiceException("Invalid period key: " + periodKey);
        }
        throw new ServiceException("Unsupported period type: " + periodType);
    }

    private LocalDate nextPeriod(String periodType, LocalDate current) throws ServiceException {
        if ("DAY".equals(periodType)) {
            return current.plusDays(1);
        }
        if ("WEEK".equals(periodType)) {
            return current.plusWeeks(1);
        }
        if ("MONTH".equals(periodType)) {
            return current.plusMonths(1);
        }
        if ("QUARTER".equals(periodType)) {
            return current.plusMonths(3);
        }
        if ("HALFYEAR".equals(periodType)) {
            return current.plusMonths(6);
        }
        if ("YEAR".equals(periodType)) {
            return current.plusYears(1);
        }
        throw new ServiceException("Unsupported period type: " + periodType);
    }

    private String formatPeriodKey(String periodType, LocalDate date) throws ServiceException {
        if ("DAY".equals(periodType)) {
            return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        if ("WEEK".equals(periodType)) {
            int year = date.get(WeekFields.ISO.weekBasedYear());
            int week = date.get(WeekFields.ISO.weekOfWeekBasedYear());
            return String.format("%04d-W%02d", year, week);
        }
        if ("MONTH".equals(periodType)) {
            return String.format("%04d-%02d", date.getYear(), date.getMonthValue());
        }
        if ("QUARTER".equals(periodType)) {
            return String.format("%04d-Q%d", date.getYear(), ((date.getMonthValue() - 1) / 3) + 1);
        }
        if ("HALFYEAR".equals(periodType)) {
            return String.format("%04d-H%d", date.getYear(), date.getMonthValue() <= 6 ? 1 : 2);
        }
        if ("YEAR".equals(periodType)) {
            return String.valueOf(date.getYear());
        }
        throw new ServiceException("Unsupported period type: " + periodType);
    }
}
