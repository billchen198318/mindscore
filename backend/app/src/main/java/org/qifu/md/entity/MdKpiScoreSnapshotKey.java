package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdKpiScoreSnapshotKey implements java.io.Serializable {
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
	
	@EntityUK(name = "kpiOid")
	public String getKpiOid() {
		return kpiOid;
	}
	
	public void setKpiOid(String kpiOid) {
		this.kpiOid = kpiOid;
	}
	
	@EntityUK(name = "periodType")
	public String getPeriodType() {
		return periodType;
	}
	
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}
	
	@EntityUK(name = "periodKey")
	public String getPeriodKey() {
		return periodKey;
	}
	
	public void setPeriodKey(String periodKey) {
		this.periodKey = periodKey;
	}
	
	@EntityUK(name = "dataForType")
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
	
	@EntityUK(name = "orgOid")
	public String getOrgOid() {
		return orgOid;
	}
	
	public void setOrgOid(String orgOid) {
		this.orgOid = orgOid;
	}
}