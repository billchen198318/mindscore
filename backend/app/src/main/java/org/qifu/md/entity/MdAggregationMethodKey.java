package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdAggregationMethodKey implements java.io.Serializable {
	private static final long serialVersionUID = 4153987123654125897L;
	private String oid;
	private String aggrCode;

	@EntityPK(name = "oid", autoUUID = true)
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}

	@EntityUK(name = "aggrCode")
	public String getAggrCode() {
		return aggrCode;
	}
	public void setAggrCode(String aggrCode) {
		this.aggrCode = aggrCode;
	}
}
