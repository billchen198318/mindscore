package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdOkrKeyResultKey implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String oid;
	private String objectiveOid;
	private String krCode;
	
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
	
	@EntityUK(name = "krCode")
	public String getKrCode() {
		return krCode;
	}
	
	public void setKrCode(String krCode) {
		this.krCode = krCode;
	}
}