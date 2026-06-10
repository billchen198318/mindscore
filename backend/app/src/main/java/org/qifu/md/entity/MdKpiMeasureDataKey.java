package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdKpiMeasureDataKey implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String oid;
	private String kpiOid;
	private String periodType;
	private String periodKey;
	private String dataForType;
	private String account;
	private String orgOid;
	
	@EntityPK(name = "oid", autoUUID = true)
	public String getOid() {
		return oid;
	}
	
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	@EntityUK(name = "kpi_oid")
	public String getKpiOid() {
		return kpiOid;
	}
	
	public void setKpiOid(String kpiOid) {
		this.kpiOid = kpiOid;
	}
	
	@EntityUK(name = "period_type")
	public String getPeriodType() {
		return periodType;
	}
	
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}
	
	@EntityUK(name = "period_key")
	public String getPeriodKey() {
		return periodKey;
	}
	
	public void setPeriodKey(String periodKey) {
		this.periodKey = periodKey;
	}
	
	@EntityUK(name = "data_for_type")
	public String getDataForType() {
		return dataForType;
	}
	
	public void setDataForType(String dataForType) {
		this.dataForType = dataForType;
	}
	
	@EntityUK(name = "account")
	public String getAccount() {
		return account;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
	
	@EntityUK(name = "org_oid")
	public String getOrgOid() {
		return orgOid;
	}
	
	public void setOrgOid(String orgOid) {
		this.orgOid = orgOid;
	}
}