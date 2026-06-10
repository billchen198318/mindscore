package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.mapper.MdOkrObjectiveMapper;
import org.qifu.md.service.IMdOkrObjectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdOkrObjectiveServiceImpl extends BaseService<MdOkrObjective, String> implements IMdOkrObjectiveService<MdOkrObjective, String> {

	private MdOkrObjectiveMapper mdOkrObjectiveMapper;
	
	@Autowired
	public MdOkrObjectiveServiceImpl(MdOkrObjectiveMapper mdOkrObjectiveMapper) {
		this.mdOkrObjectiveMapper = mdOkrObjectiveMapper;
	}
	
	@Override
	protected IBaseMapper<MdOkrObjective, String> getBaseMapper() {
		return mdOkrObjectiveMapper;
	}

}