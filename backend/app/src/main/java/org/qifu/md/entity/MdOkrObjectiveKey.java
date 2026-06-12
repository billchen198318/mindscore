package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdOkrObjectiveKey implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String oid;
	private String cycleOid;
	private String objectiveCode;
	
	@EntityPK(name = "oid", autoUUID = true)
	public String getOid() {
		return oid;
	}
	
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	@EntityUK(name = "cycleOid")
	public String getCycleOid() {
		return cycleOid;
	}
	
	public void setCycleOid(String cycleOid) {
		this.cycleOid = cycleOid;
	}
	
	@EntityUK(name = "objectiveCode")
	public String getObjectiveCode() {
		return objectiveCode;
	}
	
	public void setObjectiveCode(String objectiveCode) {
		this.objectiveCode = objectiveCode;
	}
}