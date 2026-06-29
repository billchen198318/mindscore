package org.qifu.md.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.QueryResult;
import org.qifu.base.model.SearchBody;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdInsight;
import org.qifu.md.service.IMdInsightService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG010D0004", description = "Insight Inbox")
@RestController
@RequestMapping("/api/MD_PROG010D0004")
public class MdPROG010D0004Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdInsightService<MdInsight, String> insightService;

    public MdPROG010D0004Controller(IMdInsightService<MdInsight, String> insightService) {
        this.insightService = insightService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0004Q", check = true)
    @Operation(summary = "Insight inbox query")
    @PostMapping(value = "/findPage", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QueryResult<List<MdInsight>>> findPage(@RequestBody SearchBody body) {
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

    @ControllerMethodAuthority(programId = "MD_PROG010D0004Q", check = true)
    @Operation(summary = "Load insight")
    @PostMapping(value = "/load", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<MdInsight>> load(@RequestBody MdInsight request) {
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

    @ControllerMethodAuthority(programId = "MD_PROG010D0004E", check = true)
    @Operation(summary = "Accept insight")
    @PostMapping(value = "/accept", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<MdInsight>> accept(@RequestBody MdInsight request) {
        return updateStatus(request, "ACCEPTED");
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0004E", check = true)
    @Operation(summary = "Dismiss insight")
    @PostMapping(value = "/dismiss", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<MdInsight>> dismiss(@RequestBody MdInsight request) {
        return updateStatus(request, "DISMISSED");
    }

    @ControllerMethodAuthority(programId = "MD_PROG010D0004E", check = true)
    @Operation(summary = "Resolve insight")
    @PostMapping(value = "/resolve", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultControllerJsonResultObj<MdInsight>> resolve(@RequestBody MdInsight request) {
        return updateStatus(request, "RESOLVED");
    }

    private ResponseEntity<DefaultControllerJsonResultObj<MdInsight>> updateStatus(MdInsight request, String status) {
        DefaultControllerJsonResultObj<MdInsight> result = initDefaultJsonResult();
        try {
            if (request == null || StringUtils.isBlank(request.getOid())) {
                throw new ServiceException("Insight OID is required");
            }
            MdInsight key = new MdInsight();
            key.setOid(request.getOid());
            MdInsight entity = insightService.selectByEntityPrimaryKey(key).getValue();
            if (entity == null) {
                throw new ServiceException("Insight not found");
            }
            Date now = new Date();
            entity.setStatus(status);
            if ("ACCEPTED".equals(status)) {
                entity.setAcceptedAt(now);
            } else if ("DISMISSED".equals(status)) {
                entity.setDismissedAt(now);
            } else if ("RESOLVED".equals(status)) {
                entity.setResolvedAt(now);
            }
            setDefaultResponseJsonResult(insightService.update(entity), result);
        } catch (ServiceException | ControllerException e) {
            exceptionResult(result, e);
        }
        return ResponseEntity.ok(result);
    }
}
