package org.qifu.md.logic.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.message.BaseSystemMessage;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.ServiceAuthority;
import org.qifu.base.model.ServiceMethodAuthority;
import org.qifu.base.model.ServiceMethodType;
import org.qifu.base.model.YesNoKeyProvide;
import org.qifu.core.entity.TbAccount;
import org.qifu.core.entity.TbUserRole;
import org.qifu.core.service.IAccountService;
import org.qifu.core.service.IUserRoleService;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.logic.IMdOrgMemberLogicService;
import org.qifu.md.mapper.MdOrgMemberMapper;
import org.qifu.md.service.IMdOrgMemberService;
import org.qifu.util.SimpleUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@ServiceAuthority(check = true)
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=false)
public class MdOrgMemberLogicServiceImpl implements IMdOrgMemberLogicService {

    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;
    private final IAccountService<TbAccount, String> accountService;
    private final IUserRoleService<TbUserRole, String> userRoleService;
    private final MdOrgMemberMapper mdOrgMemberMapper;
    private final PasswordEncoder passwordEncoder;

    public MdOrgMemberLogicServiceImpl(IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService,
                                       IAccountService<TbAccount, String> accountService,
                                       IUserRoleService<TbUserRole, String> userRoleService,
                                       MdOrgMemberMapper mdOrgMemberMapper,
                                       PasswordEncoder passwordEncoder) {
        this.mdOrgMemberService = mdOrgMemberService;
        this.accountService = accountService;
        this.userRoleService = userRoleService;
        this.mdOrgMemberMapper = mdOrgMemberMapper;
        this.passwordEncoder = passwordEncoder;
    }
    
	@ServiceMethodAuthority(type = {ServiceMethodType.INSERT})
	@Transactional(
			propagation=Propagation.REQUIRED, 
			readOnly=false,
			rollbackFor={RuntimeException.class, IOException.class, Exception.class} )		    
    @Override
    public void createMemberWithAccount(MdOrgMember entity) throws ServiceException {
        // 1. 建立帳號
        TbAccount acc = new TbAccount();
        acc.setAccount(entity.getAccount());
        // 檢查是否存在
        if (this.accountService.countByUK(acc) > 0) {
            throw new ServiceException("帳號已存在: " + entity.getAccount());
        }
        acc.setOid(SimpleUtils.getUUIDStr());
        acc.setPassword(this.passwordEncoder.encode("DefP@ssw0rd123!")); // 建議依實際需求調整
        acc.setOnJob(YesNoKeyProvide.YES);
        acc.setCuserid(entity.getCuserid());
        acc.setCdate(new Date());
        this.accountService.insert(acc);

        // 2. 建立預設角色
        TbUserRole userRole = new TbUserRole();
        userRole.setRole("COMMON01"); // 預設角色
        userRole.setAccount(acc.getAccount());
        userRole.setDescription("Auto-created for org member");
        this.userRoleService.insert(userRole);

        // 3. 建立組織成員
        this.mdOrgMemberService.insert(entity);
        
        
        // 非AD登入, 需要再補發 password 修正 url 部份
        
    }

	@ServiceMethodAuthority(type = {ServiceMethodType.DELETE})
	@Transactional(
			propagation=Propagation.REQUIRED,
			readOnly=false,
			rollbackFor={RuntimeException.class, IOException.class, Exception.class} )
    @Override
    public DefaultResult<Boolean> deleteMemberWithAccount(MdOrgMember entity) throws ServiceException {
        if (entity == null || StringUtils.isBlank(entity.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }

        MdOrgMember member = this.mdOrgMemberService.selectByEntityPrimaryKey(entity).getValueEmptyThrowMessage();
        assertNoBusinessReference(member);

        member.setEnabled(YesNoKeyProvide.NO);
        this.mdOrgMemberService.update(member).getValueEmptyThrowMessage();

        TbAccount account = new TbAccount();
        account.setAccount(member.getAccount());
        account = this.accountService.selectByUniqueKey(account).getValueEmptyThrowMessage();
        account.setOnJob(YesNoKeyProvide.NO);
        this.accountService.update(account).getValueEmptyThrowMessage();

        DefaultResult<Boolean> result = new DefaultResult<>();
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(true);
        result.setMessage(BaseSystemMessage.deleteSuccess());
        return result;
    }

    private void assertNoBusinessReference(MdOrgMember member) throws ServiceException {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("oid", member.getOid());
        paramMap.put("account", member.getAccount());
        List<String> referenceNames = this.mdOrgMemberMapper.findBusinessReferenceNamesByAccount(paramMap);
        if (referenceNames != null && !referenceNames.isEmpty()) {
            throw new ServiceException("成員帳號已有資料使用，不允許刪除：" + String.join(", ", referenceNames));
        }
    }
	
}
