package com.dxy.library.util.email.test;

import com.dxy.library.util.email.EmailUtils;
import com.dxy.library.util.email.dto.Email;
import org.junit.Test;

/**
 * @author duanxinyuan
 * 2018/8/16 下午1:17
 */
public class EmailTest {

    @Test
    public void send() {
        Email email = Email.builder()
                .host("test.com")
                .port(25)
                .from("test@test.com")
                .to("testto@test.com")
                .password("password")
                .title("title")
                .content("content")
                .isHtml(false).build();
        //同步发送
        boolean send = EmailUtils.send(email);
        System.out.println(send);
        //异步发送
        EmailUtils.sendAsync(email, ((receiver, isSuccess) -> {
            System.out.println(receiver);
            System.out.println(isSuccess);
        }));
    }

}
