package org.qifu.md.entity;

import java.util.Date;
import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdOrgMember extends MdOrgMemberKey {
	private static final long serialVersionUID = -1516142759902640286L;
	private String displayName;
	private String jobTitle;
	private String isManager;
	private String enabled;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;

	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getIsManager() {
		return isManager;
	}
	public void setIsManager(String isManager) {
		this.isManager = isManager;
	}

	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
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
