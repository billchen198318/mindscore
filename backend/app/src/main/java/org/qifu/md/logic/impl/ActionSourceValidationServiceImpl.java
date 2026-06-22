package org.qifu.md.logic.impl;

import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ServiceException;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdOkrKeyResult;
import org.qifu.md.entity.MdOkrObjective;
import org.qifu.md.entity.MdStrategyObjective;
import org.qifu.md.logic.IActionSourceValidationService;
import org.qifu.md.service.IMdKpiService;
import org.qifu.md.service.IMdOkrKeyResultService;
import org.qifu.md.service.IMdOkrObjectiveService;
import org.qifu.md.service.IMdStrategyObjectiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class ActionSourceValidationServiceImpl implements IActionSourceValidationService {
    private final IMdKpiService<MdKpi, String> mdKpiService;
    private final IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService;
    private final IMdOkrKeyResultService<MdOkrKeyResult, String> mdOkrKeyResultService;
    private final IMdStrategyObjectiveService<MdStrategyObjective, String> mdStrategyObjectiveService;

    public ActionSourceValidationServiceImpl(IMdKpiService<MdKpi, String> mdKpiService,
            IMdOkrObjectiveService<MdOkrObjective, String> mdOkrObjectiveService,
            IMdOkrKeyResultService<MdOkrKeyResult, String> mdOkrKeyResultService,
            IMdStrategyObjectiveService<MdStrategyObjective, String> mdStrategyObjectiveService) {
        this.mdKpiService = mdKpiService;
        this.mdOkrObjectiveService = mdOkrObjectiveService;
        this.mdOkrKeyResultService = mdOkrKeyResultService;
        this.mdStrategyObjectiveService = mdStrategyObjectiveService;
    }

    @Override
    public void validate(String sourceType, String sourceOid) throws ServiceException {
        if (StringUtils.isAnyBlank(sourceType, sourceOid)) {
            throw new ServiceException("Action source type and OID are required.");
        }
        boolean exists;
        if ("KPI".equals(sourceType)) {
            MdKpi key = new MdKpi();
            key.setOid(sourceOid);
            exists = this.mdKpiService.selectByEntityPrimaryKey(key).getValue() != null;
        } else if ("OKR_OBJECTIVE".equals(sourceType)) {
            MdOkrObjective key = new MdOkrObjective();
            key.setOid(sourceOid);
            exists = this.mdOkrObjectiveService.selectByEntityPrimaryKey(key).getValue() != null;
        } else if ("OKR_KR".equals(sourceType)) {
            MdOkrKeyResult key = new MdOkrKeyResult();
            key.setOid(sourceOid);
            exists = this.mdOkrKeyResultService.selectByEntityPrimaryKey(key).getValue() != null;
        } else if ("STRATEGY".equals(sourceType)) {
            MdStrategyObjective key = new MdStrategyObjective();
            key.setOid(sourceOid);
            exists = this.mdStrategyObjectiveService.selectByEntityPrimaryKey(key).getValue() != null;
        } else if ("INSIGHT".equals(sourceType)) {
            return;
        } else {
            throw new ServiceException("Unsupported action source type: " + sourceType);
        }
        if (!exists) {
            throw new ServiceException("Action source does not exist: " + sourceType + " / " + sourceOid);
        }
    }
}
