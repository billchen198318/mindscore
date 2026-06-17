package org.qifu.md.api;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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

@Tag(name = "MD_PROG007D0001", description = "Strategy Workspace")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG007D0001")
public class MdPROG007D0001Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService;
    private final IMdStrategyThemeService<MdStrategyTheme, String> mdStrategyThemeService;

    public MdPROG007D0001Controller(IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService,
            IMdStrategyThemeService<MdStrategyTheme, String> mdStrategyThemeService) {
        super();
        this.mdStrategyWorkspaceService = mdStrategyWorkspaceService;
        this.mdStrategyThemeService = mdStrategyThemeService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0001Q", check = true)
    @Operation(summary = "MD_PROG007D0001 - findPage", description = "Strategy workspace query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdStrategyWorkspace>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdStrategyWorkspace>> result = this.initResult();
        try {
            QueryResult<List<MdStrategyWorkspace>> queryResult = this.mdStrategyWorkspaceService.findPage(
                    this.queryParameter(searchBody)
                        .fullLink("workspaceCodeLike")
                        .fullLink("workspaceNameLike")
                        .fullEquals("status")
                        .value(),
                    searchBody.getPageOf().orderBy("WORKSPACE_CODE").sortTypeAsc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0001Q", check = true)
    @Operation(summary = "MD_PROG007D0001 - findList", description = "Strategy workspace list")
    @PostMapping(value = "/findList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdStrategyWorkspace>>> findList(@RequestBody MdStrategyWorkspace entity) {
        DefaultControllerJsonResultObj<List<MdStrategyWorkspace>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdStrategyWorkspace>> listResult = this.mdStrategyWorkspaceService.selectList("WORKSPACE_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0001A", check = true)
    @Operation(summary = "MD_PROG007D0001 - save", description = "Strategy workspace create")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdStrategyWorkspace>> doSave(@RequestBody MdStrategyWorkspace entity) {
        DefaultControllerJsonResultObj<MdStrategyWorkspace> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            this.normalize(entity);
            DefaultResult<MdStrategyWorkspace> saveResult = this.mdStrategyWorkspaceService.insert(entity);
            this.setDefaultResponseJsonResult(saveResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0001E", check = true)
    @Operation(summary = "MD_PROG007D0001 - load", description = "Strategy workspace load")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdStrategyWorkspace>> doLoad(@RequestBody MdStrategyWorkspace entity) {
        DefaultControllerJsonResultObj<MdStrategyWorkspace> result = this.initDefaultJsonResult();
        try {
            DefaultResult<MdStrategyWorkspace> loadResult = this.mdStrategyWorkspaceService.selectByEntityPrimaryKey(entity);
            this.setDefaultResponseJsonResult(loadResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0001E", check = true)
    @Operation(summary = "MD_PROG007D0001 - update", description = "Strategy workspace update")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdStrategyWorkspace>> doUpdate(@RequestBody MdStrategyWorkspace entity) {
        DefaultControllerJsonResultObj<MdStrategyWorkspace> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            this.normalize(entity);
            DefaultResult<MdStrategyWorkspace> updateResult = this.mdStrategyWorkspaceService.update(entity);
            this.setDefaultResponseJsonResult(updateResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0001D", check = true)
    @Operation(summary = "MD_PROG007D0001 - delete", description = "Strategy workspace delete")
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> doDelete(@RequestBody MdStrategyWorkspace entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            this.checkWorkspaceNotUsed(entity);
            DefaultResult<Boolean> deleteResult = this.mdStrategyWorkspaceService.delete(entity);
            this.setDefaultResponseJsonResult(deleteResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handlerCheck(DefaultControllerJsonResultObj<MdStrategyWorkspace> result, MdStrategyWorkspace entity) throws ControllerException, ServiceException {
        CheckControllerFieldHandler<MdStrategyWorkspace> chk = CheckControllerFieldHandler.build((DefaultControllerJsonResultObj) result);
        chk.testField("workspaceCode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(workspaceCode)", "Please enter workspace code.")
           .testField("workspaceCode", StringUtils.isNotBlank(entity.getWorkspaceCode()) && !SimpleUtils.checkBeTrueOfAZaz09Id(entity.getWorkspaceCode()), "Workspace code only allows 0-9, a-z, A-Z, dash, underscore, and dot.")
           .testField("workspaceName", entity, "@org.apache.commons.lang3.StringUtils@isBlank(workspaceName)", "Please enter workspace name.")
           .testField("status", PleaseSelect.noSelect(entity.getStatus()), "Please select status.")
           .testField("status", !this.isValidStatus(entity.getStatus()), "Status only allows DRAFT, ACTIVE, CLOSED, or ARCHIVED.")
           .throwHtmlMessage();
    }

    private void normalize(MdStrategyWorkspace entity) {
        entity.setVisionText(StringUtils.trimToNull(entity.getVisionText()));
        entity.setMissionText(StringUtils.trimToNull(entity.getMissionText()));
        entity.setDescription(StringUtils.trimToNull(entity.getDescription()));
    }

    private boolean isValidStatus(String status) {
        return Strings.CS.equalsAny(status, "DRAFT", "ACTIVE", "CLOSED", "ARCHIVED");
    }

    private void checkWorkspaceNotUsed(MdStrategyWorkspace entity) throws ServiceException, ControllerException {
        DefaultResult<List<MdStrategyTheme>> themeListResult = this.mdStrategyThemeService.selectListByParams(Map.of("workspaceOid", entity.getOid()));
        List<MdStrategyTheme> themeList = themeListResult.getValue();
        if (themeList != null && !themeList.isEmpty()) {
            throw new ControllerException("This strategy workspace is used by theme and cannot be deleted.");
        }
    }
}
