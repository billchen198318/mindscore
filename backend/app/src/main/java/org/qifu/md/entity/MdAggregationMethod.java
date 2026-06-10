package org.qifu.md.entity;

import java.util.Date;
import org.qifu.base.model.CreateDateField;
import org.qifu.base.model.CreateUserField;
import org.qifu.base.model.UpdateDateField;
import org.qifu.base.model.UpdateUserField;

public class MdAggregationMethod extends MdAggregationMethodKey {
	private static final long serialVersionUID = -3654129874561239854L;
	private String aggrName;
	private String aggrType;
	private String expression;
	private String description;
	private String enabled;
	private String cuserid;
	private Date cdate;
	private String uuserid;
	private Date udate;

	public String getAggrName() {
		return aggrName;
	}
	public void setAggrName(String aggrName) {
		this.aggrName = aggrName;
	}

	public String getAggrType() {
		return aggrType;
	}
	public void setAggrType(String aggrType) {
		this.aggrType = aggrType;
	}

	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
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
