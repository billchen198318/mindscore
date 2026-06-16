package org.qifu.md.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.CheckControllerFieldHandler;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.PleaseSelect;
import org.qifu.base.model.QueryResult;
import org.qifu.base.model.SearchBody;
import org.qifu.base.model.YesNoKeyProvide;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiScoreColor;
import org.qifu.md.service.IMdKpiScoreColorService;
import org.qifu.md.service.IMdKpiService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG003D0002", description = "KPI Score Color")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG003D0002")
public class MdPROG003D0002Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private static final String SCOPE_GLOBAL = "GLOBAL";
    private static final String SCOPE_KPI = "KPI";
    private static final String COLOR_CUSTOM = "CUSTOM";
    private static final String COLOR_DEFAULT = "DEFAULT";

    private final IMdKpiScoreColorService<MdKpiScoreColor, String> mdKpiScoreColorService;
    private final IMdKpiService<MdKpi, String> mdKpiService;

    public MdPROG003D0002Controller(IMdKpiScoreColorService<MdKpiScoreColor, String> mdKpiScoreColorService,
            IMdKpiService<MdKpi, String> mdKpiService) {
        super();
        this.mdKpiScoreColorService = mdKpiScoreColorService;
        this.mdKpiService = mdKpiService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG003D0002Q", check = true)
    @Operation(summary = "MD_PROG003D0002 - findPage", description = "KPI score color query")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdKpiScoreColor>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdKpiScoreColor>> result = this.initResult();
        try {
            QueryResult<List<MdKpiScoreColor>> queryResult = this.mdKpiScoreColorService.findPage(
                    this.queryParameter(searchBody)
                        .fullEquals("scopeType")
                        .fullEquals("kpiOid")
                        .fullEquals("colorType")
                        .fullLink("colorCodeLike")
                        .fullLink("colorNameLike")
                        .fullEquals("scoreStatus")
                        .fullEquals("enabled")
                        .value(),
                    searchBody.getPageOf().orderBy("SCOPE_TYPE, SCOPE_KEY, SORT_NO, SCORE_MIN").sortTypeAsc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG003D0002Q", check = true)
    @Operation(summary = "MD_PROG003D0002 - findKpiList", description = "KPI option list")
    @PostMapping(value = "/findKpiList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdKpi>>> findKpiList(@RequestBody MdKpi entity) {
        DefaultControllerJsonResultObj<List<MdKpi>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdKpi>> listResult = this.mdKpiService.selectList("KPI_CODE", "ASC");
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG003D0002E", check = true)
    @Operation(summary = "MD_PROG003D0002 - load", description = "KPI score color load")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdKpiScoreColor>> doLoad(@RequestBody MdKpiScoreColor entity) {
        DefaultControllerJsonResultObj<MdKpiScoreColor> result = this.initDefaultJsonResult();
        try {
            DefaultResult<MdKpiScoreColor> loadResult = this.mdKpiScoreColorService.selectByEntityPrimaryKey(entity);
            this.setDefaultResponseJsonResult(loadResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG003D0002A", check = true)
    @Operation(summary = "MD_PROG003D0002 - save", description = "KPI score color create")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdKpiScoreColor>> doSave(@RequestBody MdKpiScoreColor entity) {
        DefaultControllerJsonResultObj<MdKpiScoreColor> result = this.initDefaultJsonResult();
        try {
            MdKpiScoreColor normalized = normalize(entity);
            this.handlerCheck(result, normalized);
            DefaultResult<MdKpiScoreColor> saveResult = this.mdKpiScoreColorService.insert(normalized);
            this.setDefaultResponseJsonResult(saveResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG003D0002E", check = true)
    @Operation(summary = "MD_PROG003D0002 - update", description = "KPI score color update")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdKpiScoreColor>> doUpdate(@RequestBody MdKpiScoreColor entity) {
        DefaultControllerJsonResultObj<MdKpiScoreColor> result = this.initDefaultJsonResult();
        try {
            MdKpiScoreColor normalized = normalize(entity);
            this.handlerCheck(result, normalized);
            DefaultResult<MdKpiScoreColor> updateResult = this.mdKpiScoreColorService.update(normalized);
            this.setDefaultResponseJsonResult(updateResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG003D0002D", check = true)
    @Operation(summary = "MD_PROG003D0002 - delete", description = "KPI score color delete")
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> doDelete(@RequestBody MdKpiScoreColor entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            DefaultResult<Boolean> deleteResult = this.mdKpiScoreColorService.delete(entity);
            this.setDefaultResponseJsonResult(deleteResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    private MdKpiScoreColor normalize(MdKpiScoreColor entity) {
        if (entity == null) {
            return null;
        }
        entity.setScopeType(StringUtils.defaultIfBlank(entity.getScopeType(), SCOPE_GLOBAL).toUpperCase());
        entity.setColorType(StringUtils.defaultIfBlank(entity.getColorType(), COLOR_CUSTOM).toUpperCase());
        entity.setScoreStatus(StringUtils.defaultIfBlank(entity.getScoreStatus(), "UNKNOWN").toUpperCase());
        entity.setEnabled(StringUtils.defaultIfBlank(entity.getEnabled(), YesNoKeyProvide.YES));
        entity.setColorCode(StringUtils.trimToNull(entity.getColorCode()));
        entity.setColorName(StringUtils.trimToNull(entity.getColorName()));
        entity.setFontColor(StringUtils.trimToNull(entity.getFontColor()));
        entity.setBgColor(StringUtils.trimToNull(entity.getBgColor()));
        entity.setDescription(StringUtils.trimToNull(entity.getDescription()));
        entity.setSortNo(entity.getSortNo() == null ? 0 : entity.getSortNo());

        if (SCOPE_GLOBAL.equals(entity.getScopeType())) {
            entity.setScopeKey(SCOPE_GLOBAL);
            entity.setKpiOid(null);
        } else if (SCOPE_KPI.equals(entity.getScopeType()) && !PleaseSelect.noSelect(entity.getKpiOid())) {
            entity.setScopeKey(entity.getKpiOid());
        }
        if (COLOR_DEFAULT.equals(entity.getColorType())) {
            entity.setScoreMin(null);
            entity.setScoreMax(null);
        }
        return entity;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void handlerCheck(DefaultControllerJsonResultObj<MdKpiScoreColor> result, MdKpiScoreColor entity) throws ControllerException, ServiceException {
        if (entity == null) {
            result.getCheckFields().put("colorCode", "Please enter color rule data.");
            throw new ControllerException("Please enter color rule data.");
        }

        CheckControllerFieldHandler<MdKpiScoreColor> chk = CheckControllerFieldHandler.build((DefaultControllerJsonResultObj) result);
        chk.testField("scopeType", !SCOPE_GLOBAL.equals(entity.getScopeType()) && !SCOPE_KPI.equals(entity.getScopeType()), "Please select valid scope type.")
           .testField("kpiOid", SCOPE_KPI.equals(entity.getScopeType()) && PleaseSelect.noSelect(entity.getKpiOid()), "Please select KPI.")
           .testField("colorType", !COLOR_CUSTOM.equals(entity.getColorType()) && !COLOR_DEFAULT.equals(entity.getColorType()), "Please select valid color type.")
           .testField("colorCode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(colorCode)", "Please enter color code.")
           .testField("colorCode", entity, "!@org.qifu.util.SimpleUtils@checkBeTrueOfAZaz09Id(colorCode)", "Color code only allows 0-9, a-z, A-Z, -, _, .")
           .testField("colorName", entity, "@org.apache.commons.lang3.StringUtils@isBlank(colorName)", "Please enter color name.")
           .testField("scoreStatus", !isScoreStatus(entity.getScoreStatus()), "Please select valid score status.")
           .testField("fontColor", entity, "@org.apache.commons.lang3.StringUtils@isBlank(fontColor)", "Please enter font color.")
           .testField("bgColor", entity, "@org.apache.commons.lang3.StringUtils@isBlank(bgColor)", "Please enter background color.")
           .testField("enabled", PleaseSelect.noSelect(entity.getEnabled()), "Please select enabled flag.")
           .throwHtmlMessage();

        if (COLOR_CUSTOM.equals(entity.getColorType())) {
            if (entity.getScoreMin() == null) {
                result.getCheckFields().put("scoreMin", "Please enter score min.");
                throw new ControllerException("Please enter score min.");
            }
            if (entity.getScoreMax() == null) {
                result.getCheckFields().put("scoreMax", "Please enter score max.");
                throw new ControllerException("Please enter score max.");
            }
            if (entity.getScoreMin().compareTo(entity.getScoreMax()) > 0) {
                result.getCheckFields().put("scoreMin", "Score min cannot be greater than score max.");
                throw new ControllerException("Score min cannot be greater than score max.");
            }
            validateScoreRangeOverlap(result, entity);
        }
        validateHexColor(result, "fontColor", entity.getFontColor(), "Please enter valid font color, for example #FFFFFF.");
        validateHexColor(result, "bgColor", entity.getBgColor(), "Please enter valid background color, for example #198754.");
    }

    private void validateScoreRangeOverlap(DefaultControllerJsonResultObj<MdKpiScoreColor> result, MdKpiScoreColor entity) throws ControllerException, ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("scopeType", entity.getScopeType());
        params.put("scopeKey", entity.getScopeKey());
        params.put("colorType", COLOR_CUSTOM);
        DefaultResult<List<MdKpiScoreColor>> listResult = this.mdKpiScoreColorService.selectListByParams(params);
        List<MdKpiScoreColor> rules = listResult.getValue();
        if (rules == null) {
            return;
        }
        for (MdKpiScoreColor rule : rules) {
            if (Strings.CS.equals(entity.getOid(), rule.getOid())) {
                continue;
            }
            if (rule.getScoreMin() == null || rule.getScoreMax() == null) {
                continue;
            }
            boolean overlapped = rule.getScoreMin().compareTo(entity.getScoreMax()) <= 0
                    && rule.getScoreMax().compareTo(entity.getScoreMin()) >= 0;
            if (overlapped) {
                String message = "Score range overlaps with existing rule: " + rule.getColorCode()
                        + " (" + rule.getScoreMin() + " - " + rule.getScoreMax() + ").";
                result.getCheckFields().put("scoreMin", message);
                result.getCheckFields().put("scoreMax", message);
                throw new ControllerException(message);
            }
        }
    }

    private boolean isScoreStatus(String scoreStatus) {
        return "GOOD".equals(scoreStatus) || "WARNING".equals(scoreStatus) || "BAD".equals(scoreStatus) || "UNKNOWN".equals(scoreStatus);
    }

    private void validateHexColor(DefaultControllerJsonResultObj<MdKpiScoreColor> result, String field, String value, String message) throws ControllerException {
        if (StringUtils.isBlank(value) || !value.matches("^#[0-9a-fA-F]{6}$")) {
            result.getCheckFields().put(field, message);
            throw new ControllerException(message);
        }
    }
}
