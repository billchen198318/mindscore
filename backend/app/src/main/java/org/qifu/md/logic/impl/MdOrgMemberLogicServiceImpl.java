package org.qifu.md.logic.impl;

import java.io.IOException;
import java.util.Date;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.ServiceAuthority;
import org.qifu.base.model.ServiceMethodAuthority;
import org.qifu.base.model.ServiceMethodType;
import org.qifu.base.model.YesNo;
import org.qifu.core.entity.TbAccount;
import org.qifu.core.entity.TbUserRole;
import org.qifu.core.service.IAccountService;
import org.qifu.core.service.IUserRoleService;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.logic.IMdOrgMemberLogicService;
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
    private final PasswordEncoder passwordEncoder;

    public MdOrgMemberLogicServiceImpl(IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService,
                                       IAccountService<TbAccount, String> accountService,
                                       IUserRoleService<TbUserRole, String> userRoleService,
                                       PasswordEncoder passwordEncoder) {
        this.mdOrgMemberService = mdOrgMemberService;
        this.accountService = accountService;
        this.userRoleService = userRoleService;
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
        acc.setOnJob(YesNo.YES);
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
	
}
