package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdStrategyObjective;
import org.qifu.md.mapper.MdStrategyObjectiveMapper;
import org.qifu.md.service.IMdStrategyObjectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdStrategyObjectiveServiceImpl extends BaseService<MdStrategyObjective, String> implements IMdStrategyObjectiveService<MdStrategyObjective, String> {

	private MdStrategyObjectiveMapper mdStrategyObjectiveMapper;
	
	@Autowired
	public MdStrategyObjectiveServiceImpl(MdStrategyObjectiveMapper mdStrategyObjectiveMapper) {
		this.mdStrategyObjectiveMapper = mdStrategyObjectiveMapper;
	}
	
	@Override
	protected IBaseMapper<MdStrategyObjective, String> getBaseMapper() {
		return mdStrategyObjectiveMapper;
	}

}