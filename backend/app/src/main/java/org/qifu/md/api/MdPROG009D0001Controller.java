package org.qifu.md.api;

import java.util.List;

import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdOkrCycle;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.entity.MdStrategyWorkspace;
import org.qifu.md.logic.IManagementDashboardLogicService;
import org.qifu.md.model.ManagementDashboardQuery;
import org.qifu.md.model.ManagementDashboardResult;
import org.qifu.md.service.IMdOkrCycleService;
import org.qifu.md.service.IMdOrgMemberService;
import org.qifu.md.service.IMdOrgUnitService;
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

@Tag(name = "MD_PROG009D0001", description = "Management Dashboard")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG009D0001")
public class MdPROG009D0001Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IManagementDashboardLogicService managementDashboardLogicService;
    private final IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService;
    private final IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService;
    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;
    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;

    public MdPROG009D0001Controller(IManagementDashboardLogicService managementDashboardLogicService,
            IMdOkrCycleService<MdOkrCycle, String> mdOkrCycleService,
            IMdStrategyWorkspaceService<MdStrategyWorkspace, String> mdStrategyWorkspaceService,
            IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService,
            IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService) {
        super();
        this.managementDashboardLogicService = managementDashboardLogicService;
        this.mdOkrCycleService = mdOkrCycleService;
        this.mdStrategyWorkspaceService = mdStrategyWorkspaceService;
        this.mdOrgUnitService = mdOrgUnitService;
        this.mdOrgMemberService = mdOrgMemberService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG009D0001Q", check = true)
    @Operation(summary = "MD_PROG009D0001 - dashboard", description = "Management dashboard summary")
    @PostMapping(value = "/dashboard", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<ManagementDashboardResult>> dashboard(@RequestBody ManagementDashboardQuery query) {
        DefaultControllerJsonResultObj<ManagementDashboardResult> result = this.initDefaultJsonResult();
        try {
            this.setDefaultResponseJsonResult(this.managementDashboardLogicService.dashboard(query), result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG009D0001Q", check = true)
    @Operation(summary = "MD_PROG009D0001 - findCycleList", description = "OKR cycle option list")
    @PostMapping(value = "/findCycleList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdOkrCycle>>> findCycleList(@RequestBody MdOkrCycle entity) {
        DefaultControllerJsonResultObj<List<MdOkrCycle>> result = this.initDefaultJsonResult();
        try {
            this.setDefaultResponseJsonResult(this.mdOkrCycleService.selectList("START_DATE", "DESC"), result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG009D0001Q", check = true)
    @Operation(summary = "MD_PROG009D0001 - findWorkspaceList", description = "Strategy workspace option list")
    @PostMapping(value = "/findWorkspaceList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdStrategyWorkspace>>> findWorkspaceList(@RequestBody MdStrategyWorkspace entity) {
        DefaultControllerJsonResultObj<List<MdStrategyWorkspace>> result = this.initDefaultJsonResult();
        try {
            this.setDefaultResponseJsonResult(this.mdStrategyWorkspaceService.selectList("WORKSPACE_CODE", "ASC"), result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG009D0001Q", check = true)
    @Operation(summary = "MD_PROG009D0001 - findOrgList", description = "Organization option list")
    @PostMapping(value = "/findOrgList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdOrgUnit>>> findOrgList(@RequestBody MdOrgUnit entity) {
        DefaultControllerJsonResultObj<List<MdOrgUnit>> result = this.initDefaultJsonResult();
        try {
            this.setDefaultResponseJsonResult(this.mdOrgUnitService.selectList("ORG_CODE", "ASC"), result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG009D0001Q", check = true)
    @Operation(summary = "MD_PROG009D0001 - findMemberList", description = "Member option list")
    @PostMapping(value = "/findMemberList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdOrgMember>>> findMemberList(@RequestBody MdOrgMember entity) {
        DefaultControllerJsonResultObj<List<MdOrgMember>> result = this.initDefaultJsonResult();
        try {
            this.setDefaultResponseJsonResult(this.mdOrgMemberService.selectList("ACCOUNT", "ASC"), result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }
}
