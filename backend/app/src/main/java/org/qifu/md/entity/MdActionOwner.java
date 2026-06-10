package org.qifu.md.entity;

import java.util.Date;
import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdActionOwner extends MdActionOwnerKey {
	private static final long serialVersionUID = 1L;
	private String actionType;
	private String actionOid;
	private String ownerType;
	private String account;
	private String orgOid;
	private String ownerRole;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;

	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getActionOid() {
		return actionOid;
	}
	public void setActionOid(String actionOid) {
		this.actionOid = actionOid;
	}

	public String getOwnerType() {
		return ownerType;
	}
	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}

	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}

	public String getOrgOid() {
		return orgOid;
	}
	public void setOrgOid(String orgOid) {
		this.orgOid = orgOid;
	}

	public String getOwnerRole() {
		return ownerRole;
	}
	public void setOwnerRole(String ownerRole) {
		this.ownerRole = ownerRole;
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
