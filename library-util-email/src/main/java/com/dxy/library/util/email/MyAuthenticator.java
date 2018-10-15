package com.dxy.library.util.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @author duanxinyuan
 * 2018/8/16 下午1:14
 */
class MyAuthenticator extends Authenticator {
    private String userName;
    private String password;

    MyAuthenticator(String username, String password) {
        this.userName = username;
        this.password = password;
    }
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }
}
