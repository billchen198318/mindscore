package org.qifu.md.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.qifu.base.mapper.IBaseMapper;
import org.qifu.md.entity.MdKpi;

@Mapper
public interface MdKpiMapper extends IBaseMapper<MdKpi, String> {
}