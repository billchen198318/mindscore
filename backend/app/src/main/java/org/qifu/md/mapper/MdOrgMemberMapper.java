package org.qifu.md.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.qifu.base.mapper.IBaseMapper;
import org.qifu.md.entity.MdOrgMember;

@Mapper
public interface MdOrgMemberMapper extends IBaseMapper<MdOrgMember, String> {
}
