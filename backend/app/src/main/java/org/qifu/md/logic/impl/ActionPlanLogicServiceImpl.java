package org.qifu.md.logic.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.message.BaseSystemMessage;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.PleaseSelect;
import org.qifu.base.model.ServiceMethodAuthority;
import org.qifu.base.model.ServiceMethodType;
import org.qifu.base.model.YesNo;
import org.qifu.md.entity.MdActionItem;
import org.qifu.md.entity.MdActionOwner;
import org.qifu.md.entity.MdActionPlan;
import org.qifu.md.entity.MdActionSourceLink;
import org.qifu.md.logic.IActionPlanLogicService;
import org.qifu.md.model.ActionPlanRequest;
import org.qifu.md.service.IMdActionItemService;
import org.qifu.md.service.IMdActionOwnerService;
import org.qifu.md.service.IMdActionPlanService;
import org.qifu.md.service.IMdActionSourceLinkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class ActionPlanLogicServiceImpl implements IActionPlanLogicService {

    public static final String ACTION_TYPE_PLAN = "PLAN";
    public static final String OWNER_TYPE_ORG = "ORG";
    public static final String OWNER_TYPE_ACCOUNT = "ACCOUNT";
    public static final String OWNER_ROLE_OWNER = "OWNER";
    public static final String OWNER_ROLE_VIEWER = "VIEWER";
    public static final String OWNER_ROLE_APPROVER = "APPROVER";

    private final IMdActionPlanService<MdActionPlan, String> mdActionPlanService;
    private final IMdActionOwnerService<MdActionOwner, String> mdActionOwnerService;
    private final IMdActionSourceLinkService<MdActionSourceLink, String> mdActionSourceLinkService;
    private final IMdActionItemService<MdActionItem, String> mdActionItemService;

    public ActionPlanLogicServiceImpl(IMdActionPlanService<MdActionPlan, String> mdActionPlanService,
            IMdActionOwnerService<MdActionOwner, String> mdActionOwnerService,
            IMdActionSourceLinkService<MdActionSourceLink, String> mdActionSourceLinkService,
            IMdActionItemService<MdActionItem, String> mdActionItemService) {
        this.mdActionPlanService = mdActionPlanService;
        this.mdActionOwnerService = mdActionOwnerService;
        this.mdActionSourceLinkService = mdActionSourceLinkService;
        this.mdActionItemService = mdActionItemService;
    }

    @ServiceMethodAuthority(type = ServiceMethodType.INSERT)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<ActionPlanRequest> create(ActionPlanRequest request) throws ServiceException {
        MdActionPlan plan = requirePlan(request);
        normalizePlan(plan);
        DefaultResult<MdActionPlan> planResult = this.mdActionPlanService.insert(plan);
        MdActionPlan savedPlan = planResult.getValueEmptyThrowMessage();
        rebuildOwners(savedPlan.getOid(), request.getOwnerList());
        rebuildSourceLinks(savedPlan.getOid(), request.getSourceLinkList());
        return load(savedPlan);
    }

    @Override
    public DefaultResult<ActionPlanRequest> load(MdActionPlan entity) throws ServiceException {
        if (entity == null || StringUtils.isBlank(entity.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        DefaultResult<MdActionPlan> planResult = this.mdActionPlanService.selectByEntityPrimaryKey(entity);
        MdActionPlan plan = planResult.getValueEmptyThrowMessage();

        Map<String, Object> params = new HashMap<>();
        params.put("actionType", ACTION_TYPE_PLAN);
        params.put("actionOid", plan.getOid());
        DefaultResult<List<MdActionOwner>> ownerResult = this.mdActionOwnerService.selectListByParams(params, "OWNER_TYPE, ACCOUNT, ORG_OID", "ASC");
        DefaultResult<List<MdActionSourceLink>> sourceLinkResult = this.mdActionSourceLinkService.selectListByParams(params, "SOURCE_TYPE, SOURCE_OID", "ASC");

        ActionPlanRequest value = new ActionPlanRequest();
        value.setActionPlan(plan);
        value.setOwnerList(ownerResult.getValue());
        value.setSourceLinkList(sourceLinkResult.getValue());

        DefaultResult<ActionPlanRequest> result = new DefaultResult<>();
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
    public DefaultResult<ActionPlanRequest> update(ActionPlanRequest request) throws ServiceException {
        MdActionPlan plan = requirePlan(request);
        if (StringUtils.isBlank(plan.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        normalizePlan(plan);
        this.mdActionPlanService.update(plan).getValueEmptyThrowMessage();
        rebuildOwners(plan.getOid(), request.getOwnerList());
        rebuildSourceLinks(plan.getOid(), request.getSourceLinkList());
        return load(plan);
    }

    @ServiceMethodAuthority(type = ServiceMethodType.DELETE)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<Boolean> delete(MdActionPlan entity) throws ServiceException {
        if (entity == null || StringUtils.isBlank(entity.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        assertNoActionItem(entity.getOid());
        deleteSourceLinks(entity.getOid());
        deleteOwners(entity.getOid());
        return this.mdActionPlanService.delete(entity);
    }

    private MdActionPlan requirePlan(ActionPlanRequest request) throws ServiceException {
        if (request == null || request.getActionPlan() == null) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        return request.getActionPlan();
    }

    private void normalizePlan(MdActionPlan plan) throws ServiceException {
        plan.setPlanCode(StringUtils.trim(plan.getPlanCode()));
        plan.setPlanName(StringUtils.trim(plan.getPlanName()));
        plan.setDescription(StringUtils.trimToNull(plan.getDescription()));
        plan.setStatus(StringUtils.defaultIfBlank(plan.getStatus(), "ACTIVE"));
        if (plan.getProgressValue() == null) {
            plan.setProgressValue(BigDecimal.ZERO);
        }
        if (!StringUtils.equalsAny(plan.getStatus(), "DRAFT", "ACTIVE", "CLOSED", "ARCHIVED")) {
            throw new ServiceException("Unsupported action plan status: " + plan.getStatus());
        }
        if (plan.getStartDate() != null && plan.getEndDate() != null && plan.getEndDate().before(plan.getStartDate())) {
            throw new ServiceException("Action plan end date cannot be earlier than start date.");
        }
    }

    private void rebuildOwners(String planOid, List<MdActionOwner> ownerList) throws ServiceException {
        deleteOwners(planOid);
        if (ownerList == null) {
            return;
        }
        for (MdActionOwner owner : ownerList) {
            MdActionOwner normalized = normalizeOwner(planOid, owner);
            if (normalized != null) {
                this.mdActionOwnerService.insert(normalized);
            }
        }
    }

    private void deleteOwners(String planOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("actionType", ACTION_TYPE_PLAN);
        params.put("actionOid", planOid);
        DefaultResult<List<MdActionOwner>> ownerResult = this.mdActionOwnerService.selectListByParams(params);
        List<MdActionOwner> owners = ownerResult.getValue();
        for (int i = 0; owners != null && i < owners.size(); i++) {
            this.mdActionOwnerService.delete(owners.get(i));
        }
    }

    private MdActionOwner normalizeOwner(String planOid, MdActionOwner owner) throws ServiceException {
        if (owner == null || StringUtils.isBlank(owner.getOwnerType())) {
            return null;
        }
        MdActionOwner normalized = new MdActionOwner();
        normalized.setActionType(ACTION_TYPE_PLAN);
        normalized.setActionOid(planOid);
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
        throw new ServiceException("Unsupported action owner type: " + owner.getOwnerType());
    }

    private String normalizeOwnerRole(String ownerRole) throws ServiceException {
        if (StringUtils.isBlank(ownerRole)) {
            return OWNER_ROLE_OWNER;
        }
        if (StringUtils.equalsAny(ownerRole, OWNER_ROLE_OWNER, OWNER_ROLE_VIEWER, OWNER_ROLE_APPROVER)) {
            return ownerRole;
        }
        throw new ServiceException("Unsupported action owner role: " + ownerRole);
    }

    private void rebuildSourceLinks(String planOid, List<MdActionSourceLink> sourceLinkList) throws ServiceException {
        deleteSourceLinks(planOid);
        if (sourceLinkList == null) {
            return;
        }
        for (MdActionSourceLink sourceLink : sourceLinkList) {
            MdActionSourceLink normalized = normalizeSourceLink(planOid, sourceLink);
            if (normalized != null) {
                this.mdActionSourceLinkService.insert(normalized);
            }
        }
    }

    private void deleteSourceLinks(String planOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("actionType", ACTION_TYPE_PLAN);
        params.put("actionOid", planOid);
        DefaultResult<List<MdActionSourceLink>> sourceLinkResult = this.mdActionSourceLinkService.selectListByParams(params);
        List<MdActionSourceLink> sourceLinks = sourceLinkResult.getValue();
        for (int i = 0; sourceLinks != null && i < sourceLinks.size(); i++) {
            this.mdActionSourceLinkService.delete(sourceLinks.get(i));
        }
    }

    private MdActionSourceLink normalizeSourceLink(String planOid, MdActionSourceLink sourceLink) throws ServiceException {
        if (sourceLink == null || StringUtils.isBlank(sourceLink.getSourceType()) || PleaseSelect.noSelect(sourceLink.getSourceOid())) {
            return null;
        }
        MdActionSourceLink normalized = new MdActionSourceLink();
        normalized.setActionType(ACTION_TYPE_PLAN);
        normalized.setActionOid(planOid);
        normalized.setSourceType(StringUtils.trim(sourceLink.getSourceType()));
        normalized.setSourceOid(StringUtils.trim(sourceLink.getSourceOid()));
        normalized.setLinkReason(StringUtils.trimToNull(sourceLink.getLinkReason()));
        if (!StringUtils.equalsAny(normalized.getSourceType(), "KPI", "OKR_OBJECTIVE", "OKR_KR", "STRATEGY", "INSIGHT")) {
            throw new ServiceException("Unsupported action source type: " + normalized.getSourceType());
        }
        return normalized;
    }

    private void assertNoActionItem(String planOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("planOid", planOid);
        DefaultResult<List<MdActionItem>> itemResult = this.mdActionItemService.selectListByParams(params);
        List<MdActionItem> itemList = itemResult.getValue();
        if (itemList != null && !itemList.isEmpty()) {
            throw new ServiceException("This action plan has action items and cannot be deleted.");
        }
    }
}
