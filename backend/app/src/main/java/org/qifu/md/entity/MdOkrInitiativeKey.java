package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdOkrInitiativeKey implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String oid;
	private String objectiveOid;
	private String initiativeCode;
	
	@EntityPK(name = "oid", autoUUID = true)
	public String getOid() {
		return oid;
	}
	
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	@EntityUK(name = "objectiveOid")
	public String getObjectiveOid() {
		return objectiveOid;
	}
	
	public void setObjectiveOid(String objectiveOid) {
		this.objectiveOid = objectiveOid;
	}
	
	@EntityUK(name = "initiativeCode")
	public String getInitiativeCode() {
		return initiativeCode;
	}
	
	public void setInitiativeCode(String initiativeCode) {
		this.initiativeCode = initiativeCode;
	}
}
