package org.qifu.md.logic;

import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.md.entity.MdOrgMember;

public interface IMdOrgMemberLogicService {
    void createMemberWithAccount(MdOrgMember entity) throws ServiceException;
    DefaultResult<Boolean> deleteMemberWithAccount(MdOrgMember entity) throws ServiceException;
}
