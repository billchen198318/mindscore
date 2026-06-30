package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdInsightRecommendation;
import org.qifu.md.mapper.MdInsightRecommendationMapper;
import org.qifu.md.service.IMdInsightRecommendationService;
import org.springframework.stereotype.Service;

@Service
public class MdInsightRecommendationServiceImpl extends BaseService<MdInsightRecommendation, String> implements IMdInsightRecommendationService<MdInsightRecommendation, String> {
    private final MdInsightRecommendationMapper mapper;

    public MdInsightRecommendationServiceImpl(MdInsightRecommendationMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected IBaseMapper<MdInsightRecommendation, String> getBaseMapper() {
        return mapper;
    }
}
