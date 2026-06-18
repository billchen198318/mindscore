package org.qifu.md.logic;

import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.md.model.ManagementDashboardQuery;
import org.qifu.md.model.ManagementDashboardResult;

public interface IManagementDashboardLogicService {

    DefaultResult<ManagementDashboardResult> dashboard(ManagementDashboardQuery query) throws ServiceException;

}
