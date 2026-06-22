package org.qifu.md.logic.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.message.BaseSystemMessage;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.PleaseSelect;
import org.qifu.base.model.ServiceMethodAuthority;
import org.qifu.base.model.ServiceMethodType;
import org.qifu.base.model.YesNo;
import org.qifu.md.entity.MdActionItem;
import org.qifu.md.entity.MdActionOwner;
import org.qifu.md.entity.MdActionSourceLink;
import org.qifu.md.logic.IActionItemLogicService;
import org.qifu.md.logic.IActionSourceValidationService;
import org.qifu.md.model.ActionItemRequest;
import org.qifu.md.service.IMdActionItemService;
import org.qifu.md.service.IMdActionOwnerService;
import org.qifu.md.service.IMdActionSourceLinkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class ActionItemLogicServiceImpl implements IActionItemLogicService {

    public static final String ACTION_TYPE_ITEM = "ITEM";
    public static final String OWNER_TYPE_ORG = "ORG";
    public static final String OWNER_TYPE_ACCOUNT = "ACCOUNT";
    public static final String OWNER_ROLE_OWNER = "OWNER";
    public static final String OWNER_ROLE_VIEWER = "VIEWER";
    public static final String OWNER_ROLE_APPROVER = "APPROVER";

    private final IMdActionItemService<MdActionItem, String> mdActionItemService;
    private final IMdActionOwnerService<MdActionOwner, String> mdActionOwnerService;
    private final IMdActionSourceLinkService<MdActionSourceLink, String> mdActionSourceLinkService;
    private final IActionSourceValidationService actionSourceValidationService;

    public ActionItemLogicServiceImpl(IMdActionItemService<MdActionItem, String> mdActionItemService,
            IMdActionOwnerService<MdActionOwner, String> mdActionOwnerService,
            IMdActionSourceLinkService<MdActionSourceLink, String> mdActionSourceLinkService,
            IActionSourceValidationService actionSourceValidationService) {
        this.mdActionItemService = mdActionItemService;
        this.mdActionOwnerService = mdActionOwnerService;
        this.mdActionSourceLinkService = mdActionSourceLinkService;
        this.actionSourceValidationService = actionSourceValidationService;
    }

    @ServiceMethodAuthority(type = ServiceMethodType.INSERT)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<ActionItemRequest> create(ActionItemRequest request) throws ServiceException {
        MdActionItem item = requireItem(request);
        normalizeItem(item);
        DefaultResult<MdActionItem> itemResult = this.mdActionItemService.insert(item);
        MdActionItem savedItem = itemResult.getValueEmptyThrowMessage();
        rebuildOwners(savedItem.getOid(), request.getOwnerList());
        rebuildSourceLinks(savedItem.getOid(), request.getSourceLinkList());
        return load(savedItem);
    }

    @Override
    public DefaultResult<ActionItemRequest> load(MdActionItem entity) throws ServiceException {
        if (entity == null || StringUtils.isBlank(entity.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        DefaultResult<MdActionItem> itemResult = this.mdActionItemService.selectByEntityPrimaryKey(entity);
        MdActionItem item = itemResult.getValueEmptyThrowMessage();

        Map<String, Object> params = new HashMap<>();
        params.put("actionType", ACTION_TYPE_ITEM);
        params.put("actionOid", item.getOid());
        DefaultResult<List<MdActionOwner>> ownerResult = this.mdActionOwnerService.selectListByParams(params, "OWNER_TYPE, ACCOUNT, ORG_OID", "ASC");
        DefaultResult<List<MdActionSourceLink>> sourceLinkResult = this.mdActionSourceLinkService.selectListByParams(params, "SOURCE_TYPE, SOURCE_OID", "ASC");

        ActionItemRequest value = new ActionItemRequest();
        value.setActionItem(item);
        value.setOwnerList(ownerResult.getValue());
        value.setSourceLinkList(sourceLinkResult.getValue());

        DefaultResult<ActionItemRequest> result = new DefaultResult<>();
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
    public DefaultResult<ActionItemRequest> update(ActionItemRequest request) throws ServiceException {
        MdActionItem item = requireItem(request);
        if (StringUtils.isBlank(item.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        normalizeItem(item);
        if (Strings.CS.equals(item.getOid(), item.getParentOid())) {
            throw new ServiceException("Parent action item cannot be the current item.");
        }
        this.mdActionItemService.update(item).getValueEmptyThrowMessage();
        rebuildOwners(item.getOid(), request.getOwnerList());
        rebuildSourceLinks(item.getOid(), request.getSourceLinkList());
        return load(item);
    }

    @ServiceMethodAuthority(type = ServiceMethodType.DELETE)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<Boolean> delete(MdActionItem entity) throws ServiceException {
        if (entity == null || StringUtils.isBlank(entity.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        assertNoChildItem(entity.getOid());
        deleteSourceLinks(entity.getOid());
        deleteOwners(entity.getOid());
        return this.mdActionItemService.delete(entity);
    }

    private MdActionItem requireItem(ActionItemRequest request) throws ServiceException {
        if (request == null || request.getActionItem() == null) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        return request.getActionItem();
    }

    private void normalizeItem(MdActionItem item) throws ServiceException {
        item.setPlanOid(StringUtils.trim(item.getPlanOid()));
        item.setParentOid(StringUtils.trimToNull(item.getParentOid()));
        item.setItemName(StringUtils.trim(item.getItemName()));
        item.setActionStage(StringUtils.defaultIfBlank(item.getActionStage(), "DO"));
        item.setDescription(StringUtils.trimToNull(item.getDescription()));
        item.setStatus(StringUtils.defaultIfBlank(item.getStatus(), "ACTIVE"));
        if (item.getProgressValue() == null) {
            item.setProgressValue(BigDecimal.ZERO);
        }
        if (item.getSortNo() == null) {
            item.setSortNo(0);
        }
        if (!Strings.CS.equalsAny(item.getActionStage(), "PLAN", "DO", "CHECK", "ACT")) {
            throw new ServiceException("Unsupported action stage: " + item.getActionStage());
        }
        if (!Strings.CS.equalsAny(item.getStatus(), "DRAFT", "ACTIVE", "CLOSED", "ARCHIVED")) {
            throw new ServiceException("Unsupported action item status: " + item.getStatus());
        }
        if (item.getStartDate() != null && item.getEndDate() != null && item.getEndDate().before(item.getStartDate())) {
            throw new ServiceException("Action item end date cannot be earlier than start date.");
        }
        if (item.getDoneDate() != null && item.getProgressValue().compareTo(new BigDecimal("100")) < 0) {
            item.setProgressValue(new BigDecimal("100"));
        }
    }

    private void rebuildOwners(String itemOid, List<MdActionOwner> ownerList) throws ServiceException {
        deleteOwners(itemOid);
        if (ownerList == null) {
            return;
        }
        for (MdActionOwner owner : ownerList) {
            MdActionOwner normalized = normalizeOwner(itemOid, owner);
            if (normalized != null) {
                this.mdActionOwnerService.insert(normalized);
            }
        }
    }

    private void deleteOwners(String itemOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("actionType", ACTION_TYPE_ITEM);
        params.put("actionOid", itemOid);
        DefaultResult<List<MdActionOwner>> ownerResult = this.mdActionOwnerService.selectListByParams(params);
        List<MdActionOwner> owners = ownerResult.getValue();
        for (int i = 0; owners != null && i < owners.size(); i++) {
            this.mdActionOwnerService.delete(owners.get(i));
        }
    }

    private MdActionOwner normalizeOwner(String itemOid, MdActionOwner owner) throws ServiceException {
        if (owner == null || StringUtils.isBlank(owner.getOwnerType())) {
            return null;
        }
        MdActionOwner normalized = new MdActionOwner();
        normalized.setActionType(ACTION_TYPE_ITEM);
        normalized.setActionOid(itemOid);
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
        if (Strings.CS.equalsAny(ownerRole, OWNER_ROLE_OWNER, OWNER_ROLE_VIEWER, OWNER_ROLE_APPROVER)) {
            return ownerRole;
        }
        throw new ServiceException("Unsupported action owner role: " + ownerRole);
    }

    private void rebuildSourceLinks(String itemOid, List<MdActionSourceLink> sourceLinkList) throws ServiceException {
        if (sourceLinkList == null) {
            deleteSourceLinks(itemOid);
            return;
        }
        List<MdActionSourceLink> normalizedList = new ArrayList<>();
        Set<String> sourceKeys = new HashSet<>();
        for (MdActionSourceLink sourceLink : sourceLinkList) {
            MdActionSourceLink normalized = normalizeSourceLink(itemOid, sourceLink);
            if (normalized != null) {
                String sourceKey = normalized.getSourceType() + "|" + normalized.getSourceOid();
                if (!sourceKeys.add(sourceKey)) {
                    throw new ServiceException("Duplicate action source: " + normalized.getSourceType() + " / " + normalized.getSourceOid());
                }
                this.actionSourceValidationService.validate(normalized.getSourceType(), normalized.getSourceOid());
                normalizedList.add(normalized);
            }
        }
        deleteSourceLinks(itemOid);
        for (MdActionSourceLink normalized : normalizedList) {
            this.mdActionSourceLinkService.insert(normalized);
        }
    }

    private void deleteSourceLinks(String itemOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("actionType", ACTION_TYPE_ITEM);
        params.put("actionOid", itemOid);
        DefaultResult<List<MdActionSourceLink>> sourceLinkResult = this.mdActionSourceLinkService.selectListByParams(params);
        List<MdActionSourceLink> sourceLinks = sourceLinkResult.getValue();
        for (int i = 0; sourceLinks != null && i < sourceLinks.size(); i++) {
            this.mdActionSourceLinkService.delete(sourceLinks.get(i));
        }
    }

    private MdActionSourceLink normalizeSourceLink(String itemOid, MdActionSourceLink sourceLink) throws ServiceException {
        if (sourceLink == null || StringUtils.isBlank(sourceLink.getSourceType()) || PleaseSelect.noSelect(sourceLink.getSourceOid())) {
            return null;
        }
        MdActionSourceLink normalized = new MdActionSourceLink();
        normalized.setActionType(ACTION_TYPE_ITEM);
        normalized.setActionOid(itemOid);
        normalized.setSourceType(StringUtils.trim(sourceLink.getSourceType()));
        normalized.setSourceOid(StringUtils.trim(sourceLink.getSourceOid()));
        normalized.setLinkReason(StringUtils.trimToNull(sourceLink.getLinkReason()));
        if (!Strings.CS.equalsAny(normalized.getSourceType(), "KPI", "OKR_OBJECTIVE", "OKR_KR", "STRATEGY", "INSIGHT")) {
            throw new ServiceException("Unsupported action source type: " + normalized.getSourceType());
        }
        return normalized;
    }

    private void assertNoChildItem(String itemOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("parentOid", itemOid);
        DefaultResult<List<MdActionItem>> itemResult = this.mdActionItemService.selectListByParams(params);
        List<MdActionItem> itemList = itemResult.getValue();
        if (itemList != null && !itemList.isEmpty()) {
            throw new ServiceException("This action item has child items and cannot be deleted.");
        }
    }
}
