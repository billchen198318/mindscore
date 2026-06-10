package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdStrategyThemeKey implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String oid;
	private String workspaceOid;
	private String themeCode;
	
	@EntityPK(name = "oid", autoUUID = true)
	public String getOid() {
		return oid;
	}
	
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	@EntityUK(name = "workspace_oid")
	public String getWorkspaceOid() {
		return workspaceOid;
	}
	
	public void setWorkspaceOid(String workspaceOid) {
		this.workspaceOid = workspaceOid;
	}
	
	@EntityUK(name = "theme_code")
	public String getThemeCode() {
		return themeCode;
	}
	
	public void setThemeCode(String themeCode) {
		this.themeCode = themeCode;
	}
}