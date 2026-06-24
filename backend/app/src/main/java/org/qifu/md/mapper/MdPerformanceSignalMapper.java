package org.qifu.md.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.qifu.base.mapper.IBaseMapper;
import org.qifu.md.entity.MdPerformanceSignal;

@Mapper
public interface MdPerformanceSignalMapper extends IBaseMapper<MdPerformanceSignal, String> {
}