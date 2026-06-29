package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdInsight;
import org.qifu.md.mapper.MdInsightMapper;
import org.qifu.md.service.IMdInsightService;
import org.springframework.stereotype.Service;

@Service
public class MdInsightServiceImpl extends BaseService<MdInsight, String> implements IMdInsightService<MdInsight, String> {
    private final MdInsightMapper mapper;

    public MdInsightServiceImpl(MdInsightMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected IBaseMapper<MdInsight, String> getBaseMapper() {
        return mapper;
    }
}
