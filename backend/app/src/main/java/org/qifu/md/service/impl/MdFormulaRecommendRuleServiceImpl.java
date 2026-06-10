package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdFormulaRecommendRule;
import org.qifu.md.mapper.MdFormulaRecommendRuleMapper;
import org.qifu.md.service.IMdFormulaRecommendRuleService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdFormulaRecommendRuleServiceImpl extends BaseService<MdFormulaRecommendRule, String> implements IMdFormulaRecommendRuleService<MdFormulaRecommendRule, String> {
	
	private final MdFormulaRecommendRuleMapper mdFormulaRecommendRuleMapper;

	public MdFormulaRecommendRuleServiceImpl(MdFormulaRecommendRuleMapper mdFormulaRecommendRuleMapper) {
		super();
		this.mdFormulaRecommendRuleMapper = mdFormulaRecommendRuleMapper;
	}

	@Override
	protected IBaseMapper<MdFormulaRecommendRule, String> getBaseMapper() {
		return this.mdFormulaRecommendRuleMapper;
	}
}
