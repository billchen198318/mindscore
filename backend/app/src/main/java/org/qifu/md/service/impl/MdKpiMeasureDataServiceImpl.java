package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdKpiMeasureData;
import org.qifu.md.mapper.MdKpiMeasureDataMapper;
import org.qifu.md.service.IMdKpiMeasureDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdKpiMeasureDataServiceImpl extends BaseService<MdKpiMeasureData, String> implements IMdKpiMeasureDataService<MdKpiMeasureData, String> {

	private MdKpiMeasureDataMapper mdKpiMeasureDataMapper;
	
	@Autowired
	public MdKpiMeasureDataServiceImpl(MdKpiMeasureDataMapper mdKpiMeasureDataMapper) {
		this.mdKpiMeasureDataMapper = mdKpiMeasureDataMapper;
	}
	
	@Override
	protected IBaseMapper<MdKpiMeasureData, String> getBaseMapper() {
		return mdKpiMeasureDataMapper;
	}

}