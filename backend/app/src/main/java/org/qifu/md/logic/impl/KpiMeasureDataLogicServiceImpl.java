package org.qifu.md.logic.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
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
import org.qifu.md.entity.MdKpiMeasureData;
import org.qifu.md.logic.IKpiMeasureDataLogicService;
import org.qifu.md.service.IMdKpiMeasureDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class KpiMeasureDataLogicServiceImpl implements IKpiMeasureDataLogicService {

    public static final String DATA_FOR_GLOBAL = "GLOBAL";
    public static final String DATA_FOR_ACCOUNT = "ACCOUNT";
    public static final String DATA_FOR_ORG = "ORG";
    public static final String SOURCE_MANUAL = "MANUAL";
    public static final String LOCKED_YES = "Y";
    public static final String LOCKED_NO = "N";
    public static final String PERIOD_DAY = "DAY";
    public static final String PERIOD_WEEK = "WEEK";
    public static final String PERIOD_MONTH = "MONTH";
    public static final String PERIOD_QUARTER = "QUARTER";
    public static final String PERIOD_HALFYEAR = "HALFYEAR";
    public static final String PERIOD_YEAR = "YEAR";

    private final IMdKpiMeasureDataService<MdKpiMeasureData, String> mdKpiMeasureDataService;

    public KpiMeasureDataLogicServiceImpl(IMdKpiMeasureDataService<MdKpiMeasureData, String> mdKpiMeasureDataService) {
        this.mdKpiMeasureDataService = mdKpiMeasureDataService;
    }

    @Override
    public DefaultResult<MdKpiMeasureData> loadByKey(MdKpiMeasureData entity) throws ServiceException {
        MdKpiMeasureData normalized = normalizeKey(entity);
        DefaultResult<MdKpiMeasureData> result = new DefaultResult<>();
        List<MdKpiMeasureData> list = this.mdKpiMeasureDataService.selectListByParams(toKeyParams(normalized)).getValue();
        result.setValue(list == null || list.isEmpty() ? null : list.get(0));
        result.setSuccess("Y");
        return result;
    }

    @ServiceMethodAuthority(type = ServiceMethodType.UPDATE)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<MdKpiMeasureData> saveOrUpdate(MdKpiMeasureData entity) throws ServiceException {
        MdKpiMeasureData normalized = normalizeForSave(entity);
        DefaultResult<MdKpiMeasureData> loadResult = loadByKey(normalized);
        MdKpiMeasureData existing = loadResult.getValue();
        if (existing == null) {
            return this.mdKpiMeasureDataService.insert(normalized);
        }
        if (LOCKED_YES.equals(existing.getLocked())) {
            throw new ServiceException("KPI measure data is locked.");
        }
        normalized.setOid(existing.getOid());
        this.mdKpiMeasureDataService.update(normalized).getValueEmptyThrowMessage();
        return this.mdKpiMeasureDataService.selectByEntityPrimaryKey(normalized);
    }

    @ServiceMethodAuthority(type = ServiceMethodType.DELETE)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<Boolean> delete(MdKpiMeasureData entity) throws ServiceException {
        if (entity == null || StringUtils.isBlank(entity.getOid())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }
        MdKpiMeasureData existing = this.mdKpiMeasureDataService.selectByEntityPrimaryKey(entity).getValueEmptyThrowMessage();
        if (LOCKED_YES.equals(existing.getLocked())) {
            throw new ServiceException("KPI measure data is locked.");
        }
        return this.mdKpiMeasureDataService.delete(entity);
    }

    private MdKpiMeasureData normalizeForSave(MdKpiMeasureData entity) throws ServiceException {
        MdKpiMeasureData normalized = normalizeKey(entity);
        normalized.setOid(entity.getOid());
        normalized.setMeasureDate(toMeasureDate(normalized.getPeriodType(), normalized.getPeriodKey()));
        normalized.setTargetValue(entity.getTargetValue());
        normalized.setActualValue(entity.getActualValue());
        normalized.setMinValue(entity.getMinValue());
        normalized.setMaxValue(entity.getMaxValue());
        normalized.setSourceType(StringUtils.defaultIfBlank(entity.getSourceType(), SOURCE_MANUAL));
        normalized.setSourceRef(StringUtils.defaultIfBlank(entity.getSourceRef(), null));
        normalized.setEvidenceText(StringUtils.defaultIfBlank(entity.getEvidenceText(), null));
        normalized.setLocked(StringUtils.defaultIfBlank(entity.getLocked(), LOCKED_NO));
        return normalized;
    }

    private java.util.Date toMeasureDate(String periodType, String periodKey) throws ServiceException {
        try {
            LocalDate date;
            if (PERIOD_DAY.equals(periodType)) {
                date = LocalDate.parse(periodKey);
            } else if (PERIOD_WEEK.equals(periodType)) {
                String[] parts = periodKey.split("-W");
                int year = Integer.parseInt(parts[0]);
                int week = Integer.parseInt(parts[1]);
                date = LocalDate.of(year, 1, 4)
                        .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                        .with(ChronoField.DAY_OF_WEEK, 1);
            } else if (PERIOD_MONTH.equals(periodType)) {
                date = LocalDate.parse(periodKey + "-01");
            } else if (PERIOD_QUARTER.equals(periodType)) {
                String[] parts = periodKey.split("-Q");
                date = LocalDate.of(Integer.parseInt(parts[0]), (Integer.parseInt(parts[1]) - 1) * 3 + 1, 1);
            } else if (PERIOD_HALFYEAR.equals(periodType)) {
                String[] parts = periodKey.split("-H");
                date = LocalDate.of(Integer.parseInt(parts[0]), "1".equals(parts[1]) ? 1 : 7, 1);
            } else if (PERIOD_YEAR.equals(periodType)) {
                date = LocalDate.of(Integer.parseInt(periodKey), 1, 1);
            } else {
                throw new ServiceException("Unsupported period type: " + periodType);
            }
            return java.util.Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (RuntimeException e) {
            throw new ServiceException("Invalid period key: " + periodKey);
        }
    }

    private MdKpiMeasureData normalizeKey(MdKpiMeasureData entity) throws ServiceException {
        if (entity == null || PleaseSelect.noSelect(entity.getKpiOid()) || PleaseSelect.noSelect(entity.getPeriodType())
                || StringUtils.isBlank(entity.getPeriodKey()) || PleaseSelect.noSelect(entity.getDataForType())) {
            throw new ServiceException(BaseSystemMessage.parameterBlank());
        }

        MdKpiMeasureData normalized = new MdKpiMeasureData();
        normalized.setKpiOid(entity.getKpiOid());
        normalized.setPeriodType(entity.getPeriodType());
        normalized.setPeriodKey(entity.getPeriodKey());
        normalized.setDataForType(entity.getDataForType());

        if (DATA_FOR_GLOBAL.equals(entity.getDataForType())) {
            normalized.setAccount(null);
            normalized.setOrgOid(null);
            return normalized;
        }
        if (DATA_FOR_ACCOUNT.equals(entity.getDataForType())) {
            if (PleaseSelect.noSelect(entity.getAccount())) {
                throw new ServiceException("Please select account.");
            }
            normalized.setAccount(entity.getAccount());
            normalized.setOrgOid(null);
            return normalized;
        }
        if (DATA_FOR_ORG.equals(entity.getDataForType())) {
            if (PleaseSelect.noSelect(entity.getOrgOid())) {
                throw new ServiceException("Please select organization.");
            }
            normalized.setAccount(null);
            normalized.setOrgOid(entity.getOrgOid());
            return normalized;
        }
        throw new ServiceException("Unsupported data for type: " + entity.getDataForType());
    }

    private Map<String, Object> toKeyParams(MdKpiMeasureData entity) {
        Map<String, Object> params = new HashMap<>();
        params.put("kpiOid", entity.getKpiOid());
        params.put("periodType", entity.getPeriodType());
        params.put("periodKey", entity.getPeriodKey());
        params.put("dataForType", entity.getDataForType());
        if (StringUtils.isNotBlank(entity.getAccount())) {
            params.put("account", entity.getAccount());
        }
        if (StringUtils.isNotBlank(entity.getOrgOid())) {
            params.put("orgOid", entity.getOrgOid());
        }
        return params;
    }
}
