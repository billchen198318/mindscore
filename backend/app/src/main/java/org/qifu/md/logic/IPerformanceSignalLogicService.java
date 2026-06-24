package org.qifu.md.logic;

import java.util.List;
import java.util.Map;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.md.entity.MdPerformanceSignal;
import org.qifu.md.model.PerformanceSignalGenerationResult;

public interface IPerformanceSignalLogicService {
    DefaultResult<PerformanceSignalGenerationResult> generateKpiSignals(Map<String, Object> params) throws ServiceException;
    DefaultResult<List<MdPerformanceSignal>> generateKpiSignalsBySnapshotOid(String snapshotOid) throws ServiceException;
}