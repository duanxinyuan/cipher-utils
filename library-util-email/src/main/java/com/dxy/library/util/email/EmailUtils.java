package com.dxy.library.util.email;


import com.dxy.library.util.common.ExecutorUtils;
import com.dxy.library.util.common.StringUtils;
import com.dxy.library.util.email.callback.EmailCallback;
import com.dxy.library.util.email.dto.Email;
import com.dxy.library.util.email.mailinfo.MailAuthenticator;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

/**
 * 发送邮件的工具类
 * @author duanxinyuan
 * 2018/8/16 下午1:15
 */
@Slf4j
public class EmailUtils {

    //能存储1024封邮件
    private static ExecutorService executorService = ExecutorUtils.getExecutorService("EmailUtils", 3, 3, 1024, 60);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread("EmailUtils-shutdown-hook") {
            @Override
            public void run() {
                //防止kill时丢失邮件
                executorService.shutdown();
            }
        });
    }

    /**
     * 异步发送邮件
     */
    public static void sendAsync(Email email, EmailCallback callback) {
        executorService.execute(() -> {
            boolean send = send(email);
            callback.callback(email.getTo(), send);
        });
    }

    /**
     * 发送邮件
     */
    public static boolean send(Email email) {
        // 判断是否需要身份认证
        MailAuthenticator authenticator = null;
        Properties properties = getProperties(email);
        // 如果需要身份认证，则创建一个密码验证器
        if (StringUtils.isNotEmpty(email.getPassword())) {
            authenticator = new MailAuthenticator(email.getFrom(), email.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getInstance(properties, authenticator);
        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(email.getFrom());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            String[] split = email.getTo().split(",");
            List<Address> addresses = Lists.newArrayList();
            for (String s : split) {
                Address to = new InternetAddress(s);
                addresses.add(to);
            }
            // Message.RecipientType.TO属性表示接收者的类型为TO
            mailMessage.setRecipients(Message.RecipientType.TO, addresses.toArray(new Address[0]));
            // 设置邮件消息的主题
            mailMessage.setSubject(email.getTitle());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            if (email.isHtml()) {
                // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
                Multipart mainPart = new MimeMultipart();
                // 创建一个包含HTML内容的MimeBodyPart
                BodyPart html = new MimeBodyPart();
                // 设置HTML内容
                html.setContent(email.getContent(), "text/html; charset=utf-8");
                mainPart.addBodyPart(html);
                // 将MiniMultipart对象设置为邮件内容
                mailMessage.setContent(mainPart);
            } else {
                // 设置邮件消息的主要内容
                String mailContent = email.getContent();
                mailMessage.setText(mailContent);
            }
            // 发送邮件
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            log.error("send email error", ex);
        }
        return false;
    }

    private static Properties getProperties(Email email) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", email.getHost());
        properties.put("mail.smtp.port", email.getPort());
        properties.put("mail.smtp.auth", StringUtils.isNoneEmpty(email.getPassword()));
        return properties;
    }
}
