package org.qifu.md.logic.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.message.BaseSystemMessage;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.PleaseSelect;
import org.qifu.base.model.ServiceAuthority;
import org.qifu.base.model.ServiceMethodAuthority;
import org.qifu.base.model.ServiceMethodType;
import org.qifu.base.model.YesNoKeyProvide;
import org.qifu.md.entity.MdKpiMeasureData;
import org.qifu.md.entity.MdKpi;
import org.qifu.md.entity.MdOrgMember;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.logic.IKpiMeasureDataLogicService;
import org.qifu.md.model.KpiMeasureDataImportPreview;
import org.qifu.md.model.KpiMeasureDataImportRequest;
import org.qifu.md.model.KpiMeasureDataImportResult;
import org.qifu.md.model.KpiMeasureDataImportRow;
import org.qifu.md.service.IMdKpiService;
import org.qifu.md.service.IMdKpiMeasureDataService;
import org.qifu.md.service.IMdOrgMemberService;
import org.qifu.md.service.IMdOrgUnitService;
import org.qifu.md.util.PeriodKeyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@ServiceAuthority(check = true)
@Transactional(propagation = Propagation.REQUIRED, timeout = 300, readOnly = true)
public class KpiMeasureDataLogicServiceImpl implements IKpiMeasureDataLogicService {

    public static final String DATA_FOR_GLOBAL = "GLOBAL";
    public static final String DATA_FOR_ACCOUNT = "ACCOUNT";
    public static final String DATA_FOR_ORG = "ORG";
    public static final String SOURCE_MANUAL = "MANUAL";
    public static final String SOURCE_IMPORT = "IMPORT";
    public static final String LOCKED_YES = YesNoKeyProvide.YES;
    public static final String LOCKED_NO = YesNoKeyProvide.NO;
    public static final String PERIOD_DAY = "DAY";
    public static final String PERIOD_WEEK = "WEEK";
    public static final String PERIOD_MONTH = "MONTH";
    public static final String PERIOD_QUARTER = "QUARTER";
    public static final String PERIOD_HALFYEAR = "HALFYEAR";
    public static final String PERIOD_YEAR = "YEAR";
    public static final String KPI_PERIOD_ALL = "ALL";

    private static final int MAX_IMPORT_ROWS = 1000;
    private static final List<String> IMPORT_HEADERS = List.of(
            "kpi_code", "period_type", "period_key", "data_for_type", "org_code",
            "account", "target_value", "actual_value", "note");

    private final IMdKpiMeasureDataService<MdKpiMeasureData, String> mdKpiMeasureDataService;
    private final IMdKpiService<MdKpi, String> mdKpiService;
    private final IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService;
    private final IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService;

    public KpiMeasureDataLogicServiceImpl(IMdKpiMeasureDataService<MdKpiMeasureData, String> mdKpiMeasureDataService,
            IMdKpiService<MdKpi, String> mdKpiService,
            IMdOrgUnitService<MdOrgUnit, String> mdOrgUnitService,
            IMdOrgMemberService<MdOrgMember, String> mdOrgMemberService) {
        this.mdKpiMeasureDataService = mdKpiMeasureDataService;
        this.mdKpiService = mdKpiService;
        this.mdOrgUnitService = mdOrgUnitService;
        this.mdOrgMemberService = mdOrgMemberService;
    }

    @Override
    public DefaultResult<MdKpiMeasureData> loadByKey(MdKpiMeasureData entity) throws ServiceException {
        MdKpiMeasureData normalized = normalizeKey(entity);
        DefaultResult<MdKpiMeasureData> result = new DefaultResult<>();
        List<MdKpiMeasureData> list = this.mdKpiMeasureDataService.selectListByParams(toKeyParams(normalized)).getValue();
        result.setValue(list == null || list.isEmpty() ? null : list.get(0));
        result.setSuccess(YesNoKeyProvide.YES);
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
            if (!LOCKED_NO.equals(normalized.getLocked())) {
                throw new ServiceException("KPI measure data is locked. Set Locked to No and save to unlock it first.");
            }
            existing.setLocked(LOCKED_NO);
            this.mdKpiMeasureDataService.update(existing).getValueEmptyThrowMessage();
            return this.mdKpiMeasureDataService.selectByEntityPrimaryKey(existing);
        }
        normalized.setOid(existing.getOid());
        this.mdKpiMeasureDataService.update(normalized).getValueEmptyThrowMessage();
        DefaultResult<MdKpiMeasureData> result = this.mdKpiMeasureDataService.selectByEntityPrimaryKey(normalized);
        result.setMessage(BaseSystemMessage.updateSuccess());
        return result;
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

