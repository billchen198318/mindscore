package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdKpiKey implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String oid;
	private String kpiCode;
	
	@EntityPK(name = "oid", autoUUID = true)
	public String getOid() {
		return oid;
	}
	
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	@EntityUK(name = "kpi_code")
	public String getKpiCode() {
		return kpiCode;
	}
	
	public void setKpiCode(String kpiCode) {
		this.kpiCode = kpiCode;
	}
}