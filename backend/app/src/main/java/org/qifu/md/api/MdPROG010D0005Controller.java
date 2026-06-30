package org.qifu.md.api;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.CheckControllerFieldHandler;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.QueryResult;
import org.qifu.base.model.SearchBody;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdInsight;
import org.qifu.md.entity.MdInsightEvidence;
import org.qifu.md.entity.MdInsightRecommendation;
import org.qifu.md.service.IMdInsightEvidenceService;
import org.qifu.md.service.IMdInsightRecommendationService;
import org.qifu.md.service.IMdInsightService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG010D0005", description = "Insight Evidence / Recommendation")
@RestController
@RequestMapping("/api/MD_PROG010D0005")
public class MdPROG010D0005Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_TENANT_OID = "DEFAULT";

    private final IMdInsightService<MdInsight, String> insightService;
    private final IMdInsightEvidenceService<MdInsightEvidence, String> evidenceService;
    private final IMdInsightRecommendationService<MdInsightRecommendation, String> recommendationService;

    public MdPROG010D0005Controller(
            IMdInsightService<MdInsight, String> insightService,
            IMdInsightEvidenceService<MdInsightEvidence, String> evidenceService,
            IMdInsightRecommendationService<MdInsightRecommendation, String> recommendationService) {
        this.insightService = insightService;
        this.evidenceService = evidenceService;
        this.recommendationService = recommendationService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0005Q", check = true)
    @Operation(summary = "Insight query")
    @PostMapping(value = "/findInsightPage", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QueryResult<List<MdInsight>>> findInsightPage(@RequestBody SearchBody body) {
        QueryResult<List<MdInsight>> result = initResult();
        try {
            Map<String, Object> params = queryParameter(body)
                    .fullLink("insightNoLike").fullLink("titleLike")
                    .fullEquals("insightType").fullEquals("severity")
                    .fullEquals("sourceType").fullEquals("status")
                    .fullEquals("ownerAccount").fullEquals("generatedByType")
                    .value();
            QueryResult<List<MdInsight>> query = insightService.findPage(
                    params, body.getPageOf().orderBy("GENERATED_AT").sortTypeDesc());
            setQueryResponseJsonResult(query, result, body.getPageOf());
        } catch (ServiceException | ControllerException e) {
            noSuccessResult(result, e);
        }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0005Q", check = true)
    @Operation(summary = "Load insight")
    @PostMapping(value = "/loadInsight", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<MdInsight>> loadInsight(@RequestBody MdInsight request) {
        DefaultControllerJsonResultObj<MdInsight> result = initDefaultJsonResult();
        try {
            MdInsight key = new MdInsight();
            key.setOid(request == null ? null : request.getOid());
            setDefaultResponseJsonResult(insightService.selectByEntityPrimaryKey(key), result);
        } catch (ServiceException | ControllerException e) {
            exceptionResult(result, e);
        }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0005Q", check = true)
    @Operation(summary = "Insight evidence list")
    @PostMapping(value = "/findEvidenceList", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdInsightEvidence>>> findEvidenceList(
            @RequestBody Map<String, Object> request) {
        DefaultControllerJsonResultObj<List<MdInsightEvidence>> result = initDefaultJsonResult();
        try {
            String insightOid = request == null || request.get("insightOid") == null ? null : String.valueOf(request.get("insightOid"));
            if (StringUtils.isBlank(insightOid)) {
                throw new ServiceException("Insight OID is required");
            }
            setDefaultResponseJsonResult(evidenceService.selectListByParams(
                    Map.of("insightOid", insightOid, "orderBy", "SORT_NO, CDATE", "sortType", "ASC")), result);
        } catch (ServiceException | ControllerException e) {
            exceptionResult(result, e);
        }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0005Q", check = true)
    @Operation(summary = "Insight recommendation query")
    @PostMapping(value = "/findRecommendationPage", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QueryResult<List<MdInsightRecommendation>>> findRecommendationPage(@RequestBody SearchBody body) {
        QueryResult<List<MdInsightRecommendation>> result = initResult();
        try {
            Map<String, Object> params = queryParameter(body)
                    .fullEquals("insightOid").fullEquals("recommendationType")
                    .fullLink("titleLike").fullEquals("status")
                    .fullEquals("acceptedFlag").fullEquals("actionCreatedFlag")
                    .value();
            QueryResult<List<MdInsightRecommendation>> query = recommendationService.findPage(
                    params, body.getPageOf().orderBy("PRIORITY_NO, CDATE").sortTypeAsc());
            setQueryResponseJsonResult(query, result, body.getPageOf());
        } catch (ServiceException | ControllerException e) {
            noSuccessResult(result, e);
        }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0005E", check = true)
    @Operation(summary = "Load recommendation")
    @PostMapping(value = "/loadRecommendation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<MdInsightRecommendation>> loadRecommendation(
            @RequestBody MdInsightRecommendation request) {
        DefaultControllerJsonResultObj<MdInsightRecommendation> result = initDefaultJsonResult();
        try {
            MdInsightRecommendation key = new MdInsightRecommendation();
            key.setOid(request == null ? null : request.getOid());
            setDefaultResponseJsonResult(recommendationService.selectByEntityPrimaryKey(key), result);
        } catch (ServiceException | ControllerException e) {
            exceptionResult(result, e);
        }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0005A", check = true)
    @Operation(summary = "Create recommendation")
    @PostMapping(value = "/saveRecommendation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<MdInsightRecommendation>> saveRecommendation(
            @RequestBody MdInsightRecommendation request) {
        DefaultControllerJsonResultObj<MdInsightRecommendation> result = initDefaultJsonResult();
        try {
            validateRecommendation(result, request, true);
            normalizeRecommendation(request);
            setDefaultResponseJsonResult(recommendationService.insert(request), result);
        } catch (ServiceException | ControllerException e) {
            exceptionResult(result, e);
        }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0005E", check = true)
    @Operation(summary = "Update recommendation")
    @PostMapping(value = "/updateRecommendation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<MdInsightRecommendation>> updateRecommendation(
            @RequestBody MdInsightRecommendation request) {
        DefaultControllerJsonResultObj<MdInsightRecommendation> result = initDefaultJsonResult();
        try {
            validateRecommendation(result, request, false);
            normalizeRecommendation(request);
            setDefaultResponseJsonResult(recommendationService.update(request), result);
        } catch (ServiceException | ControllerException e) {
            exceptionResult(result, e);
        }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0005D", check = true)
    @Operation(summary = "Delete recommendation")
    @PostMapping(value = "/deleteRecommendation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> deleteRecommendation(
            @RequestBody MdInsightRecommendation request) {
        DefaultControllerJsonResultObj<Boolean> result = initDefaultJsonResult();
        try {
            MdInsightRecommendation key = new MdInsightRecommendation();
            key.setOid(request == null ? null : request.getOid());
            result.setValue(recommendationService.delete(key).getValue());
            successResult(result);
        } catch (ServiceException e) {
            exceptionResult(result, e);
        }
        return ResponseEntity.ok(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0005E", check = true)
    @Operation(summary = "Accept recommendation")
    @PostMapping(value = "/acceptRecommendation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<MdInsightRecommendation>> acceptRecommendation(
            @RequestBody MdInsightRecommendation request) {
        return updateRecommendationStatus(request, "ACCEPTED", "Y");
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0005E", check = true)
    @Operation(summary = "Dismiss recommendation")
    @PostMapping(value = "/dismissRecommendation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<MdInsightRecommendation>> dismissRecommendation(
            @RequestBody MdInsightRecommendation request) {
        return updateRecommendationStatus(request, "DISMISSED", "N");
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0005E", check = true)
    @Operation(summary = "Complete recommendation")
    @PostMapping(value = "/completeRecommendation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<MdInsightRecommendation>> completeRecommendation(
            @RequestBody MdInsightRecommendation request) {
        return updateRecommendationStatus(request, "COMPLETED", "Y");
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0005E", check = true)
    @Operation(summary = "Reopen recommendation")
    @PostMapping(value = "/reopenRecommendation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<MdInsightRecommendation>> reopenRecommendation(
            @RequestBody MdInsightRecommendation request) {
        return updateRecommendationStatus(request, "OPEN", "N");
    }

    private ResponseEntity<DefaultControllerJsonResultObj<MdInsightRecommendation>> updateRecommendationStatus(
            MdInsightRecommendation request, String status, String acceptedFlag) {
        DefaultControllerJsonResultObj<MdInsightRecommendation> result = initDefaultJsonResult();
        try {
            if (request == null || StringUtils.isBlank(request.getOid())) {
                throw new ServiceException("Recommendation OID is required");
            }
            MdInsightRecommendation key = new MdInsightRecommendation();
            key.setOid(request.getOid());
            MdInsightRecommendation entity = recommendationService.selectByEntityPrimaryKey(key).getValue();
            if (entity == null) {
                throw new ServiceException("Recommendation not found");
            }
            entity.setStatus(status);
            entity.setAcceptedFlag(acceptedFlag);
            normalizeRecommendation(entity);
            setDefaultResponseJsonResult(recommendationService.update(entity), result);
        } catch (ServiceException | ControllerException e) {
            exceptionResult(result, e);
        }
        return ResponseEntity.ok(result);
    }

    private void validateRecommendation(DefaultControllerJsonResultObj<MdInsightRecommendation> result,
            MdInsightRecommendation request, boolean create) throws ControllerException {
        CheckControllerFieldHandler<MdInsightRecommendation> check = getCheckControllerFieldHandler(result);
        check.testField("oid", !create && (request == null || StringUtils.isBlank(request.getOid())), "OID is required")
             .testField("insightOid", request == null || StringUtils.isBlank(request.getInsightOid()), "Insight OID is required")
             .testField("recommendationType", request == null || StringUtils.isBlank(request.getRecommendationType()), "Recommendation type is required")
             .testField("title", request == null || StringUtils.isBlank(request.getTitle()), "Title is required")
             .throwHtmlMessage();
    }

    private void normalizeRecommendation(MdInsightRecommendation request) {
        request.setTenantOid(StringUtils.defaultIfBlank(request.getTenantOid(), DEFAULT_TENANT_OID));
        request.setRecommendationType(StringUtils.trimToEmpty(request.getRecommendationType()).toUpperCase());
        request.setTitle(StringUtils.trimToEmpty(request.getTitle()));
        request.setStatus(StringUtils.defaultIfBlank(request.getStatus(), "OPEN").toUpperCase());
        request.setAcceptedFlag(StringUtils.defaultIfBlank(request.getAcceptedFlag(), "N").toUpperCase());
        request.setActionCreatedFlag(StringUtils.defaultIfBlank(request.getActionCreatedFlag(), "N").toUpperCase());
        request.setPriorityNo(request.getPriorityNo() == null ? 0 : request.getPriorityNo());
        request.setIsDeleted(request.getIsDeleted() == null ? 0 : request.getIsDeleted());
    }
}
