package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdKpiScoreColor;
import org.qifu.md.mapper.MdKpiScoreColorMapper;
import org.qifu.md.service.IMdKpiScoreColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdKpiScoreColorServiceImpl extends BaseService<MdKpiScoreColor, String> implements IMdKpiScoreColorService<MdKpiScoreColor, String> {

    private MdKpiScoreColorMapper mdKpiScoreColorMapper;

    @Autowired
    public MdKpiScoreColorServiceImpl(MdKpiScoreColorMapper mdKpiScoreColorMapper) {
        this.mdKpiScoreColorMapper = mdKpiScoreColorMapper;
    }

    @Override
    protected IBaseMapper<MdKpiScoreColor, String> getBaseMapper() {
        return mdKpiScoreColorMapper;
    }
}
