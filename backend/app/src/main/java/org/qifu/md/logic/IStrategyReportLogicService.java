package org.qifu.md.logic;

import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.md.model.StrategyReportQueryRequest;
import org.qifu.md.model.StrategyReportResult;

public interface IStrategyReportLogicService {

    DefaultResult<StrategyReportResult> generate(StrategyReportQueryRequest request) throws ServiceException;
}
