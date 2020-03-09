package com.dxy.library.util.cipher.constant;

/**
 * 填充(Padding)
 * 是对需要按块处理的数据，当数据长度不符合块处理需求时，按照一定方法填充满块长的一种规则
 * JDK的PKCS5Padding实际是上述的PKCS7的实现
 * 如果加密为PKCS5Padding，解密可以选择NoPadding，也能解密成功，内容为原文加上PKCS5Padding之后的结果
 * AES加密时，缺省模式和填充为“AES/ECB/PKCS5Padding”，Cipher.getInstance(“AES”)与Cipher.getInstance(“AES/ECB/PKCS5Padding”)等效
 * AES加密时，在CBC、ECB和PCBC三种模式下不能使用，不改变密文长度
 * @author duanxinyuan
 * 2019/2/19 20:57
 */
public enum Padding {

    //全填充0x00
    NoPadding("NoPadding"),

    //用于RSA算法
    PKCS1Padding("PKCS1Padding"),

    //固定填充8个字符，也就是0x08
    PKCS5Padding("PKCS5Padding"),

    //可以填充1到255字节，填充1个字符就全0x01，填充2个字符就全0x02，不需要填充就增加一个块，填充块长度，块长为8就填充0x08，块长为16就填充0x10
    PKCS7Padding("PKCS7Padding"),

    ISO10126Padding("ISO10126Padding"),

    //对称加密不支持
    OAEPPadding("OAEPPadding"),

    //对称加密不支持
    ISO9796d1Padding("ISO9796d1Padding"),

    //对称加密不支持
    SSL3Padding("SSL3Padding"),
    ;

    String padding;

    Padding(String padding) {
        this.padding = padding;
    }

    public String getPadding() {
        return padding;
    }

    public void setPadding(String padding) {
        this.padding = padding;
    }

}
