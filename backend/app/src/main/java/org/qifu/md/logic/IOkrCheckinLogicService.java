package org.qifu.md.logic;

import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.md.entity.MdOkrCheckin;

public interface IOkrCheckinLogicService {

    DefaultResult<MdOkrCheckin> create(MdOkrCheckin entity) throws ServiceException;

    DefaultResult<Boolean> delete(MdOkrCheckin entity) throws ServiceException;
}
