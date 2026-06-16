package org.qifu.md.logic;

import java.util.List;

import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.md.entity.MdKpiScoreSnapshot;
import org.qifu.md.model.KpiReportQueryRequest;
import org.qifu.md.model.KpiReportScoreView;
import org.qifu.md.model.KpiReportSummary;

public interface IKpiReportLogicService {

    DefaultResult<List<KpiReportScoreView>> enrich(List<MdKpiScoreSnapshot> snapshots) throws ServiceException;

    DefaultResult<List<KpiReportScoreView>> trend(KpiReportQueryRequest request) throws ServiceException;

    DefaultResult<List<KpiReportScoreView>> targetActual(KpiReportQueryRequest request) throws ServiceException;

    DefaultResult<KpiReportSummary> summary(KpiReportQueryRequest request) throws ServiceException;
}
