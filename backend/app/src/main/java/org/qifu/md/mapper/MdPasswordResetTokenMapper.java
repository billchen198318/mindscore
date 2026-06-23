package org.qifu.md.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.qifu.base.mapper.IBaseMapper;
import org.qifu.md.entity.MdPasswordResetToken;

@Mapper
public interface MdPasswordResetTokenMapper extends IBaseMapper<MdPasswordResetToken, String> {
    MdPasswordResetToken selectActiveByTokenHash(Map<String, Object> paramMap);
    int revokeActiveTokensByAccount(Map<String, Object> paramMap);
}
