package com.dxy.common.util.email;


import com.dxy.common.util.ExecutorUtil;
import com.dxy.common.util.StringUtils;
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

/**
 * 发送邮件的工具类
 * @author duanxinyuan
 * 2018/8/16 下午1:15
 */
@Slf4j
public class EmailUtil {

    public static void sendAsync(String host, String port, String from, String to, String password, String title, String content, EmailCallback callback) {
        sendAsync(host, port, from, to, password, title, content, false, callback);
    }

    public static void sendAsync(String host, String port, String from, List<String> receiveUsers, String password, String title, String content, EmailCallback callback) {
        String to = StringUtils.join(receiveUsers, ",");
        sendAsync(host, port, from, to, password, title, content, callback);
    }

    public static void sendAsync(String host, String port, String from, String[] receiveUsers, String password, String title, String content, EmailCallback callback) {
        String to = StringUtils.join(receiveUsers, ",");
        sendAsync(host, port, from, to, password, title, content, callback);
    }

    public static void sendAsync(String host, String port, String from, List<String> receiveUsers, String password, String title, String content, boolean isHtml, EmailCallback callback) {
        String to = StringUtils.join(receiveUsers, ",");
        sendAsync(host, port, from, to, password, title, content, isHtml, callback);
    }

    public static void sendAsync(String host, String port, String from, String[] receiveUsers, String password, String title, String content, boolean isHtml, EmailCallback callback) {
        String to = StringUtils.join(receiveUsers, ",");
        sendAsync(host, port, from, to, password, title, content, isHtml, callback);
    }

    public static void sendAsync(String host, String port, String from, String to, String password, String title, String content, boolean isHtml, EmailCallback callback) {
        ExecutorUtil.getInstance().execute(() -> {
            boolean send = send(host, port, from, to, password, title, content, isHtml);
            callback.callback(to, send);
        });
    }

    public static boolean send(String host, String port, String from, String to, String password, String title, String content) {
        return send(host, port, from, to, password, title, content, false);
    }

    public static void send(String host, String port, String from, List<String> receiveUsers, String password, String title, String content) {
        String to = StringUtils.join(receiveUsers, ",");
        send(host, port, from, to, password, title, content);
    }

    public static void send(String host, String port, String from, String[] receiveUsers, String password, String title, String content) {
        String to = StringUtils.join(receiveUsers, ",");
        send(host, port, from, to, password, title, content);
    }

    public static void send(String host, String port, String from, List<String> receiveUsers, String password, String title, String content, boolean isHtml) {
        String to = StringUtils.join(receiveUsers, ",");
        send(host, port, from, to, password, title, content, isHtml);
    }

    public static void send(String host, String port, String from, String[] receiveUsers, String password, String title, String content, boolean isHtml) {
        String to = StringUtils.join(receiveUsers, ",");
        send(host, port, from, to, password, title, content, isHtml);
    }

    /**
     * 发送邮件
     * @param host 邮箱Host
     * @param port 端口
     * @param from 发送者邮箱
     * @param to 接收者邮箱，多个以逗号隔开
     * @param password 发送者密码
     * @param title 邮件标题
     * @param content 邮件内容
     * @param isHtml 是否是HTML格式
     */
    public static boolean send(String host, String port, String from, String to, String password, String title, String content, boolean isHtml) {
        // 设置邮件服务器信息
        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost(host);
        mailInfo.setMailServerPort(port);
        mailInfo.setValidate(true);
        // 邮箱用户名
        mailInfo.setUserName(from);
        // 邮箱密码
        mailInfo.setPassword(password);
        // 发件人邮箱
        mailInfo.setFromAddress(from);
        // 收件人邮箱
        mailInfo.setToAddress(to);
        // 邮件标题
        mailInfo.setSubject(title);
        mailInfo.setContent(content);
        // 发送邮件
        return send(mailInfo, isHtml);
    }

    private static boolean send(MailSenderInfo mailInfo, boolean isHtml) {
        // 判断是否需要身份认证
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        // 如果需要身份认证，则创建一个密码验证器
        if (mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getInstance(pro, authenticator);
        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            String[] split = mailInfo.getToAddress().split(",");
            List<Address> addresses = Lists.newArrayList();
            for (String s : split) {
                Address to = new InternetAddress(s);
                addresses.add(to);
            }
            // Message.RecipientType.TO属性表示接收者的类型为TO
            mailMessage.setRecipients(Message.RecipientType.TO, addresses.toArray(new Address[0]));
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            if (isHtml) {
                // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
                Multipart mainPart = new MimeMultipart();
                // 创建一个包含HTML内容的MimeBodyPart
                BodyPart html = new MimeBodyPart();
                // 设置HTML内容
                html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
                mainPart.addBodyPart(html);
                // 将MiniMultipart对象设置为邮件内容
                mailMessage.setContent(mainPart);
            } else {
                // 设置邮件消息的主要内容
                String mailContent = mailInfo.getContent();
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
}
