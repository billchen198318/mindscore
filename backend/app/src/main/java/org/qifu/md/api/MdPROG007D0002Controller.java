package org.qifu.md.api;

import java.math.BigDecimal;
import java.util.List;

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
import org.qifu.md.entity.MdStrategyTheme;
import org.qifu.md.entity.MdStrategyWorkspace;
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

@Tag(name = "MD_PROG007D0002", description = "Strategy Theme")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG007D0002")
public class MdPROG007D0002Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdStrategyThemeService<MdStrategyTheme, String> mdStrategyThemeService;
    private final IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService;

    public MdPROG007D0002Controller(IMdStrategyThemeService<MdStrategyTheme, String> mdStrategyThemeService,
            IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService) {
        super();
        this.mdStrategyThemeService = mdStrategyThemeService;
        this.mdStrategyWorkspaceService = mdStrategyWorkspaceService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0002Q", check = true)
    @Operation(summary = "MD_PROG007D0002 - findPage", description = "Strategy theme query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdStrategyTheme>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdStrategyTheme>> result = this.initResult();
        try {
            QueryResult<List<MdStrategyTheme>> queryResult = this.mdStrategyThemeService.findPage(
                    this.queryParameter(searchBody)
                        .fullEquals("workspaceOid")
                        .fullLink("themeCodeLike")
                        .fullLink("themeNameLike")
                        .value(),
                    searchBody.getPageOf().orderBy("SORT_NO").sortTypeAsc().orderBy("THEME_CODE").sortTypeAsc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0002Q", check = true)
    @Operation(summary = "MD_PROG007D0002 - findWorkspaceList", description = "Strategy workspace list")
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

    @ControllerMethodAuthority(programId = "MD_PROG007D0002A", check = true)
    @Operation(summary = "MD_PROG007D0002 - save", description = "Strategy theme create")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdStrategyTheme>> doSave(@RequestBody MdStrategyTheme entity) {
        DefaultControllerJsonResultObj<MdStrategyTheme> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            this.normalize(entity);
            DefaultResult<MdStrategyTheme> saveResult = this.mdStrategyThemeService.insert(entity);
            this.setDefaultResponseJsonResult(saveResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0002E", check = true)
    @Operation(summary = "MD_PROG007D0002 - load", description = "Strategy theme load")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdStrategyTheme>> doLoad(@RequestBody MdStrategyTheme entity) {
        DefaultControllerJsonResultObj<MdStrategyTheme> result = this.initDefaultJsonResult();
        try {
            DefaultResult<MdStrategyTheme> loadResult = this.mdStrategyThemeService.selectByEntityPrimaryKey(entity);
            this.setDefaultResponseJsonResult(loadResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0002E", check = true)
    @Operation(summary = "MD_PROG007D0002 - update", description = "Strategy theme update")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdStrategyTheme>> doUpdate(@RequestBody MdStrategyTheme entity) {
        DefaultControllerJsonResultObj<MdStrategyTheme> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            this.normalize(entity);
            DefaultResult<MdStrategyTheme> updateResult = this.mdStrategyThemeService.update(entity);
            this.setDefaultResponseJsonResult(updateResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handlerCheck(DefaultControllerJsonResultObj<MdStrategyTheme> result, MdStrategyTheme entity) throws ControllerException, ServiceException {
        CheckControllerFieldHandler<MdStrategyTheme> chk = CheckControllerFieldHandler.build((DefaultControllerJsonResultObj) result);
        chk.testField("workspaceOid", PleaseSelect.noSelect(entity.getWorkspaceOid()), "Please select workspace.")
           .testField("themeCode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(themeCode)", "Please enter theme code.")
           .testField("themeCode", StringUtils.isNotBlank(entity.getThemeCode()) && !SimpleUtils.checkBeTrueOfAZaz09Id(entity.getThemeCode()), "Theme code only allows 0-9, a-z, A-Z, dash, underscore, and dot.")
           .testField("themeName", entity, "@org.apache.commons.lang3.StringUtils@isBlank(themeName)", "Please enter theme name.")
           .testField("weightValue", entity.getWeightValue() == null, "Please enter weight.")
           .testField("weightValue", entity.getWeightValue() != null && entity.getWeightValue().compareTo(BigDecimal.ZERO) < 0, "Weight cannot be less than zero.")
           .testField("sortNo", entity.getSortNo() == null, "Please enter sort no.")
           .testField("sortNo", entity.getSortNo() != null && entity.getSortNo().intValue() < 0, "Sort no cannot be less than zero.")
           .throwHtmlMessage();
    }

    private void normalize(MdStrategyTheme entity) {
        entity.setThemeCode(StringUtils.trimToEmpty(entity.getThemeCode()));
        entity.setThemeName(StringUtils.trimToEmpty(entity.getThemeName()));
        entity.setDescription(StringUtils.trimToNull(entity.getDescription()));
    }
}
