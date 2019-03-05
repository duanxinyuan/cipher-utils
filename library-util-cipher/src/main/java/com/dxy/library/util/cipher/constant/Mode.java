package com.dxy.library.util.cipher.constant;

/**
 * 密码块工作模式(Block cipher mode of operation)
 * 是对于按块处理密码的加密方式的一种扩充，不仅仅适用于AES，包括DES, RSA等加密方法同样适用
 * 1、NoPadding填充情况下，CBC、ECB和PCBC三种模式是不支持的
 * 2、CFB、OFB两种模式下，加密数据长度等于原始数据长度
 * 3、PCBC是CBC的扩种，较少使用，故而没有引入
 * @author duanxinyuan
 * 2019/2/19 20:52
 */
public enum Mode {

    //无模式，对称加密不支持
    NONE("NONE"),

    //电子密码本，每块独立加密，有点：分块可以并行处理，缺点：同样的原文得到相同的密文，容易被攻击
    ECB("ECB"),

    //密码分组链接，每块加密依赖于前一块的密文，优点：同样的原文得到不同的密文、原文微小改动影响后面全部密文，缺点：加密需要串行处理，误差传递
    CBC("CBC"),

//    //填充密码块链接，CBC的扩种，较少使用，优点：同样的原文得到不同的密文，互换两个邻接的密文块不会对后续块的解密造成影响，缺点：加密需要串行处理
//    PCBC("PCBC"),

    //密文反馈
    CFB("CFB"),

    //输出反馈模式，加密后密文与原文异或XOR，缺点：能够对密文进行校验
    OFB("OFB"),

    //计数器模式，增加一个序列函数对所有密文快做XOR
    CTR("CTR");

    String mode;

    Mode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

}
