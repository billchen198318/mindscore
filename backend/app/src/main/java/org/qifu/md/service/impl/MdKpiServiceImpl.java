package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.mapper.MdKpiMapper;
import org.qifu.md.service.IMdKpiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdKpiServiceImpl extends BaseService<MdKpi, String> implements IMdKpiService<MdKpi, String> {

	private MdKpiMapper mdKpiMapper;
	
	@Autowired
	public MdKpiServiceImpl(MdKpiMapper mdKpiMapper) {
		this.mdKpiMapper = mdKpiMapper;
	}
	
	@Override
	protected IBaseMapper<MdKpi, String> getBaseMapper() {
		return mdKpiMapper;
	}

}