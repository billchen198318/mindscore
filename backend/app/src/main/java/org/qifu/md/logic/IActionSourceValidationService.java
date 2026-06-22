package org.qifu.md.logic;

import org.qifu.base.exception.ServiceException;

public interface IActionSourceValidationService {
    void validate(String sourceType, String sourceOid) throws ServiceException;
}
