package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdInterpretationRuleKey implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String oid;
    private String ruleCode;

    @EntityPK(name = "oid", autoUUID = true)
    public String getOid() { return oid; }
    public void setOid(String oid) { this.oid = oid; }

    @EntityUK(name = "ruleCode")
    public String getRuleCode() { return ruleCode; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }
}