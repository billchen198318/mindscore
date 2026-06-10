package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdStrategyWorkspaceKey implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String oid;
	private String workspaceCode;
	
	@EntityPK(name = "oid", autoUUID = true)
	public String getOid() {
		return oid;
	}
	
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	@EntityUK(name = "workspace_code")
	public String getWorkspaceCode() {
		return workspaceCode;
	}
	
	public void setWorkspaceCode(String workspaceCode) {
		this.workspaceCode = workspaceCode;
	}
}