package com.dxy.library.util.email.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author duanxinyuan
 * 2019/1/22 20:29
 */
@Builder
@Data
public class Email {

    //邮件服Host
    private String host;

    //邮件服端口
    private Integer port;

    //发送者邮箱
    private String from;

    //接收者邮箱，多个以逗号隔开
    private String to;

    //发送者邮箱密码
    private String password;

    //标题
    private String title;

    //内容
    private String content;

    //是否是HTML格式
    private boolean isHtml;

}
