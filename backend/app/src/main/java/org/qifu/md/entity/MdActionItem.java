package org.qifu.md.entity;

import java.math.BigDecimal;
import java.util.Date;
import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdActionItem extends MdActionItemKey {
	private static final long serialVersionUID = 1L;
	private String planOid;
	private String parentOid;
	private String itemName;
	private String actionStage;
	private String description;
	private Date startDate;
	private Date endDate;
	private Date doneDate;
	private BigDecimal progressValue;
	private String status;
	private Integer sortNo;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;

	public String getPlanOid() {
		return planOid;
	}
	public void setPlanOid(String planOid) {
		this.planOid = planOid;
	}

	public String getParentOid() {
		return parentOid;
	}
	public void setParentOid(String parentOid) {
		this.parentOid = parentOid;
	}

	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getActionStage() {
		return actionStage;
	}
	public void setActionStage(String actionStage) {
		this.actionStage = actionStage;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getDoneDate() {
		return doneDate;
	}
	public void setDoneDate(Date doneDate) {
		this.doneDate = doneDate;
	}

	public BigDecimal getProgressValue() {
		return progressValue;
	}
	public void setProgressValue(BigDecimal progressValue) {
		this.progressValue = progressValue;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getSortNo() {
		return sortNo;
	}
	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
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
