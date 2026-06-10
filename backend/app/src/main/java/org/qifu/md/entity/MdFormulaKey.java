package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdFormulaKey implements java.io.Serializable {
	private static final long serialVersionUID = -2420367980845325785L;
	private String oid;
	private String formulaCode;
	private Integer versionNo;

	@EntityPK(name = "oid", autoUUID = true)
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}

	@EntityUK(name = "formula_code")
	public String getFormulaCode() {
		return formulaCode;
	}
	public void setFormulaCode(String formulaCode) {
		this.formulaCode = formulaCode;
	}

	@EntityUK(name = "version_no")
	public Integer getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
}
