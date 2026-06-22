package org.qifu.md.logic;

import java.io.InputStream;

import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.DefaultResult;
import org.qifu.md.entity.MdKpiMeasureData;
import org.qifu.md.model.KpiMeasureDataImportPreview;
import org.qifu.md.model.KpiMeasureDataImportRequest;
import org.qifu.md.model.KpiMeasureDataImportResult;

public interface IKpiMeasureDataLogicService {

    DefaultResult<MdKpiMeasureData> loadByKey(MdKpiMeasureData entity) throws ServiceException;

    DefaultResult<MdKpiMeasureData> saveOrUpdate(MdKpiMeasureData entity) throws ServiceException;

    DefaultResult<Boolean> delete(MdKpiMeasureData entity) throws ServiceException;

    DefaultResult<KpiMeasureDataImportPreview> previewImport(InputStream inputStream) throws ServiceException;

    DefaultResult<KpiMeasureDataImportResult> importRows(KpiMeasureDataImportRequest request) throws ServiceException;
}
