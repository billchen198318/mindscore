package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdStrategySnapshotKey implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String oid;
	private String workspaceOid;
	private String periodType;
	private String periodKey;
	
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
	
	@EntityUK(name = "period_type")
	public String getPeriodType() {
		return periodType;
	}
	
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}
	
	@EntityUK(name = "period_key")
	public String getPeriodKey() {
		return periodKey;
	}
	
	public void setPeriodKey(String periodKey) {
		this.periodKey = periodKey;
	}
}