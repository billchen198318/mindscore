package org.qifu.md.logic;

import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.model.DirectPasswordChangeRequest;
import org.qifu.md.model.PasswordResetRequest;
import org.qifu.md.model.PasswordResetTokenStatus;

public interface IMdOrgMemberLogicService {
    void createMemberWithAccount(MdOrgMember entity) throws ServiceException;
    DefaultResult<Boolean> deleteMemberWithAccount(MdOrgMember entity) throws ServiceException;
    DefaultResult<Boolean> sendPasswordResetMail(MdOrgMember entity) throws ServiceException;
    DefaultResult<Boolean> changePasswordDirectly(DirectPasswordChangeRequest request) throws ServiceException;
    DefaultResult<PasswordResetTokenStatus> validatePasswordResetToken(PasswordResetRequest request) throws ServiceException;
    DefaultResult<Boolean> resetPasswordByToken(PasswordResetRequest request) throws ServiceException;
}
