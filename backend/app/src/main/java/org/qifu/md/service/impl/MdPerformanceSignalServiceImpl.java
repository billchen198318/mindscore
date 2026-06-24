package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdPerformanceSignal;
import org.qifu.md.mapper.MdPerformanceSignalMapper;
import org.qifu.md.service.IMdPerformanceSignalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class MdPerformanceSignalServiceImpl extends BaseService<MdPerformanceSignal, String>
        implements IMdPerformanceSignalService<MdPerformanceSignal, String> {

    private final MdPerformanceSignalMapper mapper;

    public MdPerformanceSignalServiceImpl(MdPerformanceSignalMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected IBaseMapper<MdPerformanceSignal, String> getBaseMapper() {
        return mapper;
    }
}