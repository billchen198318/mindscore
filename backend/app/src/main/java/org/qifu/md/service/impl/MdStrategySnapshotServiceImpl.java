package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdStrategySnapshot;
import org.qifu.md.mapper.MdStrategySnapshotMapper;
import org.qifu.md.service.IMdStrategySnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdStrategySnapshotServiceImpl extends BaseService<MdStrategySnapshot, String> implements IMdStrategySnapshotService<MdStrategySnapshot, String> {

	private MdStrategySnapshotMapper mdStrategySnapshotMapper;
	
	@Autowired
	public MdStrategySnapshotServiceImpl(MdStrategySnapshotMapper mdStrategySnapshotMapper) {
		this.mdStrategySnapshotMapper = mdStrategySnapshotMapper;
	}
	
	@Override
	protected IBaseMapper<MdStrategySnapshot, String> getBaseMapper() {
		return mdStrategySnapshotMapper;
	}

}