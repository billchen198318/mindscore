package org.qifu.md.service.impl;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.mapper.IBaseMapper;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.YesNoKeyProvide;
import org.qifu.base.model.ZeroKeyProvide;
import org.qifu.base.service.BaseService;
import org.qifu.md.entity.MdOrgUnit;
import org.qifu.md.mapper.MdOrgUnitMapper;
import org.qifu.md.service.IMdOrgUnitService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service
@Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=true)
public class MdOrgUnitServiceImpl extends BaseService<MdOrgUnit, String> implements IMdOrgUnitService<MdOrgUnit, String> {
	
	private final MdOrgUnitMapper mdOrgUnitMapper;

	public MdOrgUnitServiceImpl(MdOrgUnitMapper mdOrgUnitMapper) {
		super();
		this.mdOrgUnitMapper = mdOrgUnitMapper;
	}

	@Override
	protected IBaseMapper<MdOrgUnit, String> getBaseMapper() {
		return this.mdOrgUnitMapper;
	}

    @Override
    public List<MdOrgUnit> findTree() throws ServiceException {
        try {
            DefaultResult<List<MdOrgUnit>> result = this.selectListByParams(new java.util.HashMap<String, Object>());
            return result.getValue();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    @Transactional(propagation=Propagation.REQUIRED, timeout=300, readOnly=false)
    @Override
    public DefaultResult<Boolean> move(String oid, String newParentOid) throws ServiceException {
        DefaultResult<Boolean> result = new DefaultResult<Boolean>();
        result.setSuccess(YesNoKeyProvide.NO);
        try {
            MdOrgUnit target = this.selectByPrimaryKey(oid).getValue();
            if (target == null) {
                result.setMessage("組織不存在");
                return result;
            }

            // 若為根節點，newParentOid 應視為 null
            if (StringUtils.isBlank(newParentOid) || "root".equals(newParentOid) || ZeroKeyProvide.OID_KEY.equals(newParentOid)) {
                newParentOid = null;
            }
            
            // 防止拖曳到自己
            if (target.getOid().equals(newParentOid)) {
                result.setMessage("不能移動到自己");
                return result;
            }
            
            // 防止拖曳到自己的子節點中
            if (newParentOid != null && isDescendant(target.getOid(), newParentOid)) {
                result.setMessage("不能移動到自己的子節點中");
                return result;
            }

            int oldLevel = target.getOrgLevel();
            int newLevel = (newParentOid == null) ? 1 : this.selectByPrimaryKey(newParentOid).getValue().getOrgLevel() + 1;
            int levelDelta = newLevel - oldLevel;

            target.setParentOid(newParentOid);
            target.setOrgLevel(newLevel);
            this.update(target);

            // 遞迴更新所有子節點
            updateDescendantsLevel(target.getOid(), levelDelta);

            result.setSuccess(YesNoKeyProvide.YES);
            result.setMessage("移動成功");
            result.setValue(true);
            return result;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private boolean isDescendant(String targetOid, String potentialParentOid) throws ServiceException {
        if (targetOid.equals(potentialParentOid)) return true;
        
        DefaultResult<MdOrgUnit> parentResult = this.selectByPrimaryKey(potentialParentOid);
        MdOrgUnit parent = (parentResult != null) ? parentResult.getValue() : null;
        
        if (parent == null || StringUtils.isBlank(parent.getParentOid())) return false;
        
        return isDescendant(targetOid, parent.getParentOid());
    }

    private void updateDescendantsLevel(String parentOid, int delta) throws ServiceException {
        java.util.Map<String, Object> param = new java.util.HashMap<String, Object>();
        param.put("parentOid", parentOid);
        List<MdOrgUnit> children = this.mdOrgUnitMapper.selectListByParams(param);
        
        if (children == null || children.isEmpty()) return;

        for (MdOrgUnit child : children) {
            child.setOrgLevel(child.getOrgLevel() + delta);
            this.update(child);
            updateDescendantsLevel(child.getOid(), delta);
        }
    }
}
