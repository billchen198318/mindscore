package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdActionSourceLink;
import org.qifu.md.mapper.MdActionSourceLinkMapper;
import org.qifu.md.service.IMdActionSourceLinkService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdActionSourceLinkServiceImpl extends BaseService<MdActionSourceLink, String> implements IMdActionSourceLinkService<MdActionSourceLink, String> {
	
	private final MdActionSourceLinkMapper mdActionSourceLinkMapper;

	public MdActionSourceLinkServiceImpl(MdActionSourceLinkMapper mdActionSourceLinkMapper) {
		super();
		this.mdActionSourceLinkMapper = mdActionSourceLinkMapper;
	}

	@Override
	protected IBaseMapper<MdActionSourceLink, String> getBaseMapper() {
		return this.mdActionSourceLinkMapper;
	}
}
