package org.qifu.md.api;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.CheckControllerFieldHandler;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.QueryResult;
import org.qifu.base.model.SearchBody;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdLlmRunLog;
import org.qifu.md.logic.ILlmProviderConfigLogicService;
import org.qifu.md.model.LlmConnectionTestResult;
import org.qifu.md.model.LlmProviderConfigRequest;
import org.qifu.md.model.LlmProviderConfigView;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG010D0001", description = "LLM Provider Config / Run Log")
@RestController
@RequestMapping("/api/MD_PROG010D0001")
public class MdPROG010D0001Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;
    private final ILlmProviderConfigLogicService logicService;
    public MdPROG010D0001Controller(ILlmProviderConfigLogicService logicService) { this.logicService = logicService; }

    @ControllerMethodAuthority(programId="MD_PROG010D0001Q", check=true)
    @Operation(summary="Provider query")
    @PostMapping(value="/findProviderPage", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QueryResult<List<LlmProviderConfigView>>> findProviderPage(@RequestBody SearchBody body) {
        QueryResult<List<LlmProviderConfigView>> result = initResult();
        try {
            QueryResult<List<LlmProviderConfigView>> query = logicService.findProviderPage(
                    queryParameter(body).fullLink("providerCodeLike").fullLink("providerNameLike")
                            .fullEquals("providerType").fullEquals("enabledFlag").value(),
                    body.getPageOf().orderBy("PROVIDER_CODE").sortTypeAsc());
            setQueryResponseJsonResult(query, result, body.getPageOf());
        } catch (ServiceException | ControllerException ex) { noSuccessResult(result, ex); }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId="MD_PROG010D0001Q", check=true)
    @Operation(summary="Run log query")
    @PostMapping(value="/findRunLogPage", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QueryResult<List<MdLlmRunLog>>> findRunLogPage(@RequestBody SearchBody body) {
        QueryResult<List<MdLlmRunLog>> result = initResult();
        try {
            QueryResult<List<MdLlmRunLog>> query = logicService.findRunLogPage(
                    queryParameter(body).fullEquals("providerOid").fullEquals("providerType")
                            .fullEquals("requestType").fullEquals("status").value(),
                    body.getPageOf().orderBy("STARTED_AT").sortTypeDesc());
            setQueryResponseJsonResult(query, result, body.getPageOf());
        } catch (ServiceException | ControllerException ex) { noSuccessResult(result, ex); }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId="MD_PROG010D0001E", check=true)
    @PostMapping(value="/load", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<LlmProviderConfigView>> load(@RequestBody LlmProviderConfigRequest request) {
        return executeView(() -> logicService.load(request.oid()));
    }

    @ControllerMethodAuthority(programId="MD_PROG010D0001A", check=true)
    @PostMapping(value="/save", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<LlmProviderConfigView>> save(@RequestBody LlmProviderConfigRequest request) {
        return executeView(request, true, () -> logicService.create(request));
    }

    @ControllerMethodAuthority(programId="MD_PROG010D0001E", check=true)
    @PostMapping(value="/update", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<LlmProviderConfigView>> update(@RequestBody LlmProviderConfigRequest request) {
        return executeView(request, false, () -> logicService.update(request));
    }

    @ControllerMethodAuthority(programId="MD_PROG010D0001D", check=true)
    @PostMapping(value="/delete", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> delete(@RequestBody LlmProviderConfigRequest request) {
        DefaultControllerJsonResultObj<Boolean> result = initDefaultJsonResult();
        try { result.setValue(logicService.delete(request.oid())); successResult(result); }
        catch (ServiceException ex) { exceptionResult(result, ex); }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId="MD_PROG010D0001E", check=true)
    @PostMapping(value="/testConnection", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<LlmConnectionTestResult>> testConnection(@RequestBody LlmProviderConfigRequest request) {
        DefaultControllerJsonResultObj<LlmConnectionTestResult> result = initDefaultJsonResult();
        try { result.setValue(logicService.testConnection(request.oid())); successResult(result); }
        catch (ServiceException ex) { exceptionResult(result, ex); }
        return ResponseEntity.ok(result);
    }

    private ResponseEntity<DefaultControllerJsonResultObj<LlmProviderConfigView>> executeView(ServiceCall call) {
        DefaultControllerJsonResultObj<LlmProviderConfigView> result = initDefaultJsonResult();
        try { result.setValue(call.execute()); successResult(result); }
        catch (ServiceException ex) { exceptionResult(result, ex); }
        return ResponseEntity.ok(result);
    }

    private ResponseEntity<DefaultControllerJsonResultObj<LlmProviderConfigView>> executeView(
            LlmProviderConfigRequest request, boolean requireApiKey, ServiceCall call) {
        DefaultControllerJsonResultObj<LlmProviderConfigView> result = initDefaultJsonResult();
        try {
            validateFields(result, request, requireApiKey);
            result.setValue(call.execute());
            successResult(result);
        } catch (ServiceException | ControllerException ex) {
            exceptionResult(result, ex);
        }
        return ResponseEntity.ok(result);
    }

    private void validateFields(DefaultControllerJsonResultObj<LlmProviderConfigView> result,
            LlmProviderConfigRequest request, boolean requireApiKey) throws ControllerException {
        CheckControllerFieldHandler<LlmProviderConfigView> check = getCheckControllerFieldHandler(result);
        check.testField("providerCode", request == null || StringUtils.isBlank(request.providerCode()), "Provider code is required")
             .testField("providerName", request == null || StringUtils.isBlank(request.providerName()), "Provider name is required")
             .testField("providerType", request == null || StringUtils.isBlank(request.providerType()), "Provider type is required")
             .testField("providerType", request != null && StringUtils.isNotBlank(request.providerType())
                     && !"OPENAI".equalsIgnoreCase(request.providerType()) && !"GEMINI".equalsIgnoreCase(request.providerType()),
                     "Provider type must be OPENAI or GEMINI")
             .testField("apiBaseUrl", request == null || StringUtils.isBlank(request.apiBaseUrl()), "API base URL is required")
             .testField("defaultModel", request == null || StringUtils.isBlank(request.defaultModel()), "Default model is required")
             .testField("apiKey", requireApiKey && (request == null || StringUtils.isBlank(request.apiKey())), "API key is required")
             .testField("enabledFlag", request == null || StringUtils.isBlank(request.enabledFlag()), "Enabled is required")
             .testField("defaultFlag", request == null || StringUtils.isBlank(request.defaultFlag()), "Default is required")
             .throwHtmlMessage();
    }

    @FunctionalInterface private interface ServiceCall { LlmProviderConfigView execute() throws ServiceException; }
}
