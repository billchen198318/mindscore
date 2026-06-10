package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdStrategyObjectiveLink;
import org.qifu.md.mapper.MdStrategyObjectiveLinkMapper;
import org.qifu.md.service.IMdStrategyObjectiveLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdStrategyObjectiveLinkServiceImpl extends BaseService<MdStrategyObjectiveLink, String> implements IMdStrategyObjectiveLinkService<MdStrategyObjectiveLink, String> {

	private MdStrategyObjectiveLinkMapper mdStrategyObjectiveLinkMapper;
	
	@Autowired
	public MdStrategyObjectiveLinkServiceImpl(MdStrategyObjectiveLinkMapper mdStrategyObjectiveLinkMapper) {
		this.mdStrategyObjectiveLinkMapper = mdStrategyObjectiveLinkMapper;
	}
	
	@Override
	protected IBaseMapper<MdStrategyObjectiveLink, String> getBaseMapper() {
		return mdStrategyObjectiveLinkMapper;
	}

}