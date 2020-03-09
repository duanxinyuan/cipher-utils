package com.dxy.library.util.cipher.constant;

/**
 * RSA数字签名类型
 * 详见{@link org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi}
 * 全部支持的算法见官方文档：https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Signature
 * @author duanxinyuan
 * 2018/10/15 16:20
 */
public enum RSASignType {

    NONEwithRSA("NONEwithRSA"),
    MD5withRSA("MD5withRSA"),

    SHA1withRSA("SHA1withRSA"),
    SHA224withRSA("SHA224withRSA"),
    SHA256withRSA("SHA256withRSA"),
    SHA384withRSA("SHA384withRSA"),
    SHA512withRSA("SHA512withRSA"),
    SHA512_224WithRSA("SHA512_224WithRSA"),
    SHA512_256WithRSA("SHA512_256WithRSA"),

    RIPEMD160WithRSA("RIPEMD160WithRSA"),
    WhirlpoolWithRSA("WhirlpoolWithRSA"),
    ;

    private String type;

    RSASignType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
