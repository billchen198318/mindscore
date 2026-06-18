package org.qifu.md.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.PageOf;
import org.qifu.base.model.QueryResult;
import org.qifu.base.model.SearchBody;
import org.qifu.base.model.YesNo;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdActionPlan;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.logic.IActionReportLogicService;
import org.qifu.md.model.ActionReportQuery;
import org.qifu.md.model.ActionReportResult;
import org.qifu.md.model.ActionReportRow;
import org.qifu.md.service.IMdActionPlanService;
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

@Tag(name = "MD_PROG008D0005", description = "Action / PDCA Report")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG008D0005")
public class MdPROG008D0005Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IActionReportLogicService actionReportLogicService;
    private final IMdActionPlanService<MdActionPlan, String> mdActionPlanService;
    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;
    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;

    public MdPROG008D0005Controller(IActionReportLogicService actionReportLogicService,
            IMdActionPlanService<MdActionPlan, String> mdActionPlanService,
            IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService,
            IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService) {
        super();
        this.actionReportLogicService = actionReportLogicService;
        this.mdActionPlanService = mdActionPlanService;
        this.mdOrgUnitService = mdOrgUnitService;
        this.mdOrgMemberService = mdOrgMemberService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0005Q", check = true)
    @Operation(summary = "MD_PROG008D0005 - report", description = "Action / PDCA report query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<ActionReportRow>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<ActionReportRow>> result = this.initResult();
        try {
            DefaultResult<ActionReportResult> reportResult = this.actionReportLogicService.report(this.toQuery(searchBody));
            ActionReportResult report = reportResult.getValueEmptyThrowMessage();
            List<ActionReportRow> rows = report.getRows() == null ? List.of() : report.getRows();
            PageOf pageOf = this.pageOf(searchBody, rows.size());

            result.setSuccess(YesNo.YES);
            result.setIsAuth(YesNo.YES);
            result.setValue(this.slice(rows, pageOf));
            result.setPageOf(pageOf);
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0005Q", check = true)
    @Operation(summary = "MD_PROG008D0005 - summary", description = "Action / PDCA report summary")
    @PostMapping(value = "/summary", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<ActionReportResult>> summary(@RequestBody SearchBody searchBody) {
        DefaultControllerJsonResultObj<ActionReportResult> result = this.initDefaultJsonResult();
        try {
            DefaultResult<ActionReportResult> reportResult = this.actionReportLogicService.report(this.toQuery(searchBody));
            this.setDefaultResponseJsonResult(reportResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0005Q", check = true)
    @Operation(summary = "MD_PROG008D0005 - findPlanList", description = "Action plan option list")
    @PostMapping(value = "/findPlanList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdActionPlan>>> findPlanList(@RequestBody MdActionPlan entity) {
        DefaultControllerJsonResultObj<List<MdActionPlan>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdActionPlan>> listResult = this.mdActionPlanService.selectList("PLAN_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG008D0005Q", check = true)
    @Operation(summary = "MD_PROG008D0005 - findOrgList", description = "Action report organization option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG008D0005Q", check = true)
    @Operation(summary = "MD_PROG008D0005 - findMemberList", description = "Action report member option list")
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

    private ActionReportQuery toQuery(SearchBody searchBody) throws ControllerException {
        Map<String, String> field = searchBody == null ? Map.of() : searchBody.getField();
        if (field == null) {
            field = Map.of();
        }
        ActionReportQuery query = new ActionReportQuery();
        query.setPlanOid(StringUtils.trimToNull(field.get("planOid")));
        query.setActionStage(StringUtils.trimToNull(field.get("actionStage")));
        query.setStatus(StringUtils.trimToNull(field.get("status")));
        query.setOwnerType(StringUtils.trimToNull(field.get("ownerType")));
        query.setAccount(StringUtils.trimToNull(field.get("account")));
        query.setOrgOid(StringUtils.trimToNull(field.get("orgOid")));
        query.setSourceType(StringUtils.trimToNull(field.get("sourceType")));
        query.setSourceOid(StringUtils.trimToNull(field.get("sourceOid")));
        query.setStartDateFrom(parseDate(field.get("startDateFrom"), "startDateFrom"));
        query.setStartDateTo(parseDate(field.get("startDateTo"), "startDateTo"));
        query.setEndDateFrom(parseDate(field.get("endDateFrom"), "endDateFrom"));
        query.setEndDateTo(parseDate(field.get("endDateTo"), "endDateTo"));
        query.setOverdueOnly("Y".equalsIgnoreCase(field.get("overdueOnly")) || "true".equalsIgnoreCase(field.get("overdueOnly")));
        return query;
    }

    private java.util.Date parseDate(String value, String fieldName) throws ControllerException {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setLenient(false);
            return format.parse(value);
        } catch (ParseException e) {
            throw new ControllerException(fieldName + " must use yyyy-MM-dd.");
        }
    }

    private PageOf pageOf(SearchBody searchBody, int count) {
        PageOf pageOf = searchBody == null || searchBody.getPageOf() == null ? new PageOf() : searchBody.getPageOf();
        pageOf.setCountSize(String.valueOf(count));
        pageOf.toCalculateSize();
        return pageOf;
    }

    private List<ActionReportRow> slice(List<ActionReportRow> rows, PageOf pageOf) {
        int showRow = NumberUtils.toInt(pageOf.getShowRow(), PageOf.DEFAULT_ROW);
        int select = NumberUtils.toInt(pageOf.getSelect(), 1);
        int fromIndex = Math.max(0, (select - 1) * showRow);
        if (fromIndex >= rows.size()) {
            return new ArrayList<>();
        }
        int toIndex = Math.min(rows.size(), fromIndex + showRow);
        return new ArrayList<>(rows.subList(fromIndex, toIndex));
    }
}
