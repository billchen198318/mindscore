package org.qifu.md.entity;

import org.qifu.base.model.EntityPK;
import org.qifu.base.model.EntityUK;

public class MdKpiScoreColorKey implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String oid;
    private String scopeType;
    private String scopeKey;
    private String colorCode;

    @EntityPK(name = "oid", autoUUID = true)
    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    @EntityUK(name = "scopeType")
    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    @EntityUK(name = "scopeKey")
    public String getScopeKey() {
        return scopeKey;
    }

    public void setScopeKey(String scopeKey) {
        this.scopeKey = scopeKey;
    }

    @EntityUK(name = "colorCode")
    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
