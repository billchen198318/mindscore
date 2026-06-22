package org.qifu.md.service.impl;
import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdLlmProviderConfig;
import org.qifu.md.mapper.MdLlmProviderConfigMapper;
import org.qifu.md.service.IMdLlmProviderConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdLlmProviderConfigServiceImpl extends BaseService<MdLlmProviderConfig, String> implements IMdLlmProviderConfigService<MdLlmProviderConfig, String> {
    private final MdLlmProviderConfigMapper mapper;
    public MdLlmProviderConfigServiceImpl(MdLlmProviderConfigMapper mapper) { this.mapper = mapper; }
    @Override protected IBaseMapper<MdLlmProviderConfig, String> getBaseMapper() { return mapper; }
}
