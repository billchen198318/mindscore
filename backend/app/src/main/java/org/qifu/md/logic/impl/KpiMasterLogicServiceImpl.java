package org.qifu.md.logic.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.message.BaseSystemMessage;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.ServiceMethodAuthority;
import org.qifu.base.model.ServiceMethodType;
import org.qifu.base.model.YesNo;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdKpiOwner;
import org.qifu.md.logic.IKpiMasterLogicService;
import org.qifu.md.model.KpiMasterRequest;
import org.qifu.md.service.IMdKpiOwnerService;
import org.qifu.md.service.IMdKpiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class KpiMasterLogicServiceImpl implements IKpiMasterLogicService {

    public static final String OWNER_TYPE_ORG = "ORG";
    public static final String OWNER_TYPE_ACCOUNT = "ACCOUNT";
    public static final String OWNER_ROLE_OWNER = "OWNER";

    private final IMdKpiService<MdKpi, String> mdKpiService;
    private final IMdKpiOwnerService<MdKpiOwner, String> mdKpiOwnerService;

    public KpiMasterLogicServiceImpl(IMdKpiService<MdKpi, String> mdKpiService,
            IMdKpiOwnerService<MdKpiOwner, String> mdKpiOwnerService) {
        this.mdKpiService = mdKpiService;
        this.mdKpiOwnerService = mdKpiOwnerService;
    }

    @ServiceMethodAuthority(type = ServiceMethodType.INSERT)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<KpiMasterRequest> create(KpiMasterRequest request) throws ServiceException {
        MdKpi kpi = requireKpi(request);
        DefaultResult<MdKpi> kpiResult = this.mdKpiService.insert(kpi);
        MdKpi savedKpi = kpiResult.getValueEmptyThrowMessage();
        rebuildOwners(savedKpi.getOid(), request.getOwnerList());
        return load(savedKpi);
    }

    @Override
    public DefaultResult<KpiMasterRequest> load(MdKpi entity) throws ServiceException {
        if (entity == null || StringUtils.isBlank(entity.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        DefaultResult<MdKpi> kpiResult = this.mdKpiService.selectByEntityPrimaryKey(entity);
        MdKpi kpi = kpiResult.getValueEmptyThrowMessage();

        Map<String, Object> params = new HashMap<>();
        params.put("kpiOid", kpi.getOid());
        DefaultResult<List<MdKpiOwner>> ownerResult = this.mdKpiOwnerService.selectListByParams(params, "OWNER_TYPE, ACCOUNT, ORG_OID", "ASC");

        KpiMasterRequest value = new KpiMasterRequest();
        value.setKpi(kpi);
        value.setOwnerList(ownerResult.getValue());

        DefaultResult<KpiMasterRequest> result = new DefaultResult<>();
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
    public DefaultResult<KpiMasterRequest> update(KpiMasterRequest request) throws ServiceException {
        MdKpi kpi = requireKpi(request);
        if (StringUtils.isBlank(kpi.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        this.mdKpiService.update(kpi).getValueEmptyThrowMessage();
        rebuildOwners(kpi.getOid(), request.getOwnerList());
        return load(kpi);
    }

    @ServiceMethodAuthority(type = ServiceMethodType.DELETE)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<Boolean> delete(MdKpi entity) throws ServiceException {
        if (entity == null || StringUtils.isBlank(entity.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        deleteOwners(entity.getOid());
        return this.mdKpiService.delete(entity);
    }

    private MdKpi requireKpi(KpiMasterRequest request) throws ServiceException {
        if (request == null || request.getKpi() == null) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        return request.getKpi();
    }

    private void rebuildOwners(String kpiOid, List<MdKpiOwner> ownerList) throws ServiceException {
        deleteOwners(kpiOid);
        if (ownerList == null) {
            return;
        }
        for (MdKpiOwner owner : ownerList) {
            MdKpiOwner normalized = normalizeOwner(kpiOid, owner);
            if (normalized != null) {
                this.mdKpiOwnerService.insert(normalized);
            }
        }
    }

    private void deleteOwners(String kpiOid) throws ServiceException {
        Map<String, Object> params = new HashMap<>();
        params.put("kpiOid", kpiOid);
        DefaultResult<List<MdKpiOwner>> ownerResult = this.mdKpiOwnerService.selectListByParams(params);
        List<MdKpiOwner> owners = ownerResult.getValue();
        for (int i = 0; owners != null && i < owners.size(); i++) {
            this.mdKpiOwnerService.delete(owners.get(i));
        }
    }

    private MdKpiOwner normalizeOwner(String kpiOid, MdKpiOwner owner) throws ServiceException {
        if (owner == null || StringUtils.isBlank(owner.getOwnerType())) {
            return null;
        }
        MdKpiOwner normalized = new MdKpiOwner();
        normalized.setKpiOid(kpiOid);
        normalized.setOwnerRole(StringUtils.defaultIfBlank(owner.getOwnerRole(), OWNER_ROLE_OWNER));

        if (OWNER_TYPE_ORG.equals(owner.getOwnerType())) {
            if (StringUtils.isBlank(owner.getOrgOid())) {
                return null;
            }
            normalized.setOwnerType(OWNER_TYPE_ORG);
            normalized.setOrgOid(owner.getOrgOid());
            return normalized;
        }
        if (OWNER_TYPE_ACCOUNT.equals(owner.getOwnerType())) {
            if (StringUtils.isBlank(owner.getAccount())) {
                return null;
            }
            normalized.setOwnerType(OWNER_TYPE_ACCOUNT);
            normalized.setAccount(owner.getAccount());
            return normalized;
        }
        throw new ServiceException("Unsupported KPI owner type: " + owner.getOwnerType());
    }
}
