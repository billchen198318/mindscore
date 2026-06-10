package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdAggregationMethod;
import org.qifu.md.mapper.MdAggregationMethodMapper;
import org.qifu.md.service.IMdAggregationMethodService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdAggregationMethodServiceImpl extends BaseService<MdAggregationMethod, String> implements IMdAggregationMethodService<MdAggregationMethod, String> {
	
	private final MdAggregationMethodMapper mdAggregationMethodMapper;

	public MdAggregationMethodServiceImpl(MdAggregationMethodMapper mdAggregationMethodMapper) {
		super();
		this.mdAggregationMethodMapper = mdAggregationMethodMapper;
	}

	@Override
	protected IBaseMapper<MdAggregationMethod, String> getBaseMapper() {
		return this.mdAggregationMethodMapper;
	}
}
