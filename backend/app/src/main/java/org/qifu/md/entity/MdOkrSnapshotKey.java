package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdOkrSnapshotKey implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String oid;
	private String objectiveOid;
	private String periodKey;
	
	@EntityPK(name = "oid", autoUUID = true)
	public String getOid() {
		return oid;
	}
	
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	@EntityUK(name = "objective_oid")
	public String getObjectiveOid() {
		return objectiveOid;
	}
	
	public void setObjectiveOid(String objectiveOid) {
		this.objectiveOid = objectiveOid;
	}
	
	@EntityUK(name = "period_key")
	public String getPeriodKey() {
		return periodKey;
	}
	
	public void setPeriodKey(String periodKey) {
		this.periodKey = periodKey;
	}
}