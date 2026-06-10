package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdOkrObjectiveOwner;
import org.qifu.md.mapper.MdOkrObjectiveOwnerMapper;
import org.qifu.md.service.IMdOkrObjectiveOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdOkrObjectiveOwnerServiceImpl extends BaseService<MdOkrObjectiveOwner, String> implements IMdOkrObjectiveOwnerService<MdOkrObjectiveOwner, String> {

	private MdOkrObjectiveOwnerMapper mdOkrObjectiveOwnerMapper;
	
	@Autowired
	public MdOkrObjectiveOwnerServiceImpl(MdOkrObjectiveOwnerMapper mdOkrObjectiveOwnerMapper) {
		this.mdOkrObjectiveOwnerMapper = mdOkrObjectiveOwnerMapper;
	}
	
	@Override
	protected IBaseMapper<MdOkrObjectiveOwner, String> getBaseMapper() {
		return mdOkrObjectiveOwnerMapper;
	}

}