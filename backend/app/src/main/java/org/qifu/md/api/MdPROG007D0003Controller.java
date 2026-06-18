package org.qifu.md.api;

import java.math.BigDecimal;
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
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdStrategyObjective;
import org.qifu.md.entity.MdStrategyTheme;
import org.qifu.md.entity.MdStrategyWorkspace;
import org.qifu.md.service.IMdStrategyObjectiveService;
import org.qifu.md.service.IMdStrategyThemeService;
import org.qifu.md.service.IMdStrategyWorkspaceService;
import org.qifu.util.SimpleUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG007D0003", description = "Strategy Objective")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG007D0003")
public class MdPROG007D0003Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdStrategyObjectiveService<MdStrategyObjective, String> mdStrategyObjectiveService;
    private final IMdStrategyThemeService<MdStrategyTheme, String> mdStrategyThemeService;
    private final IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService;

    public MdPROG007D0003Controller(IMdStrategyObjectiveService<MdStrategyObjective, String> mdStrategyObjectiveService,
            IMdStrategyThemeService<MdStrategyTheme, String> mdStrategyThemeService,
            IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService) {
        super();
        this.mdStrategyObjectiveService = mdStrategyObjectiveService;
        this.mdStrategyThemeService = mdStrategyThemeService;
        this.mdStrategyWorkspaceService = mdStrategyWorkspaceService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0003Q", check = true)
    @Operation(summary = "MD_PROG007D0003 - findPage", description = "Strategy objective query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdStrategyObjective>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdStrategyObjective>> result = this.initResult();
        try {
            QueryResult<List<MdStrategyObjective>> queryResult = this.mdStrategyObjectiveService.findPage(
                    this.queryParameter(searchBody)
                        .fullEquals("themeOid")
                        .fullLink("objectiveCodeLike")
                        .fullLink("objectiveNameLike")
                        .value(),
                    searchBody.getPageOf().orderBy("SORT_NO").sortTypeAsc().orderBy("OBJECTIVE_CODE").sortTypeAsc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0003Q", check = true)
    @Operation(summary = "MD_PROG007D0003 - findWorkspaceList", description = "Strategy workspace list")
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

    @ControllerMethodAuthority(programId = "MD_PROG007D0003Q", check = true)
    @Operation(summary = "MD_PROG007D0003 - findThemeList", description = "Strategy theme list")
    @PostMapping(value = "/findThemeList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdStrategyTheme>>> findThemeList(@RequestBody MdStrategyTheme entity) {
        DefaultControllerJsonResultObj<List<MdStrategyTheme>> result = this.initDefaultJsonResult();
        try {
            Map<String, Object> params = new HashMap<>();
            if (StringUtils.isNotBlank(entity.getWorkspaceOid()) && !PleaseSelect.noSelect(entity.getWorkspaceOid())) {
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

    @ControllerMethodAuthority(programId = "MD_PROG007D0003A", check = true)
    @Operation(summary = "MD_PROG007D0003 - save", description = "Strategy objective create")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdStrategyObjective>> doSave(@RequestBody MdStrategyObjective entity) {
        DefaultControllerJsonResultObj<MdStrategyObjective> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            this.normalize(entity);
            DefaultResult<MdStrategyObjective> saveResult = this.mdStrategyObjectiveService.insert(entity);
            this.setDefaultResponseJsonResult(saveResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0003E", check = true)
    @Operation(summary = "MD_PROG007D0003 - load", description = "Strategy objective load")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdStrategyObjective>> doLoad(@RequestBody MdStrategyObjective entity) {
        DefaultControllerJsonResultObj<MdStrategyObjective> result = this.initDefaultJsonResult();
        try {
            DefaultResult<MdStrategyObjective> loadResult = this.mdStrategyObjectiveService.selectByEntityPrimaryKey(entity);
            this.setDefaultResponseJsonResult(loadResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0003E", check = true)
    @Operation(summary = "MD_PROG007D0003 - update", description = "Strategy objective update")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdStrategyObjective>> doUpdate(@RequestBody MdStrategyObjective entity) {
        DefaultControllerJsonResultObj<MdStrategyObjective> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            this.normalize(entity);
            DefaultResult<MdStrategyObjective> updateResult = this.mdStrategyObjectiveService.update(entity);
            this.setDefaultResponseJsonResult(updateResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handlerCheck(DefaultControllerJsonResultObj<MdStrategyObjective> result, MdStrategyObjective entity) throws ControllerException, ServiceException {
        CheckControllerFieldHandler<MdStrategyObjective> chk = CheckControllerFieldHandler.build((DefaultControllerJsonResultObj) result);
        chk.testField("themeOid", PleaseSelect.noSelect(entity.getThemeOid()), "Please select theme.")
           .testField("objectiveCode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(objectiveCode)", "Please enter objective code.")
           .testField("objectiveCode", StringUtils.isNotBlank(entity.getObjectiveCode()) && !SimpleUtils.checkBeTrueOfAZaz09Id(entity.getObjectiveCode()), "Objective code only allows 0-9, a-z, A-Z, dash, underscore, and dot.")
           .testField("objectiveName", entity, "@org.apache.commons.lang3.StringUtils@isBlank(objectiveName)", "Please enter objective name.")
           .testField("weightValue", entity.getWeightValue() == null, "Please enter weight.")
           .testField("weightValue", entity.getWeightValue() != null && entity.getWeightValue().compareTo(BigDecimal.ZERO) < 0, "Weight cannot be less than zero.")
           .testField("sortNo", entity.getSortNo() == null, "Please enter sort no.")
           .testField("sortNo", entity.getSortNo() != null && entity.getSortNo().intValue() < 0, "Sort no cannot be less than zero.")
           .throwHtmlMessage();
    }

    private void normalize(MdStrategyObjective entity) {
        entity.setObjectiveCode(StringUtils.trimToEmpty(entity.getObjectiveCode()));
        entity.setObjectiveName(StringUtils.trimToEmpty(entity.getObjectiveName()));
        entity.setDescription(StringUtils.trimToNull(entity.getDescription()));
    }
}
