package com.dxy.library.util.cipher.constant;

/**
 * 数字签名类型
 * 全部支持的算法见官方文档：https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Signature
 * @author duanxinyuan
 * 2018/10/15 16:20
 */
public enum SignType {

    NONEwithRSA("NONEwithRSA"),

    MD5withRSA("MD5withRSA"),

    SHA1withRSA("SHA1withRSA"),
    SHA224withRSA("SHA224withRSA"),
    SHA256withRSA("SHA256withRSA"),
    SHA384withRSA("SHA384withRSA"),
    SHA512withRSA("SHA512withRSA"),

    SHA1withSM2("SHA1withSM2"),
    SHA256withSM2("SHA256withSM2"),
    ;

    private String type;

    SignType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
