package org.hyperion.rs2.model;

import org.hyperion.rs2.util.PasswordEncryption;

public class Password {

    private String encryptedPass;

    private String salt;

    private String realPassword;

    private String tempPassword;

    public Password(final String password, final String salt) {
        setRealPassword(password);
        this.salt = salt;
    }

    public Password(final String password) {
        this(password, null);
    }

    public Password() {
        this(null, null);
    }

    public static String encryptPassword(final String password, final String salt) {
        return PasswordEncryption.sha1(password + salt);
    }

    public String getRealPassword() {
        return realPassword;
    }

    public void setRealPassword(final String password) {
        this.realPassword = password;
    }

    public String getEncryptedPass() {
        return encryptedPass;
    }

    public void setEncryptedPass(final String encrypted) {
        this.encryptedPass = encrypted;
        //System.out.println("Setting encrypted pass: " + encrypted);
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(final String salt) {
        this.salt = salt;
    }

    public String getTempPassword() {
        return tempPassword;
    }


    public void setTempPassword(final String tempPassword) {
        this.tempPassword = tempPassword;
    }

}