    @Override
    public DefaultResult<KpiMeasureDataImportPreview> previewImport(InputStream inputStream) throws ServiceException {
        if (inputStream == null) {
            throw new ServiceException("CSV file is required.");
        }
        List<KpiMeasureDataImportRow> rows = parseCsv(inputStream);
        KpiMeasureDataImportPreview preview = validateImportRows(rows, false).preview;
        DefaultResult<KpiMeasureDataImportPreview> result = new DefaultResult<>();
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(preview);
        result.setMessage(preview.isCanImport() ? "CSV validation passed." : "CSV contains validation errors.");
        return result;
    }

    @ServiceMethodAuthority(type = ServiceMethodType.UPDATE)
    @Transactional(
            propagation = Propagation.REQUIRED,
            readOnly = false,
            rollbackFor = {RuntimeException.class, IOException.class, Exception.class})
    @Override
    public DefaultResult<KpiMeasureDataImportResult> importRows(KpiMeasureDataImportRequest request) throws ServiceException {
        if (request == null || request.getRows() == null || request.getRows().isEmpty()) {
            throw new ServiceException("Import rows are required.");
        }
        if (request.getRows().size() > MAX_IMPORT_ROWS) {
            throw new ServiceException("CSV import cannot exceed " + MAX_IMPORT_ROWS + " rows.");
        }
        ValidationBatch batch = validateImportRows(request.getRows(), true);
        if (!batch.preview.isCanImport()) {
            throw new ServiceException("CSV data changed or contains validation errors. Please preview the file again.");
        }

        KpiMeasureDataImportResult imported = new KpiMeasureDataImportResult();
        for (int i = 0; i < batch.entities.size(); i++) {
            MdKpiMeasureData entity = batch.entities.get(i);
            entity.setSourceType(SOURCE_IMPORT);
            entity.setSourceRef(StringUtils.abbreviate(StringUtils.trimToNull(request.getSourceRef()), 200));
            saveOrUpdate(entity).getValueEmptyThrowMessage();
            if ("UPDATE".equals(batch.preview.getRows().get(i).getAction())) {
                imported.setUpdateCount(imported.getUpdateCount() + 1);
            } else {
                imported.setInsertCount(imported.getInsertCount() + 1);
            }
        }
        imported.setTotalCount(batch.entities.size());
        DefaultResult<KpiMeasureDataImportResult> result = new DefaultResult<>();
        result.setSuccess(YesNoKeyProvide.YES);
        result.setValue(imported);
        result.setMessage("CSV import completed.");
        return result;
    }

