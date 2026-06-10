package org.qifu.md.entity;

import java.util.Date;

import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdStrategyWorkspace extends MdStrategyWorkspaceKey {
	private static final long serialVersionUID = 1L;
	
	private String workspaceName;
	private String visionText;
	private String missionText;
	private String description;
	private String status;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;
	
	public String getWorkspaceName() {
		return workspaceName;
	}
	
	public void setWorkspaceName(String workspaceName) {
		this.workspaceName = workspaceName;
	}
	
	public String getVisionText() {
		return visionText;
	}
	
	public void setVisionText(String visionText) {
		this.visionText = visionText;
	}
	
	public String getMissionText() {
		return missionText;
	}
	
	public void setMissionText(String missionText) {
		this.missionText = missionText;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
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