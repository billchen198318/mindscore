package org.qifu.md.api;

import java.util.List;
import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.CheckControllerFieldHandler;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.QueryResult;
import org.qifu.base.model.SearchBody;
import org.qifu.core.util.CoreApiSupport;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.service.IMdOrgUnitService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MD_PROG001D0001", description = "組織管理")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG001D0001")
public class MD_PROG001D0001Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;

    public MD_PROG001D0001Controller(IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService) {
        super();
        this.mdOrgUnitService = mdOrgUnitService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG001D0001Q", check = true)
    @Operation(summary = "MD_PROG001D0001 - findPage", description = "查詢組織資料")
    @PostMapping(value = "/findPage", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<QueryResult<List<MdOrgUnit>>> findPage(@RequestBody SearchBody searchBody) {
        QueryResult<List<MdOrgUnit>> result = this.initResult();
        try {
            QueryResult<List<MdOrgUnit>> queryResult = this.mdOrgUnitService.findPage(
                    this.queryParameter(searchBody).fullLink("orgCode").fullLink("orgName").value(),
                    searchBody.getPageOf().orderBy("ORG_CODE").sortTypeAsc());
            this.setQueryResponseJsonResult(queryResult, result, searchBody.getPageOf());
        } catch (ServiceException | ControllerException e) {
            this.noSuccessResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG001D0001Q", check = true)
    @Operation(summary = "MD_PROG001D0001 - findList", description = "查詢組織清單")
    @PostMapping(value = "/findList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdOrgUnit>>> findList(@RequestBody MdOrgUnit entity) {
        DefaultControllerJsonResultObj<List<MdOrgUnit>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdOrgUnit>> listResult = this.mdOrgUnitService.selectList();
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG001D0001C", check = true)
    @Operation(summary = "MD_PROG001D0001 - save", description = "新增組織資料")
    @PostMapping(value = "/save", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdOrgUnit>> doSave(@RequestBody MdOrgUnit entity) {
        DefaultControllerJsonResultObj<MdOrgUnit> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            DefaultResult<MdOrgUnit> cResult = this.mdOrgUnitService.insert(entity);
            this.setDefaultResponseJsonResult(cResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG001D0001E", check = true)
    @Operation(summary = "MD_PROG001D0001 - load", description = "讀取組織資料")
    @PostMapping(value = "/load", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdOrgUnit>> doLoad(@RequestBody MdOrgUnit entity) {
        DefaultControllerJsonResultObj<MdOrgUnit> result = this.initDefaultJsonResult();
        try {
            DefaultResult<MdOrgUnit> lResult = this.mdOrgUnitService.selectByEntityPrimaryKey(entity);
            this.setDefaultResponseJsonResult(lResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG001D0001U", check = true)
    @Operation(summary = "MD_PROG001D0001 - update", description = "更新組織資料")
    @PostMapping(value = "/update", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<MdOrgUnit>> doUpdate(@RequestBody MdOrgUnit entity) {
        DefaultControllerJsonResultObj<MdOrgUnit> result = this.initDefaultJsonResult();
        try {
            this.handlerCheck(result, entity);
            DefaultResult<MdOrgUnit> uResult = this.mdOrgUnitService.update(entity);
            this.setDefaultResponseJsonResult(uResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG001D0001D", check = true)
    @Operation(summary = "MD_PROG001D0001 - delete", description = "刪除組織資料")
    @PostMapping(value = "/delete", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> doDelete(@RequestBody MdOrgUnit entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            DefaultResult<Boolean> delResult = this.mdOrgUnitService.delete(entity);
            this.setDefaultResponseJsonResult(delResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    private void handlerCheck(DefaultControllerJsonResultObj<MdOrgUnit> result, MdOrgUnit entity) throws ControllerException, ServiceException {
        CheckControllerFieldHandler<MdOrgUnit> chk = this.getCheckControllerFieldHandler(result);
        chk.testField("orgCode", entity, "@org.apache.commons.lang3.StringUtils@isBlank(orgCode)", "請輸入組織代碼")
           .testField("orgName", entity, "@org.apache.commons.lang3.StringUtils@isBlank(orgName)", "請輸入組織名稱")
           .throwHtmlMessage();
    }
}
