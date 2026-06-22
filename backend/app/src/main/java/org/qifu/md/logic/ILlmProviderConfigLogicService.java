package org.qifu.md.logic;

import java.util.List;
import java.util.Map;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.PageOf;
import org.qifu.base.model.QueryResult;
import org.qifu.md.entity.MdLlmRunLog;
import org.qifu.md.model.LlmConnectionTestResult;
import org.qifu.md.model.LlmProviderConfigRequest;
import org.qifu.md.model.LlmProviderConfigView;

public interface ILlmProviderConfigLogicService {
    QueryResult<List<LlmProviderConfigView>> findProviderPage(Map<String, Object> params, PageOf pageOf) throws ServiceException;
    QueryResult<List<MdLlmRunLog>> findRunLogPage(Map<String, Object> params, PageOf pageOf) throws ServiceException;
    LlmProviderConfigView load(String oid) throws ServiceException;
    LlmProviderConfigView create(LlmProviderConfigRequest request) throws ServiceException;
    LlmProviderConfigView update(LlmProviderConfigRequest request) throws ServiceException;
    boolean delete(String oid) throws ServiceException;
    LlmConnectionTestResult testConnection(String oid) throws ServiceException;
}
