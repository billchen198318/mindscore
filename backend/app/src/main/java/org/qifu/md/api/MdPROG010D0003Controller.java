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
import org.qifu.md.entity.MdInterpretationRule;
import org.qifu.md.service.IMdInterpretationRuleService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG010D0003", description = "Interpretation Rule")
@RestController
@RequestMapping("/api/MD_PROG010D0003")
public class MdPROG010D0003Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_TENANT_OID = "DEFAULT";

    private final IMdInterpretationRuleService<MdInterpretationRule, String> ruleService;

    public MdPROG010D0003Controller(IMdInterpretationRuleService<MdInterpretationRule, String> ruleService) {
        this.ruleService = ruleService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0003Q", check = true)
    @Operation(summary = "Interpretation rule query")
    @PostMapping(value = "/findPage", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QueryResult<List<MdInterpretationRule>>> findPage(@RequestBody SearchBody body) {
        QueryResult<List<MdInterpretationRule>> result = initResult();
        try {
            QueryResult<List<MdInterpretationRule>> query = ruleService.findPage(
                    queryParameter(body).fullLink("ruleCodeLike").fullLink("ruleNameLike")
                            .fullEquals("ruleType").fullEquals("sourceType")
                            .fullEquals("severity").fullEquals("enabledFlag").value(),
                    body.getPageOf().orderBy("PRIORITY_NO, RULE_CODE").sortTypeAsc());
            setQueryResponseJsonResult(query, result, body.getPageOf());
        } catch (ServiceException | ControllerException e) {
            noSuccessResult(result, e);
        }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0003E", check = true)
    @Operation(summary = "Load interpretation rule")
    @PostMapping(value = "/load", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<MdInterpretationRule>> load(@RequestBody MdInterpretationRule request) {
        DefaultControllerJsonResultObj<MdInterpretationRule> result = initDefaultJsonResult();
        try {
            MdInterpretationRule key = new MdInterpretationRule();
            key.setOid(request == null ? null : request.getOid());
            setDefaultResponseJsonResult(ruleService.selectByEntityPrimaryKey(key), result);
        } catch (ServiceException | ControllerException e) {
            exceptionResult(result, e);
        }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0003A", check = true)
    @Operation(summary = "Create interpretation rule")
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<MdInterpretationRule>> save(@RequestBody MdInterpretationRule request) {
        DefaultControllerJsonResultObj<MdInterpretationRule> result = initDefaultJsonResult();
        try {
            validateFields(result, request, true);
            normalize(request);
            setDefaultResponseJsonResult(ruleService.insert(request), result);
        } catch (ServiceException | ControllerException e) {
            exceptionResult(result, e);
        }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0003E", check = true)
    @Operation(summary = "Update interpretation rule")
    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<MdInterpretationRule>> update(@RequestBody MdInterpretationRule request) {
        DefaultControllerJsonResultObj<MdInterpretationRule> result = initDefaultJsonResult();
        try {
            validateFields(result, request, false);
            normalize(request);
            setDefaultResponseJsonResult(ruleService.update(request), result);
        } catch (ServiceException | ControllerException e) {
            exceptionResult(result, e);
        }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0003D", check = true)
    @Operation(summary = "Delete interpretation rule")
    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> delete(@RequestBody MdInterpretationRule request) {
        DefaultControllerJsonResultObj<Boolean> result = initDefaultJsonResult();
        try {
            MdInterpretationRule key = new MdInterpretationRule();
            key.setOid(request == null ? null : request.getOid());
            result.setValue(ruleService.delete(key).getValue());
            successResult(result);
        } catch (ServiceException e) {
            exceptionResult(result, e);
        }
        return ResponseEntity.ok(result);
    }

    private void validateFields(DefaultControllerJsonResultObj<MdInterpretationRule> result,
            MdInterpretationRule request, boolean create) throws ControllerException {
        CheckControllerFieldHandler<MdInterpretationRule> check = getCheckControllerFieldHandler(result);
        check.testField("oid", !create && (request == null || StringUtils.isBlank(request.getOid())), "OID is required")
             .testField("ruleCode", request == null || StringUtils.isBlank(request.getRuleCode()), "Rule code is required")
             .testField("ruleName", request == null || StringUtils.isBlank(request.getRuleName()), "Rule name is required")
             .testField("ruleType", request == null || StringUtils.isBlank(request.getRuleType()), "Rule type is required")
             .testField("sourceType", request == null || StringUtils.isBlank(request.getSourceType()), "Source type is required")
             .testField("conditionExpr", request == null || StringUtils.isBlank(request.getConditionExpr()), "Condition expression is required")
             .testField("severity", request == null || StringUtils.isBlank(request.getSeverity()), "Severity is required")
             .testField("enabledFlag", request == null || StringUtils.isBlank(request.getEnabledFlag()), "Enabled is required")
             .throwHtmlMessage();
    }

    private void normalize(MdInterpretationRule request) {
        request.setTenantOid(StringUtils.defaultIfBlank(request.getTenantOid(), DEFAULT_TENANT_OID));
        request.setRuleCode(StringUtils.trimToEmpty(request.getRuleCode()).toUpperCase());
        request.setRuleType(StringUtils.trimToEmpty(request.getRuleType()).toUpperCase());
        request.setSourceType(StringUtils.trimToEmpty(request.getSourceType()).toUpperCase());
        request.setSeverity(StringUtils.defaultIfBlank(request.getSeverity(), "MEDIUM").toUpperCase());
        request.setEnabledFlag(StringUtils.defaultIfBlank(request.getEnabledFlag(), "Y").toUpperCase());
        request.setPriorityNo(request.getPriorityNo() == null ? 0 : request.getPriorityNo());
        request.setIsDeleted(request.getIsDeleted() == null ? 0 : request.getIsDeleted());
    }
}