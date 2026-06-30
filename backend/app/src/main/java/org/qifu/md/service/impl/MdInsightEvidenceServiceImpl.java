package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdInsightEvidence;
import org.qifu.md.mapper.MdInsightEvidenceMapper;
import org.qifu.md.service.IMdInsightEvidenceService;
import org.springframework.stereotype.Service;

@Service
public class MdInsightEvidenceServiceImpl extends BaseService<MdInsightEvidence, String> implements IMdInsightEvidenceService<MdInsightEvidence, String> {
    private final MdInsightEvidenceMapper mapper;

    public MdInsightEvidenceServiceImpl(MdInsightEvidenceMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected IBaseMapper<MdInsightEvidence, String> getBaseMapper() {
        return mapper;
    }
}
