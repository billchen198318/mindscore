package org.qifu.md.logic;

import java.util.Map;

import org.qifu.base.exception.ServiceException;
import org.qifu.md.entity.MdActionItem;
import org.qifu.md.entity.MdInsightRecommendation;

public interface IInsightRecommendationWorkflowService {
    MdInsightRecommendation generateLlmRecommendation(Map<String, Object> request) throws ServiceException;
    MdActionItem createActionFromRecommendation(Map<String, Object> request) throws ServiceException;
}