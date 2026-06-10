package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdOkrCycle;
import org.qifu.md.mapper.MdOkrCycleMapper;
import org.qifu.md.service.IMdOkrCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdOkrCycleServiceImpl extends BaseService<MdOkrCycle, String> implements IMdOkrCycleService<MdOkrCycle, String> {

	private MdOkrCycleMapper mdOkrCycleMapper;
	
	@Autowired
	public MdOkrCycleServiceImpl(MdOkrCycleMapper mdOkrCycleMapper) {
		this.mdOkrCycleMapper = mdOkrCycleMapper;
	}
	
	@Override
	protected IBaseMapper<MdOkrCycle, String> getBaseMapper() {
		return mdOkrCycleMapper;
	}

}