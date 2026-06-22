package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;

public class MdLlmRunLogKey implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String oid;
    @EntityPK(name = "oid", autoUUID = true)
    public String getOid() { return oid; }
    public void setOid(String oid) { this.oid = oid; }
}
