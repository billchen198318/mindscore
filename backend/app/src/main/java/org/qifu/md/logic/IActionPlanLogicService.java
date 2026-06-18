package org.qifu.md.logic;

import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.md.entity.MdActionPlan;
import org.qifu.md.model.ActionPlanRequest;

public interface IActionPlanLogicService {

    DefaultResult<ActionPlanRequest> create(ActionPlanRequest request) throws ServiceException;

    DefaultResult<ActionPlanRequest> load(MdActionPlan entity) throws ServiceException;

    DefaultResult<ActionPlanRequest> update(ActionPlanRequest request) throws ServiceException;

    DefaultResult<Boolean> delete(MdActionPlan entity) throws ServiceException;
}
