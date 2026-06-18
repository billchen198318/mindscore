package org.qifu.md.logic;

import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.md.entity.MdActionItem;
import org.qifu.md.model.ActionItemRequest;

public interface IActionItemLogicService {

    DefaultResult<ActionItemRequest> create(ActionItemRequest request) throws ServiceException;

    DefaultResult<ActionItemRequest> load(MdActionItem entity) throws ServiceException;

    DefaultResult<ActionItemRequest> update(ActionItemRequest request) throws ServiceException;

    DefaultResult<Boolean> delete(MdActionItem entity) throws ServiceException;
}
