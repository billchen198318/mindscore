package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdOkrInitiative;
import org.qifu.md.mapper.MdOkrInitiativeMapper;
import org.qifu.md.service.IMdOkrInitiativeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdOkrInitiativeServiceImpl extends BaseService<MdOkrInitiative, String> implements IMdOkrInitiativeService<MdOkrInitiative, String> {

	private MdOkrInitiativeMapper mdOkrInitiativeMapper;
	
	@Autowired
	public MdOkrInitiativeServiceImpl(MdOkrInitiativeMapper mdOkrInitiativeMapper) {
		this.mdOkrInitiativeMapper = mdOkrInitiativeMapper;
	}
	
	@Override
	protected IBaseMapper<MdOkrInitiative, String> getBaseMapper() {
		return mdOkrInitiativeMapper;
	}

}
