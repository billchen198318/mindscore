package org.qifu.md.entity;

import java.util.Date;

import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdOkrInitiative extends MdOkrInitiativeKey {
	private static final long serialVersionUID = 1L;
	
	private String initiativeName;
	private String content;
	private Integer sortNo;
	private String status;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;
	
	public String getInitiativeName() {
		return initiativeName;
	}
	
	public void setInitiativeName(String initiativeName) {
		this.initiativeName = initiativeName;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public Integer getSortNo() {
		return sortNo;
	}
	
	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	@CreateUserField(name = "cuserid")
	public String getCuserid() {
		return cuserid;
	}
	
	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}
	
	@CreateDateField(name = "cdate")
	public Date getCdate() {
		return cdate;
	}
	
	public void setCdate(Date cdate) {
		this.cdate = cdate;
	}
	
	@UpdateUserField(name = "uuserid")
	public String getUuserid() {
		return uuserid;
	}
	
	public void setUuserid(String uuserid) {
		this.uuserid = uuserid;
	}
	
	@UpdateDateField(name = "udate")
	public Date getUdate() {
		return udate;
	}
	
	public void setUdate(Date udate) {
		this.udate = udate;
	}
}
