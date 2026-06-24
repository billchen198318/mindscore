package org.qifu.md.model;

public class DirectPasswordChangeRequest implements java.io.Serializable {
    private static final long serialVersionUID = -8311530036141454284L;

    private String oid;
    private String password;
    private String confirmPassword;

    public String getOid() { return oid; }
    public void setOid(String oid) { this.oid = oid; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}