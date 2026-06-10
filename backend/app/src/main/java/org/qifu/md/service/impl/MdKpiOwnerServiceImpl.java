package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdKpiOwner;
import org.qifu.md.mapper.MdKpiOwnerMapper;
import org.qifu.md.service.IMdKpiOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdKpiOwnerServiceImpl extends BaseService<MdKpiOwner, String> implements IMdKpiOwnerService<MdKpiOwner, String> {

	private MdKpiOwnerMapper mdKpiOwnerMapper;
	
	@Autowired
	public MdKpiOwnerServiceImpl(MdKpiOwnerMapper mdKpiOwnerMapper) {
		this.mdKpiOwnerMapper = mdKpiOwnerMapper;
	}
	
	@Override
	protected IBaseMapper<MdKpiOwner, String> getBaseMapper() {
		return mdKpiOwnerMapper;
	}

}