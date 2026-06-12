package org.qifu.md.logic;

import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.md.entity.MdKpiMeasureData;

public interface IKpiMeasureDataLogicService {

    DefaultResult<MdKpiMeasureData> loadByKey(MdKpiMeasureData entity) throws ServiceException;

    DefaultResult<MdKpiMeasureData> saveOrUpdate(MdKpiMeasureData entity) throws ServiceException;

    DefaultResult<Boolean> delete(MdKpiMeasureData entity) throws ServiceException;
}
