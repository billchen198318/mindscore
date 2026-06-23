package org.qifu.md.api;

import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.logic.IMdOrgMemberLogicService;
import org.qifu.md.model.PasswordResetRequest;
import org.qifu.md.model.PasswordResetTokenStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseBody
@RequestMapping("/api/auth/passwordReset")
public class PasswordResetController extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdOrgMemberLogicService mdOrgMemberLogicService;

    public PasswordResetController(IMdOrgMemberLogicService mdOrgMemberLogicService) {
        this.mdOrgMemberLogicService = mdOrgMemberLogicService;
    }

    @PostMapping(value = "/validate", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<PasswordResetTokenStatus>> validate(@RequestBody PasswordResetRequest request) {
        DefaultControllerJsonResultObj<PasswordResetTokenStatus> result = this.initDefaultJsonResult();
        try {
            DefaultResult<PasswordResetTokenStatus> validateResult = this.mdOrgMemberLogicService.validatePasswordResetToken(request);
            this.setDefaultResponseJsonResult(validateResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @PostMapping(value = "/complete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> complete(@RequestBody PasswordResetRequest request) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            DefaultResult<Boolean> resetResult = this.mdOrgMemberLogicService.resetPasswordByToken(request);
            this.setDefaultResponseJsonResult(resetResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }
}
