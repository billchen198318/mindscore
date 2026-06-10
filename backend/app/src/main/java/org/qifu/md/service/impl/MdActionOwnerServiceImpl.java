package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdActionOwner;
import org.qifu.md.mapper.MdActionOwnerMapper;
import org.qifu.md.service.IMdActionOwnerService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdActionOwnerServiceImpl extends BaseService<MdActionOwner, String> implements IMdActionOwnerService<MdActionOwner, String> {
	
	private final MdActionOwnerMapper mdActionOwnerMapper;

	public MdActionOwnerServiceImpl(MdActionOwnerMapper mdActionOwnerMapper) {
		super();
		this.mdActionOwnerMapper = mdActionOwnerMapper;
	}

	@Override
	protected IBaseMapper<MdActionOwner, String> getBaseMapper() {
		return this.mdActionOwnerMapper;
	}
}
