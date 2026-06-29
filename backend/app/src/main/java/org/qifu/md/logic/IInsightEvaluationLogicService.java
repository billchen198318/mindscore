package org.qifu.md.logic;

import java.util.Map;

import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.md.model.InsightEvaluationResult;

public interface IInsightEvaluationLogicService {
    DefaultResult<InsightEvaluationResult> evaluate(Map<String, Object> params) throws ServiceException;
}
