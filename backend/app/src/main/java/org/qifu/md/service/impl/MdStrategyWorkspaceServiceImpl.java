package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdStrategyWorkspace;
import org.qifu.md.mapper.MdStrategyWorkspaceMapper;
import org.qifu.md.service.IMdStrategyWorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdStrategyWorkspaceServiceImpl extends BaseService<MdStrategyWorkspace, String> implements IMdStrategyWorkspaceService<MdStrategyWorkspace, String> {

	private MdStrategyWorkspaceMapper mdStrategyWorkspaceMapper;
	
	@Autowired
	public MdStrategyWorkspaceServiceImpl(MdStrategyWorkspaceMapper mdStrategyWorkspaceMapper) {
		this.mdStrategyWorkspaceMapper = mdStrategyWorkspaceMapper;
	}
	
	@Override
	protected IBaseMapper<MdStrategyWorkspace, String> getBaseMapper() {
		return mdStrategyWorkspaceMapper;
	}

}