package org.qifu.md.entity;

import java.util.Date;

import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdOkrObjectiveOwner extends MdOkrObjectiveOwnerKey {
	private static final long serialVersionUID = 1L;
	
	private String objectiveOid;
	private String ownerType;
	private String account;
	private String orgOid;
	private String ownerRole;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;
	
	public String getObjectiveOid() {
		return objectiveOid;
	}
	
	public void setObjectiveOid(String objectiveOid) {
		this.objectiveOid = objectiveOid;
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