package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdActionPlan;
import org.qifu.md.mapper.MdActionPlanMapper;
import org.qifu.md.service.IMdActionPlanService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdActionPlanServiceImpl extends BaseService<MdActionPlan, String> implements IMdActionPlanService<MdActionPlan, String> {
	
	private final MdActionPlanMapper mdActionPlanMapper;

	public MdActionPlanServiceImpl(MdActionPlanMapper mdActionPlanMapper) {
		super();
		this.mdActionPlanMapper = mdActionPlanMapper;
	}

	@Override
	protected IBaseMapper<MdActionPlan, String> getBaseMapper() {
		return this.mdActionPlanMapper;
	}
}
