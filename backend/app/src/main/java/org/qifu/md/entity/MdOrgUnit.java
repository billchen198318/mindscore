package org.qifu.md.entity;

import java.util.Date;
import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdOrgUnit extends MdOrgUnitKey {
	private static final long serialVersionUID = 6736283597813292415L;
	private String orgName;
	private String parentOid;
	private Integer orgLevel;
	private Integer sortNo;
	private String enabled;
	private String description;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;

	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getParentOid() {
		return parentOid;
	}
	public void setParentOid(String parentOid) {
		this.parentOid = parentOid;
	}

	public Integer getOrgLevel() {
		return orgLevel;
	}
	public void setOrgLevel(Integer orgLevel) {
		this.orgLevel = orgLevel;
	}

	public Integer getSortNo() {
		return sortNo;
	}
	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}

	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@CreateUserField(name = "cuserid")
	public String getCuserid() {
		return cuserid;
	}
	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}

	@CreateDateField(name = "cdate")
	public Date getCdate() {
		return cdate;
	}
	public void setCdate(Date cdate) {
		this.cdate = cdate;
	}

	@UpdateUserField(name = "uuserid")
	public String getUuserid() {
		return uuserid;
	}
	public void setUuserid(String uuserid) {
		this.uuserid = uuserid;
	}

	@UpdateDateField(name = "udate")
	public Date getUdate() {
		return udate;
	}
	public void setUdate(Date udate) {
		this.udate = udate;
	}
}
