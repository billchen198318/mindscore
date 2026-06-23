package org.qifu.md.logic.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.message.BaseSystemMessage;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.PleaseSelect;
import org.qifu.base.model.ServiceAuthority;
import org.qifu.base.model.ServiceMethodAuthority;
import org.qifu.base.model.ServiceMethodType;
import org.qifu.base.model.YesNo;
import org.qifu.md.entity.MdOkrInitiative;
import org.qifu.md.entity.MdOkrKeyResult;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.entity.MdOkrObjectiveOwner;
import org.qifu.md.logic.IOkrObjectiveLogicService;
import org.qifu.md.model.OkrObjectiveRequest;
import org.qifu.md.service.IMdOkrInitiativeService;
import org.qifu.md.service.IMdOkrKeyResultService;
import org.qifu.md.service.IMdOkrObjectiveOwnerService;
import org.qifu.md.service.IMdOkrObjectiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@ServiceAuthority(check = true)
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class OkrObjectiveLogicServiceImpl implements IOkrObjectiveLogicService {

    public static final String OWNER_TYPE_ORG = "ORG";
    public static final String OWNER_TYPE_ACCOUNT = "ACCOUNT";
    public static final String OWNER_ROLE_OWNER = "OWNER";
    public static final String OWNER_ROLE_VIEWER = "VIEWER";
    public static final String OWNER_ROLE_APPROVER = "APPROVER";

    private final IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService;
    private final IMdOkrObjectiveOwnerService<MdOkrObjectiveOwner, String> mdOkrObjectiveOwnerService;
    private final IMdOkrKeyResultService<MdOkrKeyResult, String> mdOkrKeyResultService;
    private final IMdOkrInitiativeService<MdOkrInitiative, String> mdOkrInitiativeService;

    public OkrObjectiveLogicServiceImpl(IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService,
            IMdOkrObjectiveOwnerService<MdOkrObjectiveOwner, String> mdOkrObjectiveOwnerService,
            IMdOkrKeyResultService<MdOkrKeyResult, String> mdOkrKeyResultService,
            IMdOkrInitiativeService<MdOkrInitiative, String> mdOkrInitiativeService) {
        this.mdOkrObjectiveService = mdOkrObjectiveService;
        this.mdOkrObjectiveOwnerService = mdOkrObjectiveOwnerService;
        this.mdOkrKeyResultService = mdOkrKeyResultService;
        this.mdOkrInitiativeService = mdOkrInitiativeService;
    }

    @ServiceMethodAuthority(type = ServiceMethodType.INSERT)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<OkrObjectiveRequest> create(OkrObjectiveRequest request) throws ServiceException {
        MdOkrObjective objective = requireObjective(request);
        normalizeObjective(objective);
        DefaultResult<MdOkrObjective> objectiveResult = this.mdOkrObjectiveService.insert(objective);
        MdOkrObjective savedObjective = objectiveResult.getValueEmptyThrowMessage();
        rebuildOwners(savedObjective.getOid(), request.getOwnerList());
        rebuildInitiatives(savedObjective.getOid(), request.getInitiativeList());
        return load(savedObjective);
    }

    @Override
    public DefaultResult<OkrObjectiveRequest> load(MdOkrObjective entity) throws ServiceException {
        if (entity == null || StringUtils.isBlank(entity.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        DefaultResult<MdOkrObjective> objectiveResult = this.mdOkrObjectiveService.selectByEntityPrimaryKey(entity);
        MdOkrObjective objective = objectiveResult.getValueEmptyThrowMessage();

        Map<String, Object> params = new HashMap<>();
        params.put("objectiveOid", objective.getOid());
        DefaultResult<List<MdOkrObjectiveOwner>> ownerResult = this.mdOkrObjectiveOwnerService.selectListByParams(params, "OWNER_TYPE, ACCOUNT, ORG_OID", "ASC");
        DefaultResult<List<MdOkrInitiative>> initiativeResult = this.mdOkrInitiativeService.selectListByParams(params, "SORT_NO, INITIATIVE_CODE", "ASC");

        OkrObjectiveRequest value = new OkrObjectiveRequest();
        value.setObjective(objective);
        value.setOwnerList(ownerResult.getValue());
        value.setInitiativeList(initiativeResult.getValue());

        DefaultResult<OkrObjectiveRequest> result = new DefaultResult<>();
        result.setSuccess(YesNo.YES);
        result.setValue(value);
        return result;
    }

    @ServiceMethodAuthority(type = ServiceMethodType.UPDATE)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<OkrObjectiveRequest> update(OkrObjectiveRequest request) throws ServiceException {
        MdOkrObjective objective = requireObjective(request);
        if (StringUtils.isBlank(objective.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        normalizeObjective(objective);
        this.mdOkrObjectiveService.update(objective).getValueEmptyThrowMessage();
        rebuildOwners(objective.getOid(), request.getOwnerList());
        rebuildInitiatives(objective.getOid(), request.getInitiativeList());
        return load(objective);
    }

    @ServiceMethodAuthority(type = ServiceMethodType.DELETE)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<Boolean> delete(MdOkrObjective entity) throws ServiceException {
        if (entity == null || StringUtils.isBlank(entity.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        assertNoKeyResult(entity.getOid());
        deleteInitiatives(entity.getOid());
        deleteOwners(entity.getOid());
        return this.mdOkrObjectiveService.delete(entity);
    }

    private MdOkrObjective requireObjective(OkrObjectiveRequest request) throws ServiceException {
        if (request == null || request.getObjective() == null) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        return request.getObjective();
    }

    private void normalizeObjective(MdOkrObjective objective) {
        if (PleaseSelect.noSelect(objective.getParentOid())) {
            objective.setParentOid(null);
        }
    }

    private void rebuildOwners(String objectiveOid, List<MdOkrObjectiveOwner> ownerList) throws ServiceException {
        deleteOwners(objectiveOid);
        if (ownerList == null) {
            return;
        }
        for (MdOkrObjectiveOwner owner : ownerList) {
            MdOkrObjectiveOwner normalized = normalizeOwner(objectiveOid, owner);
            if (normalized != null) {
                this.mdOkrObjectiveOwnerService.insert(normalized);
            }
        }
    }

    private void deleteOwners(String objectiveOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("objectiveOid", objectiveOid);
        DefaultResult<List<MdOkrObjectiveOwner>> ownerResult = this.mdOkrObjectiveOwnerService.selectListByParams(params);
        List<MdOkrObjectiveOwner> owners = ownerResult.getValue();
        for (int i = 0; owners != null && i < owners.size(); i++) {
            this.mdOkrObjectiveOwnerService.delete(owners.get(i));
        }
    }

    private void rebuildInitiatives(String objectiveOid, List<MdOkrInitiative> initiativeList) throws ServiceException {
        deleteInitiatives(objectiveOid);
        if (initiativeList == null) {
            return;
        }
        for (MdOkrInitiative initiative : initiativeList) {
            MdOkrInitiative normalized = normalizeInitiative(objectiveOid, initiative);
            if (normalized != null) {
                this.mdOkrInitiativeService.insert(normalized);
            }
        }
    }

    private void deleteInitiatives(String objectiveOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("objectiveOid", objectiveOid);
        DefaultResult<List<MdOkrInitiative>> initiativeResult = this.mdOkrInitiativeService.selectListByParams(params);
        List<MdOkrInitiative> initiatives = initiativeResult.getValue();
        for (int i = 0; initiatives != null && i < initiatives.size(); i++) {
            this.mdOkrInitiativeService.delete(initiatives.get(i));
        }
    }

    private MdOkrInitiative normalizeInitiative(String objectiveOid, MdOkrInitiative initiative) throws ServiceException {
        if (initiative == null || StringUtils.isBlank(initiative.getInitiativeCode()) || StringUtils.isBlank(initiative.getInitiativeName())) {
            return null;
        }
        MdOkrInitiative normalized = new MdOkrInitiative();
        normalized.setObjectiveOid(objectiveOid);
        normalized.setInitiativeCode(StringUtils.trim(initiative.getInitiativeCode()));
        normalized.setInitiativeName(StringUtils.trim(initiative.getInitiativeName()));
        normalized.setContent(StringUtils.trimToNull(initiative.getContent()));
        normalized.setSortNo(initiative.getSortNo() == null ? 0 : initiative.getSortNo());
        normalized.setStatus(StringUtils.defaultIfBlank(initiative.getStatus(), "ACTIVE"));
        if (!Strings.CS.equalsAny(normalized.getStatus(), "DRAFT", "ACTIVE", "CLOSED", "ARCHIVED")) {
            throw new ServiceException("Unsupported OKR initiative status: " + normalized.getStatus());
        }
        return normalized;
    }

    private MdOkrObjectiveOwner normalizeOwner(String objectiveOid, MdOkrObjectiveOwner owner) throws ServiceException {
        if (owner == null || StringUtils.isBlank(owner.getOwnerType())) {
            return null;
        }
        MdOkrObjectiveOwner normalized = new MdOkrObjectiveOwner();
        normalized.setObjectiveOid(objectiveOid);
        normalized.setOwnerRole(normalizeOwnerRole(owner.getOwnerRole()));

        if (OWNER_TYPE_ORG.equals(owner.getOwnerType())) {
            if (PleaseSelect.noSelect(owner.getOrgOid())) {
                return null;
            }
            normalized.setOwnerType(OWNER_TYPE_ORG);
            normalized.setOrgOid(owner.getOrgOid());
            return normalized;
        }
        if (OWNER_TYPE_ACCOUNT.equals(owner.getOwnerType())) {
            if (PleaseSelect.noSelect(owner.getAccount())) {
                return null;
            }
            normalized.setOwnerType(OWNER_TYPE_ACCOUNT);
            normalized.setAccount(owner.getAccount());
            return normalized;
        }
        throw new ServiceException("Unsupported OKR objective owner type: " + owner.getOwnerType());
    }

    private String normalizeOwnerRole(String ownerRole) throws ServiceException {
        if (StringUtils.isBlank(ownerRole)) {
            return OWNER_ROLE_OWNER;
        }
        if (Strings.CS.equalsAny(ownerRole, OWNER_ROLE_OWNER, OWNER_ROLE_VIEWER, OWNER_ROLE_APPROVER)) {
            return ownerRole;
        }
        throw new ServiceException("Unsupported OKR objective owner role: " + ownerRole);
    }

    private void assertNoKeyResult(String objectiveOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("objectiveOid", objectiveOid);
        DefaultResult<List<MdOkrKeyResult>> krResult = this.mdOkrKeyResultService.selectListByParams(params);
        List<MdOkrKeyResult> krList = krResult.getValue();
        if (krList != null && !krList.isEmpty()) {
            throw new ServiceException("This objective has key results and cannot be deleted.");
        }
    }
}
