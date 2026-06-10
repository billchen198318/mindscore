package org.qifu.md.entity;

import java.util.Date;
import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdActionSourceLink extends MdActionSourceLinkKey {
	private static final long serialVersionUID = 1L;
	private String actionType;
	private String actionOid;
	private String sourceType;
	private String sourceOid;
	private String linkReason;
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

	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getSourceOid() {
		return sourceOid;
	}
	public void setSourceOid(String sourceOid) {
		this.sourceOid = sourceOid;
	}

	public String getLinkReason() {
		return linkReason;
	}
	public void setLinkReason(String linkReason) {
		this.linkReason = linkReason;
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
