package org.qifu.md.logic;

import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.model.KpiMasterRequest;

public interface IKpiMasterLogicService {

    DefaultResult<KpiMasterRequest> create(KpiMasterRequest request) throws ServiceException;

    DefaultResult<KpiMasterRequest> load(MdKpi entity) throws ServiceException;

    DefaultResult<KpiMasterRequest> update(KpiMasterRequest request) throws ServiceException;

    DefaultResult<Boolean> delete(MdKpi entity) throws ServiceException;
}
