package com.dxy.library.util.cipher.constant;

/**
 * 加密算法类型
 * 全部算法类型见官方文档：https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#AlgorithmParameterGenerator
 * @author duanxinyuan
 * 2019/2/19 21:13
 */
public enum Algorithm {

    //散列算法
    MD5("MD5"),
    SHA("SHA"),
    SHA1("SHA-1"),
    SHA224("SHA-224"),
    SHA256("SHA-256"),
    SHA384("SHA-384"),
    SHA512("SHA-512"),
    SHA3_224("SHA3-224"),
    SHA3_256("SHA3-256"),
    SHA3_384("SHA3-384"),
    SHA3_512("SHA3-512"),
    SM3("SM3"),

    //对称加密算法
    DES("DES"),
    DESede("DESede"),
    AES("AES"),
    Blowfish("Blowfish"),
    SM4("SM4"),
    RC4("RC4"),

    //非对称加密算法
    RSA("RSA"),

    //非对称加密算法
    SM2("SM2"),

    //EC加密算法（SM2，只在生成密钥对时使用）
    EC("EC"),

    ;

    String algorithm;

    Algorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 获取加密算法
     * @param algorithm 加密算法
     * @param mode 密码块工作模式
     * @param padding 填充方式
     * @return 加密算法全称
     */
    public static String getAlgorithm(Algorithm algorithm, Mode mode, Padding padding) {
        return algorithm.getAlgorithm() + "/" + mode.getMode() + "/" + padding.getPadding();
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

}
