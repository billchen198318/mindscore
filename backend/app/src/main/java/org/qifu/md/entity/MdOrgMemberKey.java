package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdOrgMemberKey implements java.io.Serializable {
	private static final long serialVersionUID = -2420367980845325785L;
	private String oid;
	private String orgOid;
	private String account;

	@EntityPK(name = "oid", autoUUID = true)
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}

	@EntityUK(name = "org_oid")
	public String getOrgOid() {
		return orgOid;
	}
	public void setOrgOid(String orgOid) {
		this.orgOid = orgOid;
	}

	@EntityUK(name = "account")
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
}
