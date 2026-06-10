package org.qifu.md.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.qifu.base.mapper.IBaseMapper;
import org.qifu.md.entity.MdOkrSnapshot;

@Mapper
public interface MdOkrSnapshotMapper extends IBaseMapper<MdOkrSnapshot, String> {
}