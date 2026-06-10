package org.qifu.md.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.qifu.base.mapper.IBaseMapper;
import org.qifu.md.entity.MdKpiScoreSnapshot;

@Mapper
public interface MdKpiScoreSnapshotMapper extends IBaseMapper<MdKpiScoreSnapshot, String> {
}