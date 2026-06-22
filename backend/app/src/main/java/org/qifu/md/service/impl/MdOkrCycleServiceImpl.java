package org.qifu.md.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdOkrCycle;
import org.qifu.md.mapper.MdOkrCycleMapper;
import org.qifu.md.service.IMdOkrCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdOkrCycleServiceImpl extends BaseService<MdOkrCycle, String> implements IMdOkrCycleService<MdOkrCycle, String> {
	private static final String STATUS_DRAFT = "DRAFT";
	private static final String STATUS_ACTIVE = "ACTIVE";
	private static final String STATUS_CLOSED = "CLOSED";
	private static final String STATUS_ARCHIVED = "ARCHIVED";

	private MdOkrCycleMapper mdOkrCycleMapper;
	
	@Autowired
	public MdOkrCycleServiceImpl(MdOkrCycleMapper mdOkrCycleMapper) {
		this.mdOkrCycleMapper = mdOkrCycleMapper;
	}
	
	@Override
	protected IBaseMapper<MdOkrCycle, String> getBaseMapper() {
		return mdOkrCycleMapper;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=false)
	public DefaultResult<MdOkrCycle> insert(MdOkrCycle entity) throws ServiceException {
		if (entity == null || !Strings.CS.equals(STATUS_DRAFT, entity.getStatus())) {
			throw new ServiceException("A new OKR cycle must be created in DRAFT status.");
		}
		return super.insert(entity);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=false)
	public DefaultResult<MdOkrCycle> update(MdOkrCycle entity) throws ServiceException {
		if (entity == null || StringUtils.isBlank(entity.getOid())) {
			throw new ServiceException("OKR cycle OID is required.");
		}
		MdOkrCycle stored = this.selectByEntityPrimaryKey(entity).getValueEmptyThrowMessage();
		if (!isAllowedTransition(stored.getStatus(), entity.getStatus())) {
			throw new ServiceException("Invalid OKR cycle status transition: "
					+ stored.getStatus() + " -> " + entity.getStatus() + ".");
		}
		return super.update(entity);
	}

	private boolean isAllowedTransition(String currentStatus, String nextStatus) {
		if (Strings.CS.equals(currentStatus, nextStatus)) {
			return true;
		}
		if (Strings.CS.equals(STATUS_DRAFT, currentStatus)) {
			return Strings.CS.equalsAny(nextStatus, STATUS_ACTIVE, STATUS_ARCHIVED);
		}
		if (Strings.CS.equals(STATUS_ACTIVE, currentStatus)) {
			return Strings.CS.equals(STATUS_CLOSED, nextStatus);
		}
		if (Strings.CS.equals(STATUS_CLOSED, currentStatus)) {
			return Strings.CS.equals(STATUS_ARCHIVED, nextStatus);
		}
		return false;
	}

}
