package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdFormula;
import org.qifu.md.mapper.MdFormulaMapper;
import org.qifu.md.service.IMdFormulaService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdFormulaServiceImpl extends BaseService<MdFormula, String> implements IMdFormulaService<MdFormula, String> {
	
	private final MdFormulaMapper mdFormulaMapper;

	public MdFormulaServiceImpl(MdFormulaMapper mdFormulaMapper) {
		super();
		this.mdFormulaMapper = mdFormulaMapper;
	}

	@Override
	protected IBaseMapper<MdFormula, String> getBaseMapper() {
		return this.mdFormulaMapper;
	}
}
