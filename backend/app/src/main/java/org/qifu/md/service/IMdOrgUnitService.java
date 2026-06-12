package org.qifu.md.service;

import java.util.List;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.service.IBaseService;
import org.qifu.md.entity.MdOrgUnit;

public interface IMdOrgUnitService<T, E> extends IBaseService<MdOrgUnit, String> {
    List<MdOrgUnit> findTree() throws ServiceException;
    DefaultResult<Boolean> move(String oid, String newParentOid) throws ServiceException;
}
