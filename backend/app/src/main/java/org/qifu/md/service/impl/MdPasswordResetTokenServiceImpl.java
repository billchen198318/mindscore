package org.qifu.md.service.impl;

import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdPasswordResetToken;
import org.qifu.md.mapper.MdPasswordResetTokenMapper;
import org.qifu.md.service.IMdPasswordResetTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class MdPasswordResetTokenServiceImpl extends BaseService<MdPasswordResetToken, String>
        implements IMdPasswordResetTokenService<MdPasswordResetToken, String> {

    private final MdPasswordResetTokenMapper mdPasswordResetTokenMapper;

    public MdPasswordResetTokenServiceImpl(MdPasswordResetTokenMapper mdPasswordResetTokenMapper) {
        this.mdPasswordResetTokenMapper = mdPasswordResetTokenMapper;
    }

    @Override
    protected IBaseMapper<MdPasswordResetToken, String> getBaseMapper() {
        return this.mdPasswordResetTokenMapper;
    }
}
