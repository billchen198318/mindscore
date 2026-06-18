package org.qifu.md.logic;

import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.md.model.ActionReportQuery;
import org.qifu.md.model.ActionReportResult;

public interface IActionReportLogicService {

    DefaultResult<ActionReportResult> report(ActionReportQuery query) throws ServiceException;

}
