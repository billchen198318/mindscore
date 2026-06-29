package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdInterpretationRule;
import org.qifu.md.mapper.MdInterpretationRuleMapper;
import org.qifu.md.service.IMdInterpretationRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class MdInterpretationRuleServiceImpl extends BaseService<MdInterpretationRule, String>
        implements IMdInterpretationRuleService<MdInterpretationRule, String> {
    private final MdInterpretationRuleMapper mapper;
    public MdInterpretationRuleServiceImpl(MdInterpretationRuleMapper mapper) { this.mapper = mapper; }
    @Override protected IBaseMapper<MdInterpretationRule, String> getBaseMapper() { return mapper; }
}