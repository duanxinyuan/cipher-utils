package com.dxy.common.util.email;

/**
 * @author duanxinyuan
 * 2018/8/16 下午1:05
 */
public interface EmailCallback {

    /**
     * 返回发送结果
     * @param receiver 接收者邮箱
     * @param isSuccess 是否成功
     */
    void callback(String receiver, boolean isSuccess);
}
