package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdKpiScoreSnapshot;
import org.qifu.md.mapper.MdKpiScoreSnapshotMapper;
import org.qifu.md.service.IMdKpiScoreSnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdKpiScoreSnapshotServiceImpl extends BaseService<MdKpiScoreSnapshot, String> implements IMdKpiScoreSnapshotService<MdKpiScoreSnapshot, String> {

	private MdKpiScoreSnapshotMapper mdKpiScoreSnapshotMapper;
	
	@Autowired
	public MdKpiScoreSnapshotServiceImpl(MdKpiScoreSnapshotMapper mdKpiScoreSnapshotMapper) {
		this.mdKpiScoreSnapshotMapper = mdKpiScoreSnapshotMapper;
	}
	
	@Override
	protected IBaseMapper<MdKpiScoreSnapshot, String> getBaseMapper() {
		return mdKpiScoreSnapshotMapper;
	}

}