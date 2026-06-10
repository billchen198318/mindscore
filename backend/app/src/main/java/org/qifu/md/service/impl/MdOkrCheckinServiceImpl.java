package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdOkrCheckin;
import org.qifu.md.mapper.MdOkrCheckinMapper;
import org.qifu.md.service.IMdOkrCheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdOkrCheckinServiceImpl extends BaseService<MdOkrCheckin, String> implements IMdOkrCheckinService<MdOkrCheckin, String> {

	private MdOkrCheckinMapper mdOkrCheckinMapper;
	
	@Autowired
	public MdOkrCheckinServiceImpl(MdOkrCheckinMapper mdOkrCheckinMapper) {
		this.mdOkrCheckinMapper = mdOkrCheckinMapper;
	}
	
	@Override
	protected IBaseMapper<MdOkrCheckin, String> getBaseMapper() {
		return mdOkrCheckinMapper;
	}

}