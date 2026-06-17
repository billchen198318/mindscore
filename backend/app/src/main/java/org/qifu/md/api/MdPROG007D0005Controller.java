package org.qifu.md.api;

import java.util.List;

import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.QueryResult;
import org.qifu.base.model.SearchBody;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdStrategySnapshot;
import org.qifu.md.entity.MdStrategyWorkspace;
import org.qifu.md.service.IMdStrategySnapshotService;
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

@Tag(name = "MD_PROG007D0005", description = "Strategy Snapshot")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG007D0005")
public class MdPROG007D0005Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdStrategySnapshotService<MdStrategySnapshot, String> mdStrategySnapshotService;
    private final IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService;

    public MdPROG007D0005Controller(IMdStrategySnapshotService<MdStrategySnapshot, String> mdStrategySnapshotService,
            IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService) {
        super();
        this.mdStrategySnapshotService = mdStrategySnapshotService;
        this.mdStrategyWorkspaceService = mdStrategyWorkspaceService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0005Q", check = true)
    @Operation(summary = "MD_PROG007D0005 - findPage", description = "Strategy snapshot history query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdStrategySnapshot>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdStrategySnapshot>> result = this.initResult();
        try {
            QueryResult<List<MdStrategySnapshot>> queryResult = this.mdStrategySnapshotService.findPage(
                    this.queryParameter(searchBody)
                        .fullEquals("workspaceOid")
                        .fullEquals("periodType")
                        .fullEquals("periodKey")
                        .value(),
                    searchBody.getPageOf().orderBy("SNAPSHOT_AT").sortTypeDesc().orderBy("PERIOD_KEY").sortTypeDesc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0005Q", check = true)
    @Operation(summary = "MD_PROG007D0005 - load", description = "Strategy snapshot evidence detail")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdStrategySnapshot>> doLoad(@RequestBody MdStrategySnapshot entity) {
        DefaultControllerJsonResultObj<MdStrategySnapshot> result = this.initDefaultJsonResult();
        try {
            DefaultResult<MdStrategySnapshot> loadResult = this.mdStrategySnapshotService.selectByEntityPrimaryKey(entity);
            this.setDefaultResponseJsonResult(loadResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0005Q", check = true)
    @Operation(summary = "MD_PROG007D0005 - findWorkspaceList", description = "Strategy workspace list")
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
}
