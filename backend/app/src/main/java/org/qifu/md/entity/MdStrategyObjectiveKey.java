package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdStrategyObjectiveKey implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String oid;
	private String themeOid;
	private String objectiveCode;
	
	@EntityPK(name = "oid", autoUUID = true)
	public String getOid() {
		return oid;
	}
	
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	@EntityUK(name = "theme_oid")
	public String getThemeOid() {
		return themeOid;
	}
	
	public void setThemeOid(String themeOid) {
		this.themeOid = themeOid;
	}
	
	@EntityUK(name = "objective_code")
	public String getObjectiveCode() {
		return objectiveCode;
	}
	
	public void setObjectiveCode(String objectiveCode) {
		this.objectiveCode = objectiveCode;
	}
}