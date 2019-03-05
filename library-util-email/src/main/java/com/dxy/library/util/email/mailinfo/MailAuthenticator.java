package com.dxy.library.util.email.mailinfo;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @author duanxinyuan
 * 2018/8/16 下午1:14
 */
public class MailAuthenticator extends Authenticator {
    private String userName;
    private String password;

    public MailAuthenticator(String username, String password) {
        this.userName = username;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }

}
