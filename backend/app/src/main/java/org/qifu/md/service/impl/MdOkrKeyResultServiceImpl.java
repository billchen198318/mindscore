package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdOkrKeyResult;
import org.qifu.md.mapper.MdOkrKeyResultMapper;
import org.qifu.md.service.IMdOkrKeyResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdOkrKeyResultServiceImpl extends BaseService<MdOkrKeyResult, String> implements IMdOkrKeyResultService<MdOkrKeyResult, String> {

	private MdOkrKeyResultMapper mdOkrKeyResultMapper;
	
	@Autowired
	public MdOkrKeyResultServiceImpl(MdOkrKeyResultMapper mdOkrKeyResultMapper) {
		this.mdOkrKeyResultMapper = mdOkrKeyResultMapper;
	}
	
	@Override
	protected IBaseMapper<MdOkrKeyResult, String> getBaseMapper() {
		return mdOkrKeyResultMapper;
	}

}