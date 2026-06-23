package org.qifu.md.model;

public class PasswordResetTokenStatus implements java.io.Serializable {
    private static final long serialVersionUID = -1901230902046418229L;

    private boolean valid;
    private String account;
    private String message;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
