package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.mapper.MdOrgUnitMapper;
import org.qifu.md.service.IMdOrgUnitService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdOrgUnitServiceImpl extends BaseService<MdOrgUnit, String> implements IMdOrgUnitService<MdOrgUnit, String> {
	
	private final MdOrgUnitMapper mdOrgUnitMapper;

	public MdOrgUnitServiceImpl(MdOrgUnitMapper mdOrgUnitMapper) {
		super();
		this.mdOrgUnitMapper = mdOrgUnitMapper;
	}

	@Override
	protected IBaseMapper<MdOrgUnit, String> getBaseMapper() {
		return this.mdOrgUnitMapper;
	}
}
