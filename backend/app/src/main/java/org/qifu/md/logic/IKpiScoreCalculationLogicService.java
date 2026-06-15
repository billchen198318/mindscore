package org.qifu.md.logic;

import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.md.entity.MdKpiMeasureData;
import org.qifu.md.entity.MdKpiScoreSnapshot;

public interface IKpiScoreCalculationLogicService {

    DefaultResult<MdKpiScoreSnapshot> calculateCurrent(MdKpiMeasureData entity) throws ServiceException;
}
