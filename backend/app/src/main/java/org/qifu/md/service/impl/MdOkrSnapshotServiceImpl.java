package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdOkrSnapshot;
import org.qifu.md.mapper.MdOkrSnapshotMapper;
import org.qifu.md.service.IMdOkrSnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdOkrSnapshotServiceImpl extends BaseService<MdOkrSnapshot, String> implements IMdOkrSnapshotService<MdOkrSnapshot, String> {

	private MdOkrSnapshotMapper mdOkrSnapshotMapper;
	
	@Autowired
	public MdOkrSnapshotServiceImpl(MdOkrSnapshotMapper mdOkrSnapshotMapper) {
		this.mdOkrSnapshotMapper = mdOkrSnapshotMapper;
	}
	
	@Override
	protected IBaseMapper<MdOkrSnapshot, String> getBaseMapper() {
		return mdOkrSnapshotMapper;
	}

}