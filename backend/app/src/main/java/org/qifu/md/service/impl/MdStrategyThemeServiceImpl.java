package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdStrategyTheme;
import org.qifu.md.mapper.MdStrategyThemeMapper;
import org.qifu.md.service.IMdStrategyThemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdStrategyThemeServiceImpl extends BaseService<MdStrategyTheme, String> implements IMdStrategyThemeService<MdStrategyTheme, String> {

	private MdStrategyThemeMapper mdStrategyThemeMapper;
	
	@Autowired
	public MdStrategyThemeServiceImpl(MdStrategyThemeMapper mdStrategyThemeMapper) {
		this.mdStrategyThemeMapper = mdStrategyThemeMapper;
	}
	
	@Override
	protected IBaseMapper<MdStrategyTheme, String> getBaseMapper() {
		return mdStrategyThemeMapper;
	}

}