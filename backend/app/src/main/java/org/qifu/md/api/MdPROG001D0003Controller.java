package org.qifu.md.api;

import java.util.List;
import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
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

@Tag(name = "MD_PROG001D0003", description = "組織架構重組")
@RestController
@ResponseBody
@RequestMapping("/api/MD_PROG001D0003")
public class MdPROG001D0003Controller extends CoreApiSupport {
    private static final long serialVersionUID = 1L;

    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;

    public MdPROG001D0003Controller(IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService) {
        super();
        this.mdOrgUnitService = mdOrgUnitService;
    }

    @ControllerMethodAuthority(programId = "MD_PROG001D0003Q", check = true)
    @Operation(summary = "MD_PROG001D0003 - findTree", description = "查詢組織樹")
    @PostMapping(value = "/findTree", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<List<MdOrgUnit>>> findTree() {
        DefaultControllerJsonResultObj<List<MdOrgUnit>> result = this.initDefaultJsonResult();
        try {
            DefaultResult<List<MdOrgUnit>> listResult = this.mdOrgUnitService.selectList();
            this.setDefaultResponseJsonResult(listResult, result);
        } catch (ServiceException | ControllerException e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }

    @ControllerMethodAuthority(programId = "MD_PROG001D0003U", check = true)
    @Operation(summary = "MD_PROG001D0003 - move", description = "拖拉組織架構")
    @PostMapping(value = "/move", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DefaultControllerJsonResultObj<Boolean>> doMove(@RequestBody MdOrgUnit entity) {
        DefaultControllerJsonResultObj<Boolean> result = this.initDefaultJsonResult();
        try {
            DefaultResult<Boolean> moveResult = this.mdOrgUnitService.move(entity.getOid(), entity.getParentOid());
            this.setDefaultResponseJsonResult(moveResult, result);
        } catch (Exception e) {
            this.exceptionResult(result, e);
        }
        return ResponseEntity.ok().body(result);
    }
}
