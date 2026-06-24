package org.qifu.md.api;

import java.util.List;
import java.util.Map;
import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.CheckControllerFieldHandler;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.PleaseSelect;
import org.qifu.base.model.QueryResult;
import org.qifu.base.model.SearchBody;
import org.qifu.base.model.YesNo;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.logic.IMdOrgMemberLogicService;
import org.qifu.md.model.DirectPasswordChangeRequest;
import org.qifu.md.service.IMdOrgMemberService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG001D0002", description = "組織成員管理")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG001D0002")
public class MdPROG001D0002Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;
    
    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;

    private final IMdOrgMemberLogicService mdOrgMemberLogicService;

    public MdPROG001D0002Controller(IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService, IMdOrgMemberLogicService mdOrgMemberLogicService) {
        super();
        this.mdOrgMemberService = mdOrgMemberService;
        this.mdOrgMemberLogicService = mdOrgMemberLogicService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG001D0002Q", check = true)
    @Operation(summary = "MD_PROG001D0002 - findPage", description = "查詢組織成員資料")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdOrgMember>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdOrgMember>> result = this.initResult();
        try {
            Map<String, Object> paramMap = this.queryParameter(searchBody)
                    .fullEquals("orgOid")
                    .fullLink("accountLike")
                    .fullLink("displayNameLike")
                    .fullLink("employeeIdLike")
                    .fullLink("emailLike")
                    .value();
            paramMap.put("enabled", YesNo.YES);
            QueryResult<List<MdOrgMember>> queryResult = this.mdOrgMemberService.findPage(
                    paramMap,
                    searchBody.getPageOf().orderBy("ORG_OID").sortTypeAsc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG001D0002C", check = true)
    @Operation(summary = "MD_PROG001D0002 - save", description = "新增組織成員資料")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdOrgMember>> doSave(@RequestBody MdOrgMember entity) {
        DefaultControllerJsonResultObj<MdOrgMember> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            this.mdOrgMemberLogicService.createMemberWithAccount(entity);
            result.setSuccess(YesNo.YES);
            result.setMessage("新增成功");
            result.setValue(entity);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG001D0002E", check = true)
    @Operation(summary = "MD_PROG001D0002 - load", description = "讀取組織成員資料")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdOrgMember>> doLoad(@RequestBody MdOrgMember entity) {
        DefaultControllerJsonResultObj<MdOrgMember> result = this.initDefaultJsonResult();
        try {
            DefaultResult<MdOrgMember> lResult = this.mdOrgMemberService.selectByEntityPrimaryKey(entity);
            this.setDefaultResponseJsonResult(lResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG001D0002U", check = true)
    @Operation(summary = "MD_PROG001D0002 - update", description = "更新組織成員資料")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdOrgMember>> doUpdate(@RequestBody MdOrgMember entity) {
        DefaultControllerJsonResultObj<MdOrgMember> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            DefaultResult<MdOrgMember> uResult = this.mdOrgMemberService.update(entity);
            this.setDefaultResponseJsonResult(uResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG001D0002D", check = true)
    @Operation(summary = "MD_PROG001D0002 - delete", description = "刪除組織成員資料")
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> doDelete(@RequestBody MdOrgMember entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            DefaultResult<Boolean> delResult = this.mdOrgMemberLogicService.deleteMemberWithAccount(entity);
            this.setDefaultResponseJsonResult(delResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG001D0002U", check = true)
    @Operation(summary = "MD_PROG001D0002 - sendPasswordResetMail", description = "Send password reset mail")
    @PostMapping(value = "/sendPasswordResetMail", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> sendPasswordResetMail(@RequestBody MdOrgMember entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            DefaultResult<Boolean> mailResult = this.mdOrgMemberLogicService.sendPasswordResetMail(entity);
            this.setDefaultResponseJsonResult(mailResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG001D0002U", check = true)
    @Operation(summary = "MD_PROG001D0002 - changePassword", description = "Change member password directly")
    @PostMapping(value = "/changePassword", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> changePassword(
            @RequestBody DirectPasswordChangeRequest request) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            DefaultResult<Boolean> updateResult = this.mdOrgMemberLogicService.changePasswordDirectly(request);
            this.setDefaultResponseJsonResult(updateResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }
    
    private void handlerCheck(DefaultControllerJsonResultObj<MdOrgMember> result, MdOrgMember entity) throws ControllerException, ServiceException {
        CheckControllerFieldHandler<MdOrgMember> chk = this.getCheckControllerFieldHandler(result);
        chk.testField("orgOid", PleaseSelect.noSelect(entity.getOrgOid()), "請選擇組織")
           .testField("account", entity, "@org.apache.commons.lang3.StringUtils@isBlank(account)", "請輸入帳號")
           .testField("displayName", entity, "@org.apache.commons.lang3.StringUtils@isBlank(displayName)", "請輸入名稱")
           .testField("employeeId", entity, "@org.apache.commons.lang3.StringUtils@isBlank(employeeId)", "請輸入員工編號")
           .testField("email", entity, "@org.apache.commons.lang3.StringUtils@isBlank(email)", "請輸入Email")
           .testField("email", entity, "!@org.apache.commons.validator.routines.EmailValidator@getInstance().isValid(email)", "Email格式不正確")
           .throwHtmlMessage();
    }
}
