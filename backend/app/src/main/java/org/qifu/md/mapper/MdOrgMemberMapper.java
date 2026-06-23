package org.qifu.md.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.qifu.base.mapper.IBaseMapper;
import org.qifu.md.entity.MdOrgMember;

@Mapper
public interface MdOrgMemberMapper extends IBaseMapper<MdOrgMember, String> {
    List<String> findBusinessReferenceNamesByAccount(Map<String, Object> paramMap);
}
