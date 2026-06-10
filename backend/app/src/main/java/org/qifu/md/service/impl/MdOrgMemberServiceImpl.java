package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.mapper.MdOrgMemberMapper;
import org.qifu.md.service.IMdOrgMemberService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdOrgMemberServiceImpl extends BaseService<MdOrgMember, String> implements IMdOrgMemberService<MdOrgMember, String> {
	
	private final MdOrgMemberMapper mdOrgMemberMapper;

	public MdOrgMemberServiceImpl(MdOrgMemberMapper mdOrgMemberMapper) {
		super();
		this.mdOrgMemberMapper = mdOrgMemberMapper;
	}

	@Override
	protected IBaseMapper<MdOrgMember, String> getBaseMapper() {
		return this.mdOrgMemberMapper;
	}
}
