package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdStrategyObjectiveLinkKey implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String oid;
	private String strategyObjectiveOid;
	private String linkType;
	private String linkOid;
	
	@EntityPK(name = "oid", autoUUID = true)
	public String getOid() {
		return oid;
	}
	
	public void setOid(String oid) {
		this.oid = oid;
	}
	
	@EntityUK(name = "strategyObjectiveOid")
	public String getStrategyObjectiveOid() {
		return strategyObjectiveOid;
	}
	
	public void setStrategyObjectiveOid(String strategyObjectiveOid) {
		this.strategyObjectiveOid = strategyObjectiveOid;
	}
	
	@EntityUK(name = "linkType")
	public String getLinkType() {
		return linkType;
	}
	
	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}
	
	@EntityUK(name = "linkOid")
	public String getLinkOid() {
		return linkOid;
	}
	
	public void setLinkOid(String linkOid) {
		this.linkOid = linkOid;
	}
}