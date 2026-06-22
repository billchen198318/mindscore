package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdLlmProviderConfigKey implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private String oid;
    private String providerCode;

    @EntityPK(name = "oid", autoUUID = true)
    public String getOid() { return oid; }
    public void setOid(String oid) { this.oid = oid; }

    @EntityUK(name = "providerCode")
    public String getProviderCode() { return providerCode; }
    public void setProviderCode(String providerCode) { this.providerCode = providerCode; }
}
