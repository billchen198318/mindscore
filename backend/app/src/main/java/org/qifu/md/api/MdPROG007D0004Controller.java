package org.qifu.md.api;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdOkrCycle;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.entity.MdStrategyObjective;
import org.qifu.md.entity.MdStrategyObjectiveLink;
import org.qifu.md.entity.MdStrategyTheme;
import org.qifu.md.entity.MdStrategyWorkspace;
import org.qifu.md.service.IMdKpiService;
import org.qifu.md.service.IMdOkrCycleService;
import org.qifu.md.service.IMdOkrObjectiveService;
import org.qifu.md.service.IMdStrategyObjectiveLinkService;
import org.qifu.md.service.IMdStrategyObjectiveService;
import org.qifu.md.service.IMdStrategyThemeService;
import org.qifu.md.service.IMdStrategyWorkspaceService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG007D0004", description = "Strategy Objective Link")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG007D0004")
public class MdPROG007D0004Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;
    private static final String LINK_TYPE_KPI = "KPI";
    private static final String LINK_TYPE_OKR_OBJECTIVE = "OKR_OBJECTIVE";

    private final IMdStrategyObjectiveLinkService<MdStrategyObjectiveLink, String> mdStrategyObjectiveLinkService;
    private final IMdStrategyObjectiveService<MdStrategyObjective, String> mdStrategyObjectiveService;
    private final IMdStrategyThemeService<MdStrategyTheme, String> mdStrategyThemeService;
    private final IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService;
    private final IMdKpiService<MdKpi, String> mdKpiService;
    private final IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService;
    private final IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService;

    public MdPROG007D0004Controller(IMdStrategyObjectiveLinkService<MdStrategyObjectiveLink, String> mdStrategyObjectiveLinkService,
            IMdStrategyObjectiveService<MdStrategyObjective, String> mdStrategyObjectiveService,
            IMdStrategyThemeService<MdStrategyTheme, String> mdStrategyThemeService,
            IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService,
            IMdKpiService<MdKpi, String> mdKpiService,
            IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService,
            IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService) {
        super();
        this.mdStrategyObjectiveLinkService = mdStrategyObjectiveLinkService;
        this.mdStrategyObjectiveService = mdStrategyObjectiveService;
        this.mdStrategyThemeService = mdStrategyThemeService;
        this.mdStrategyWorkspaceService = mdStrategyWorkspaceService;
        this.mdKpiService = mdKpiService;
        this.mdOkrCycleService = mdOkrCycleService;
        this.mdOkrObjectiveService = mdOkrObjectiveService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0004Q", check = true)
    @Operation(summary = "MD_PROG007D0004 - findPage", description = "Strategy objective link query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdStrategyObjectiveLink>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdStrategyObjectiveLink>> result = this.initResult();
        try {
            QueryResult<List<MdStrategyObjectiveLink>> queryResult = this.mdStrategyObjectiveLinkService.findPage(
                    this.queryParameter(searchBody)
                        .fullEquals("strategyObjectiveOid")
                        .fullEquals("linkType")
                        .fullEquals("linkOid")
                        .value(),
                    searchBody.getPageOf().orderBy("SORT_NO").sortTypeAsc().orderBy("LINK_TYPE").sortTypeAsc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0004Q", check = true)
    @Operation(summary = "MD_PROG007D0004 - findWorkspaceList", description = "Strategy workspace list")
    @PostMapping(value = "/findWorkspaceList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdStrategyWorkspace>>> findWorkspaceList(@RequestBody MdStrategyWorkspace entity) {
        DefaultControllerJsonResultObj<List<MdStrategyWorkspace>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdStrategyWorkspace>> listResult = this.mdStrategyWorkspaceService.selectList("WORKSPACE_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0004Q", check = true)
    @Operation(summary = "MD_PROG007D0004 - findThemeList", description = "Strategy theme list")
    @PostMapping(value = "/findThemeList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdStrategyTheme>>> findThemeList(@RequestBody MdStrategyTheme entity) {
        DefaultControllerJsonResultObj<List<MdStrategyTheme>> result = this.initDefaultJsonResult();
        try {
            Map<String, Object> params = new HashMap<>();
            if (!PleaseSelect.noSelect(entity.getWorkspaceOid())) {
                params.put("workspaceOid", entity.getWorkspaceOid());
            }
            DefaultResult<List<MdStrategyTheme>> listResult = params.isEmpty()
                    ? this.mdStrategyThemeService.selectList("SORT_NO, THEME_CODE", "ASC")
                    : this.mdStrategyThemeService.selectListByParams(params, "SORT_NO, THEME_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0004Q", check = true)
    @Operation(summary = "MD_PROG007D0004 - findStrategyObjectiveList", description = "Strategy objective list")
    @PostMapping(value = "/findStrategyObjectiveList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdStrategyObjective>>> findStrategyObjectiveList(@RequestBody MdStrategyObjective entity) {
        DefaultControllerJsonResultObj<List<MdStrategyObjective>> result = this.initDefaultJsonResult();
        try {
            Map<String, Object> params = new HashMap<>();
            if (!PleaseSelect.noSelect(entity.getThemeOid())) {
                params.put("themeOid", entity.getThemeOid());
            }
            DefaultResult<List<MdStrategyObjective>> listResult = params.isEmpty()
                    ? this.mdStrategyObjectiveService.selectList("SORT_NO, OBJECTIVE_CODE", "ASC")
                    : this.mdStrategyObjectiveService.selectListByParams(params, "SORT_NO, OBJECTIVE_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0004Q", check = true)
    @Operation(summary = "MD_PROG007D0004 - findKpiList", description = "KPI list")
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

    @ControllerMethodAuthority(programId = "MD_PROG007D0004Q", check = true)
    @Operation(summary = "MD_PROG007D0004 - findCycleList", description = "OKR cycle list")
    @PostMapping(value = "/findCycleList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdOkrCycle>>> findCycleList(@RequestBody MdOkrCycle entity) {
        DefaultControllerJsonResultObj<List<MdOkrCycle>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdOkrCycle>> listResult = this.mdOkrCycleService.selectList("START_DATE", "DESC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0004Q", check = true)
    @Operation(summary = "MD_PROG007D0004 - findOkrObjectiveList", description = "OKR objective list")
    @PostMapping(value = "/findOkrObjectiveList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdOkrObjective>>> findOkrObjectiveList(@RequestBody MdOkrObjective entity) {
        DefaultControllerJsonResultObj<List<MdOkrObjective>> result = this.initDefaultJsonResult();
        try {
            Map<String, Object> params = new HashMap<>();
            if (!PleaseSelect.noSelect(entity.getCycleOid())) {
                params.put("cycleOid", entity.getCycleOid());
            }
            DefaultResult<List<MdOkrObjective>> listResult = params.isEmpty()
                    ? this.mdOkrObjectiveService.selectList("OBJECTIVE_CODE", "ASC")
                    : this.mdOkrObjectiveService.selectListByParams(params, "OBJECTIVE_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0004A", check = true)
    @Operation(summary = "MD_PROG007D0004 - save", description = "Strategy objective link create")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdStrategyObjectiveLink>> doSave(@RequestBody MdStrategyObjectiveLink entity) {
        DefaultControllerJsonResultObj<MdStrategyObjectiveLink> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            DefaultResult<MdStrategyObjectiveLink> saveResult = this.mdStrategyObjectiveLinkService.insert(entity);
            this.setDefaultResponseJsonResult(saveResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0004E", check = true)
    @Operation(summary = "MD_PROG007D0004 - load", description = "Strategy objective link load")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdStrategyObjectiveLink>> doLoad(@RequestBody MdStrategyObjectiveLink entity) {
        DefaultControllerJsonResultObj<MdStrategyObjectiveLink> result = this.initDefaultJsonResult();
        try {
            DefaultResult<MdStrategyObjectiveLink> loadResult = this.mdStrategyObjectiveLinkService.selectByEntityPrimaryKey(entity);
            this.setDefaultResponseJsonResult(loadResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0004E", check = true)
    @Operation(summary = "MD_PROG007D0004 - update", description = "Strategy objective link update")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdStrategyObjectiveLink>> doUpdate(@RequestBody MdStrategyObjectiveLink entity) {
        DefaultControllerJsonResultObj<MdStrategyObjectiveLink> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            DefaultResult<MdStrategyObjectiveLink> updateResult = this.mdStrategyObjectiveLinkService.update(entity);
            this.setDefaultResponseJsonResult(updateResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handlerCheck(DefaultControllerJsonResultObj<MdStrategyObjectiveLink> result, MdStrategyObjectiveLink entity) throws ControllerException, ServiceException {
        CheckControllerFieldHandler<MdStrategyObjectiveLink> chk = CheckControllerFieldHandler.build((DefaultControllerJsonResultObj) result);
        chk.testField("strategyObjectiveOid", PleaseSelect.noSelect(entity.getStrategyObjectiveOid()), "Please select strategy objective.")
           .testField("linkType", PleaseSelect.noSelect(entity.getLinkType()), "Please select link type.")
           .testField("linkType", !this.isValidLinkType(entity.getLinkType()), "Link type only allows KPI or OKR_OBJECTIVE.")
           .testField("linkOid", PleaseSelect.noSelect(entity.getLinkOid()), "Please select link target.")
           .testField("weightValue", entity.getWeightValue() == null, "Please enter weight.")
           .testField("weightValue", entity.getWeightValue() != null && entity.getWeightValue().compareTo(BigDecimal.ZERO) < 0, "Weight cannot be less than zero.")
           .testField("sortNo", entity.getSortNo() == null, "Please enter sort no.")
           .testField("sortNo", entity.getSortNo() != null && entity.getSortNo().intValue() < 0, "Sort no cannot be less than zero.")
           .throwHtmlMessage();
    }

    private boolean isValidLinkType(String linkType) {
        return Strings.CS.equalsAny(linkType, LINK_TYPE_KPI, LINK_TYPE_OKR_OBJECTIVE);
    }
}
