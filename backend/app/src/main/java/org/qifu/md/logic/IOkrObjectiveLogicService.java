package org.qifu.md.logic;

import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.model.OkrObjectiveRequest;

public interface IOkrObjectiveLogicService {

    DefaultResult<OkrObjectiveRequest> create(OkrObjectiveRequest request) throws ServiceException;

    DefaultResult<OkrObjectiveRequest> load(MdOkrObjective entity) throws ServiceException;

    DefaultResult<OkrObjectiveRequest> update(OkrObjectiveRequest request) throws ServiceException;

    DefaultResult<Boolean> delete(MdOkrObjective entity) throws ServiceException;
}
