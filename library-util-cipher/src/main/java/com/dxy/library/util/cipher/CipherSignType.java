package com.dxy.library.util.cipher;

/**
 * 加密签名类型
 * @author duanxinyuan
 * 2018/10/15 16:20
 */
public enum CipherSignType {
    SHA1WITHRSA("SHA1withRSA", "SHA1withRSA数字签名的方式"),
    SHA256WITHRSA("SHA256withRSA", "SHA256withRSA数字签名的方式");

    private String type;
    private String desc;

    CipherSignType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
