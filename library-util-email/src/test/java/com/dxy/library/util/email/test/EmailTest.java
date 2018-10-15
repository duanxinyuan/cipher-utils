package com.dxy.library.util.email.test;

import com.dxy.library.util.email.EmailUtils;
import org.junit.Test;

/**
 * @author duanxinyuan
 * 2018/8/16 下午1:17
 */
public class EmailTest {

    @Test
    public void send() {
        boolean send = EmailUtils.send("test.com", "25", "test@test.com", "testto@test.com", "password", "title", "content");
        System.out.println(send);
    }
}
