package org.qifu.md.service.impl;
import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdLlmRunLog;
import org.qifu.md.mapper.MdLlmRunLogMapper;
import org.qifu.md.service.IMdLlmRunLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdLlmRunLogServiceImpl extends BaseService<MdLlmRunLog, String> implements IMdLlmRunLogService<MdLlmRunLog, String> {
    private final MdLlmRunLogMapper mapper;
    public MdLlmRunLogServiceImpl(MdLlmRunLogMapper mapper) { this.mapper = mapper; }
    @Override protected IBaseMapper<MdLlmRunLog, String> getBaseMapper() { return mapper; }
}
