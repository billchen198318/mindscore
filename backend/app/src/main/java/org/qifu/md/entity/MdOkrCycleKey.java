package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdOkrCycleKey implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String oid;
	private String cycleCode;
	
	@EntityPK(name = "oid", autoUUID = true)
	public String getOid() {
		return oid;
	}
	
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	@EntityUK(name = "cycle_code")
	public String getCycleCode() {
		return cycleCode;
	}
	
	public void setCycleCode(String cycleCode) {
		this.cycleCode = cycleCode;
	}
}