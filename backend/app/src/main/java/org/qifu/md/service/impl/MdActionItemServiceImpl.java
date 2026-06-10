package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdActionItem;
import org.qifu.md.mapper.MdActionItemMapper;
import org.qifu.md.service.IMdActionItemService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdActionItemServiceImpl extends BaseService<MdActionItem, String> implements IMdActionItemService<MdActionItem, String> {
	
	private final MdActionItemMapper mdActionItemMapper;

	public MdActionItemServiceImpl(MdActionItemMapper mdActionItemMapper) {
		super();
		this.mdActionItemMapper = mdActionItemMapper;
	}

	@Override
	protected IBaseMapper<MdActionItem, String> getBaseMapper() {
		return this.mdActionItemMapper;
	}
}
