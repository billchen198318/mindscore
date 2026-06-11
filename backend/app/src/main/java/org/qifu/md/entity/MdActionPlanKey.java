package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdActionPlanKey implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String oid;
	private String planCode;

	@EntityPK(name = "oid", autoUUID = true)
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}

	@EntityUK(name = "planCode")
	public String getPlanCode() {
		return planCode;
	}
	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}
}