    private List<KpiMeasureDataImportRow> parseCsv(InputStream inputStream) throws ServiceException {
        try (PushbackReader reader = new PushbackReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8), 1)) {
            int first = reader.read();
            if (first != 0xFEFF && first != -1) {
                reader.unread(first);
            }
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .get();
            try (CSVParser parser = format.parse(reader)) {
                Set<String> headers = new HashSet<>();
                parser.getHeaderMap().keySet().forEach(header -> headers.add(StringUtils.lowerCase(StringUtils.trim(header))));
                if (!headers.containsAll(IMPORT_HEADERS)) {
                    throw new ServiceException("CSV header must be: " + String.join(",", IMPORT_HEADERS));
                }
                List<KpiMeasureDataImportRow> rows = new ArrayList<>();
                for (CSVRecord record : parser) {
                    if (isBlankRecord(record)) {
                        continue;
                    }
                    if (rows.size() >= MAX_IMPORT_ROWS) {
                        throw new ServiceException("CSV import cannot exceed " + MAX_IMPORT_ROWS + " rows.");
                    }
                    KpiMeasureDataImportRow row = new KpiMeasureDataImportRow();
                    row.setRowNumber((int) record.getRecordNumber() + 1);
                    row.setKpiCode(value(record, "kpi_code"));
                    row.setPeriodType(value(record, "period_type"));
                    row.setPeriodKey(value(record, "period_key"));
                    row.setDataForType(value(record, "data_for_type"));
                    row.setOrgCode(value(record, "org_code"));
                    row.setAccount(value(record, "account"));
                    row.setTargetValue(value(record, "target_value"));
                    row.setActualValue(value(record, "actual_value"));
                    row.setNote(value(record, "note"));
                    rows.add(row);
                }
                if (rows.isEmpty()) {
                    throw new ServiceException("CSV file contains no data rows.");
                }
                return rows;
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new ServiceException("Unable to parse CSV file: " + e.getMessage());
        }
    }

    private String value(CSVRecord record, String header) {
        return StringUtils.trimToEmpty(record.get(header));
    }

    private boolean isBlankRecord(CSVRecord record) {
        for (String value : record) {
            if (StringUtils.isNotBlank(value)) {
                return false;
            }
        }
        return true;
    }

    private ValidationBatch validateImportRows(List<KpiMeasureDataImportRow> sourceRows, boolean copyRows) throws ServiceException {
        ImportLookup lookup = loadImportLookup();
        List<KpiMeasureDataImportRow> rows = new ArrayList<>();
        List<MdKpiMeasureData> entities = new ArrayList<>();
        Set<String> naturalKeys = new HashSet<>();

        for (int i = 0; i < sourceRows.size(); i++) {
            KpiMeasureDataImportRow row = copyRows ? copyImportRow(sourceRows.get(i), i + 2) : sourceRows.get(i);
            row.setErrors(new ArrayList<>());
            row.setAction(null);
            MdKpiMeasureData entity = validateImportRow(row, lookup);
            if (entity != null) {
                String naturalKey = importNaturalKey(entity);
                if (!naturalKeys.add(naturalKey)) {
                    row.getErrors().add("Duplicate natural key in CSV file.");
                }
            }
            if (row.getErrors().isEmpty() && entity != null) {
                MdKpiMeasureData existing = loadByKey(entity).getValue();
                if (existing != null && LOCKED_YES.equals(existing.getLocked())) {
                    row.getErrors().add("Existing measure data is locked.");
                } else {
                    row.setAction(existing == null ? "INSERT" : "UPDATE");
                }
            }
            row.setValid(row.getErrors().isEmpty());
            rows.add(row);
            entities.add(entity);
        }

        KpiMeasureDataImportPreview preview = new KpiMeasureDataImportPreview();
        preview.setRows(rows);
        preview.setTotalCount(rows.size());
        preview.setValidCount((int) rows.stream().filter(KpiMeasureDataImportRow::isValid).count());
        preview.setErrorCount(preview.getTotalCount() - preview.getValidCount());
        preview.setInsertCount((int) rows.stream().filter(row -> "INSERT".equals(row.getAction())).count());
        preview.setUpdateCount((int) rows.stream().filter(row -> "UPDATE".equals(row.getAction())).count());
        preview.setCanImport(preview.getTotalCount() > 0 && preview.getErrorCount() == 0);
        return new ValidationBatch(preview, entities);
    }

    private MdKpiMeasureData validateImportRow(KpiMeasureDataImportRow row, ImportLookup lookup) {
        row.setKpiCode(StringUtils.upperCase(StringUtils.trimToEmpty(row.getKpiCode())));
        row.setPeriodType(StringUtils.upperCase(StringUtils.trimToEmpty(row.getPeriodType())));
        row.setPeriodKey(StringUtils.trimToEmpty(row.getPeriodKey()));
        row.setDataForType(StringUtils.upperCase(StringUtils.trimToEmpty(row.getDataForType())));
        row.setOrgCode(StringUtils.upperCase(StringUtils.trimToEmpty(row.getOrgCode())));
        row.setAccount(StringUtils.trimToEmpty(row.getAccount()));

        MdKpi kpi = lookup.kpis.get(row.getKpiCode());
        if (kpi == null || !YesNoKeyProvide.YES.equals(kpi.getEnabled())) {
            row.getErrors().add("KPI code does not exist or is disabled.");
        } else {
            row.setKpiName(kpi.getKpiName());
            if (!KPI_PERIOD_ALL.equals(kpi.getPeriodType())
                    && !Strings.CS.equals(kpi.getPeriodType(), row.getPeriodType())) {
                row.getErrors().add("period_type must match the KPI period type: " + kpi.getPeriodType() + ".");
            }
        }

        if (!isValidPeriodKey(row.getPeriodType(), row.getPeriodKey())) {
            row.getErrors().add("period_key format is invalid for " + row.getPeriodType() + ".");
        }

        MdOrgUnit org = null;
        if (StringUtils.isNotBlank(row.getOrgCode())) {
            org = lookup.orgs.get(row.getOrgCode());
            if (org == null || !YesNoKeyProvide.YES.equals(org.getEnabled())) {
                row.getErrors().add("Organization code does not exist or is disabled.");
            } else {
                row.setOrgName(org.getOrgName());
            }
        }
        if (DATA_FOR_GLOBAL.equals(row.getDataForType())) {
            if (StringUtils.isNotBlank(row.getOrgCode()) || StringUtils.isNotBlank(row.getAccount())) {
                row.getErrors().add("GLOBAL rows must not contain org_code or account.");
            }
        } else if (DATA_FOR_ORG.equals(row.getDataForType())) {
            if (StringUtils.isBlank(row.getOrgCode())) {
                row.getErrors().add("org_code is required for ORG rows.");
            }
            if (StringUtils.isNotBlank(row.getAccount())) {
                row.getErrors().add("ORG rows must not contain account.");
            }
        } else if (DATA_FOR_ACCOUNT.equals(row.getDataForType())) {
            if (StringUtils.isBlank(row.getAccount())) {
                row.getErrors().add("account is required for ACCOUNT rows.");
            } else if (!hasEnabledMember(lookup.members, row.getAccount(), org == null ? null : org.getOid())) {
                row.getErrors().add(org == null
                        ? "Account does not exist or is disabled."
                        : "Account is not an enabled member of the specified organization.");
            }
        } else {
            row.getErrors().add("data_for_type only allows GLOBAL, ORG, or ACCOUNT.");
        }

        BigDecimal target = decimal(row.getTargetValue(), "target_value", row.getErrors());
        BigDecimal actual = decimal(row.getActualValue(), "actual_value", row.getErrors());
        if (StringUtils.length(row.getNote()) > 2000) {
            row.getErrors().add("note cannot exceed 2000 characters.");
        }
        if (!row.getErrors().isEmpty() || kpi == null) {
            return null;
        }
        MdKpiMeasureData entity = new MdKpiMeasureData();
        entity.setKpiOid(kpi.getOid());
        entity.setPeriodType(row.getPeriodType());
        entity.setPeriodKey(row.getPeriodKey());
        entity.setDataForType(row.getDataForType());
        entity.setOrgOid(DATA_FOR_ORG.equals(row.getDataForType()) && org != null ? org.getOid() : null);
        entity.setAccount(DATA_FOR_ACCOUNT.equals(row.getDataForType()) ? row.getAccount() : null);
        entity.setTargetValue(target);
        entity.setActualValue(actual);
        entity.setEvidenceText(StringUtils.trimToNull(row.getNote()));
        entity.setSourceType(SOURCE_IMPORT);
        entity.setLocked(LOCKED_NO);
        try {
            return normalizeForSave(entity);
        } catch (ServiceException e) {
            row.getErrors().add(e.getMessage());
            return null;
        }
    }

    private BigDecimal decimal(String value, String field, List<String> errors) {
        if (StringUtils.isBlank(value)) {
            errors.add(field + " is required.");
            return null;
        }
        try {
            BigDecimal decimal = new BigDecimal(StringUtils.trim(value));
            int integerDigits = decimal.precision() - decimal.scale();
            if (decimal.scale() > 6 || integerDigits > 18) {
                errors.add(field + " cannot exceed DECIMAL(24,6).");
                return null;
            }
            return decimal;
        } catch (NumberFormatException e) {
            errors.add(field + " must be a number.");
            return null;
        }
    }

    private boolean isValidPeriodKey(String periodType, String periodKey) {
        return PeriodKeyUtils.isValid(periodType, periodKey);
    }

    private ImportLookup loadImportLookup() throws ServiceException {
        Map<String, MdKpi> kpis = new LinkedHashMap<>();
        for (MdKpi item : safeList(this.mdKpiService.selectList().getValue())) {
            kpis.put(StringUtils.upperCase(item.getKpiCode()), item);
        }
        Map<String, MdOrgUnit> orgs = new LinkedHashMap<>();
        for (MdOrgUnit item : safeList(this.mdOrgUnitService.selectList().getValue())) {
            orgs.put(StringUtils.upperCase(item.getOrgCode()), item);
        }
        return new ImportLookup(kpis, orgs, safeList(this.mdOrgMemberService.selectList().getValue()));
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }

    private boolean hasEnabledMember(List<MdOrgMember> members, String account, String orgOid) {
        return members.stream().anyMatch(member -> Strings.CS.equals(member.getAccount(), account)
                && YesNoKeyProvide.YES.equals(member.getEnabled())
                && (StringUtils.isBlank(orgOid) || Strings.CS.equals(member.getOrgOid(), orgOid)));
    }

    private String importNaturalKey(MdKpiMeasureData entity) {
        return String.join("|", StringUtils.defaultString(entity.getKpiOid()),
                StringUtils.defaultString(entity.getPeriodType()), StringUtils.defaultString(entity.getPeriodKey()),
                StringUtils.defaultString(entity.getDataForType()), StringUtils.defaultString(entity.getOrgOid()),
                StringUtils.defaultString(entity.getAccount()));
    }

    private KpiMeasureDataImportRow copyImportRow(KpiMeasureDataImportRow source, int defaultRowNumber) {
        KpiMeasureDataImportRow row = new KpiMeasureDataImportRow();
        row.setRowNumber(source.getRowNumber() > 0 ? source.getRowNumber() : defaultRowNumber);
        row.setKpiCode(source.getKpiCode());
        row.setPeriodType(source.getPeriodType());
        row.setPeriodKey(source.getPeriodKey());
        row.setDataForType(source.getDataForType());
        row.setOrgCode(source.getOrgCode());
        row.setAccount(source.getAccount());
        row.setTargetValue(source.getTargetValue());
        row.setActualValue(source.getActualValue());
        row.setNote(source.getNote());
        return row;
    }

    private record ImportLookup(Map<String, MdKpi> kpis, Map<String, MdOrgUnit> orgs,
            List<MdOrgMember> members) { }

    private record ValidationBatch(KpiMeasureDataImportPreview preview,
            List<MdKpiMeasureData> entities) { }

    private MdKpiMeasureData normalizeForSave(MdKpiMeasureData entity) throws ServiceException {
        MdKpiMeasureData normalized = normalizeKey(entity);
        validateKpiPeriodType(normalized);
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

    private void validateKpiPeriodType(MdKpiMeasureData entity) throws ServiceException {
        if (!isValidPeriodKey(entity.getPeriodType(), entity.getPeriodKey())) {
            throw new ServiceException("Invalid period key for period type: " + entity.getPeriodType() + ".");
        }
        MdKpi key = new MdKpi();
        key.setOid(entity.getKpiOid());
        MdKpi kpi = this.mdKpiService.selectByEntityPrimaryKey(key).getValueEmptyThrowMessage();
        if (!YesNoKeyProvide.YES.equals(kpi.getEnabled())) {
            throw new ServiceException("KPI is disabled.");
        }
        if (!KPI_PERIOD_ALL.equals(kpi.getPeriodType())
                && !Strings.CS.equals(kpi.getPeriodType(), entity.getPeriodType())) {
            throw new ServiceException("Period type must match the KPI period type: " + kpi.getPeriodType() + ".");
        }
    }
    private java.util.Date toMeasureDate(String periodType, String periodKey) throws ServiceException {
        return PeriodKeyUtils.toDate(periodType, periodKey);
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
