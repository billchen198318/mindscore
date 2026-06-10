package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdOrgUnitKey implements java.io.Serializable {
	private static final long serialVersionUID = 5519808381830635956L;
	private String oid;
	private String orgCode;

	@EntityPK(name = "oid", autoUUID = true)
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}

	@EntityUK(name = "org_code")
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
}
