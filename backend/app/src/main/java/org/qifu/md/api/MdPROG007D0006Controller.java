package org.qifu.md.api;

import java.util.List;
import java.util.Map;

import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdStrategyWorkspace;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.logic.IStrategyReportLogicService;
import org.qifu.md.model.StrategyReportQueryRequest;
import org.qifu.md.model.StrategyReportResult;
import org.qifu.md.service.IMdStrategyWorkspaceService;
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

@Tag(name = "MD_PROG007D0006", description = "Strategy / BSC Report")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG007D0006")
public class MdPROG007D0006Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IStrategyReportLogicService strategyReportLogicService;
    private final IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService;
    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;
    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;

    public MdPROG007D0006Controller(IStrategyReportLogicService strategyReportLogicService,
            IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService,
            IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService,
            IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService) {
        super();
        this.strategyReportLogicService = strategyReportLogicService;
        this.mdStrategyWorkspaceService = mdStrategyWorkspaceService;
        this.mdOrgUnitService = mdOrgUnitService;
        this.mdOrgMemberService = mdOrgMemberService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0006Q", check = true)
    @Operation(summary = "MD_PROG007D0006 - generate", description = "Generate Strategy / BSC report and snapshot")
    @PostMapping(value = "/generate", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<StrategyReportResult>> generate(@RequestBody StrategyReportQueryRequest request) {
        DefaultControllerJsonResultObj<StrategyReportResult> result = this.initDefaultJsonResult();
        try {
            DefaultResult<StrategyReportResult> reportResult = this.strategyReportLogicService.generate(request);
            this.setDefaultResponseJsonResult(reportResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG007D0006Q", check = true)
    @Operation(summary = "MD_PROG007D0006 - findWorkspaceList", description = "Strategy workspace option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG007D0006Q", check = true)
    @Operation(summary = "MD_PROG007D0006 - findOrgList", description = "Organization option list")
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

    @ControllerMethodAuthority(programId = "MD_PROG007D0006Q", check = true)
    @Operation(summary = "MD_PROG007D0006 - findMemberList", description = "Member option list")
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
}
