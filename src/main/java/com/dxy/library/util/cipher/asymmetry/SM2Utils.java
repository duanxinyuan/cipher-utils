package com.dxy.library.util.cipher.asymmetry;

import com.dxy.library.util.cipher.constant.Algorithm;
import com.dxy.library.util.cipher.constant.SM2SignType;
import com.dxy.library.util.cipher.exception.CipherException;
import com.dxy.library.util.cipher.pojo.SM2KeyPair;
import com.dxy.library.util.cipher.utils.KeyEncodedUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * SM2工具类
 * SM2椭圆曲线公钥密码算法，是由国家密码管理局于2010年12月17日发布，一种商用密码分组标准对称算法，
 * 国家密码管理部门已经决定采用SM2椭圆曲线算法替换RSA算法
 * 是ECC（Elliptic Curve Cryptosystem）算法的一种，基于椭圆曲线离散对数问题，
 * 计算复杂度是指数级，求解难度较大，同等安全程度要求下，椭圆曲线密码较其他公钥所需密钥长度小很多
 * 密钥长256位，安全强度比RSA 2048位高，但运算速度快于RSA
 * 默认公钥X509格式，私钥PKCS8格式，OpenSSL的d2i_ECPrivateKey函数要求私钥是SEC1标准
 * SM2密文采用ASN.1/DER方式编码
 * 包括 -签名,验签 -密钥交换 -公钥加密,私钥解密
 * @author duanxinyuan
 * 2019/2/21 20:05
 */
public class SM2Utils {

    static {
        //导入Provider，BouncyCastle是一个开源的加解密解决方案，主页在http://www.bouncycastle.org/
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 公钥加密
     * @param data 加密内容
     * @param publicKey 公钥（X509格式，经过base64编码）
     * @return 密文（Base64编码）
     */
    public static String encrypt(String data, String publicKey) {
        return encrypt(data, getPublicKey(publicKey));
    }

    /**
     * 公钥加密
     * @param data 加密内容
     * @param publicKey 公钥（X509格式）
     * @return 密文
     */
    public static byte[] encrypt(byte[] data, byte[] publicKey) {
        return encrypt(data, getPublicKey(publicKey));
    }

    /**
     * 加密
     * @param data 加密内容
     * @param ecPublicKey 公钥
     * @return 密文（Base64编码）
     */
    public static String encrypt(String data, ECPublicKey ecPublicKey) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        return Base64.encodeBase64String(encrypt(data.getBytes(), ecPublicKey));
    }

    /**
     * 加密
     * @param data 加密内容
     * @param ecPublicKey 公钥
     * @return 密文
     */
    public static byte[] encrypt(byte[] data, ECPublicKey ecPublicKey) {
        try {
            Cipher cipher = Cipher.getInstance(Algorithm.SM2.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, ecPublicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new CipherException("EC encrypt error", e);
        }
    }

    /**
     * 私钥解密
     * @param data 密文（Base64编码）
     * @param privateKey 私钥（PKCS8格式，经过base64编码）
     * @return 明文
     */
    public static String decrypt(String data, String privateKey) {
        return decrypt(data, getPrivateKey(privateKey));
    }

    /**
     * 私钥解密
     * @param data 密文
     * @param privateKey 私钥（PKCS8格式）
     * @return 明文
     */
    public static byte[] decrypt(byte[] data, byte[] privateKey) {
        return decrypt(data, getPrivateKey(privateKey));
    }

    /**
     * 解密
     * @param data 密文（Base64编码）
     * @param ecPrivateKey 私钥
     * @return 明文
     */
    public static String decrypt(String data, ECPrivateKey ecPrivateKey) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        byte[] decrypt = decrypt(Base64.decodeBase64(data.getBytes()), ecPrivateKey);
        return new String(decrypt);
    }

    /**
     * 解密
     * @param data 密文
     * @param ecPrivateKey 私钥
     * @return 明文
     */
    public static byte[] decrypt(byte[] data, ECPrivateKey ecPrivateKey) {
        try {
            Cipher cipher = Cipher.getInstance(Algorithm.SM2.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, ecPrivateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new CipherException("EC decrypt error", e);
        }
    }

    /**
     * 用私钥对信息生成数字签名
     * @param signType 签名类型
     * @param data 签名的数据
     * @param privateKey 私钥（PKCS8格式，经过base64编码）
     * @return 签名（base64编码）
     */
    public static String sign(SM2SignType signType, String data, String privateKey) {
        return sign(signType, data.getBytes(), getPrivateKey(privateKey));
    }

    /**
     * 用私钥对信息生成数字签名
     * @param signType 签名类型
     * @param data 签名的数据
     * @param privateKey 私钥（PKCS8格式，经过base64编码）
     * @return 签名
     */
    public static String sign(SM2SignType signType, String data, ECPrivateKey privateKey) {
        return sign(signType, data.getBytes(), privateKey);
    }

    /**
     * 用私钥对信息生成数字签名
     * @param signType 签名类型
     * @param data 签名的数据
     * @param privateKey 私钥（PKCS8格式，经过base64编码）
     * @return 签名
     */
    public static String sign(SM2SignType signType, byte[] data, ECPrivateKey privateKey) {
        try {
            //用私钥对信息生成数字签名
            Signature signature = Signature.getInstance(signType.getType());
            signature.initSign(privateKey);
            signature.update(data);
            return Base64.encodeBase64String(signature.sign());
        } catch (Exception e) {
            throw new CipherException("EC sign error", e);
        }
    }

    /**
     * 用公钥校验数字签名
     * @param signType 签名类型
     * @param data 加密数据
     * @param publicKey 公钥
     * @param sign 签名（base64编码）
     * @return 验签结果，true表示验签通过
     */
    public static boolean verifySign(SM2SignType signType, String data, String publicKey, String sign) {
        return verifySign(signType, data.getBytes(), publicKey, Base64.decodeBase64(sign));
    }

    /**
     * 用公钥校验数字签名
     * @param signType 签名类型
     * @param data 加密数据
     * @param publicKey 公钥
     * @param sign 签名
     * @return 验签结果，true表示验签通过
     */
    public static boolean verifySign(SM2SignType signType, String data, ECPublicKey publicKey, String sign) {
        return verifySign(signType, data.getBytes(), publicKey, Base64.decodeBase64(sign));
    }

    /**
     * 用公钥校验数字签名
     * @param signType 签名类型
     * @param data 加密数据
     * @param publicKey 公钥
     * @param sign 签名
     * @return 验签结果，true表示验签通过
     */
    public static boolean verifySign(SM2SignType signType, byte[] data, String publicKey, byte[] sign) {
        return verifySign(signType, data, getPublicKey(publicKey), sign);
    }

    /**
     * 用公钥校验数字签名
     * @param signType 签名类型
     * @param data 加密数据
     * @param publicKey 公钥
     * @param sign 签名
     * @return 验签结果，true表示验签通过
     */
    public static boolean verifySign(SM2SignType signType, byte[] data, ECPublicKey publicKey, byte[] sign) {
        try {
            Signature signature = Signature.getInstance(signType.getType());
            signature.initVerify(publicKey);
            signature.update(data);
            //验证签名是否正常
            return signature.verify(sign);
        } catch (Exception e) {
            throw new CipherException("EC verify sign error", e);
        }
    }

    /**
     * 生成公钥和私钥
     */
    public static SM2KeyPair generateKey() {
        return generateKey(256);
    }

    /**
     * 生成公钥和私钥
     */
    public static SM2KeyPair generateKey(int keysize) {
        SecureRandom sr = new SecureRandom();
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance(Algorithm.EC.getAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            throw new CipherException("EC key generate error", e);
        }
        kpg.initialize(keysize, sr);

        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        ECPublicKey ecPublicKey = (ECPublicKey) keyPair.getPublic();
        //得到私钥
        ECPrivateKey ecPrivateKey = (ECPrivateKey) keyPair.getPrivate();

        SM2KeyPair sm2KeyPair = new SM2KeyPair();

        sm2KeyPair.setPublicKey(KeyEncodedUtils.encodeX509Base64(ecPublicKey.getEncoded()));
        sm2KeyPair.setPrivateKey(KeyEncodedUtils.encodePkcs8Base64(ecPrivateKey.getEncoded()));

        sm2KeyPair.setEcPublicKey(ecPublicKey);
        sm2KeyPair.setEcPrivateKey(ecPrivateKey);
        return sm2KeyPair;
    }

    /**
     * 获取公钥
     * @param publicKey 公钥（X509格式，经过base64编码）
     */
    public static ECPublicKey getPublicKey(String publicKey) {
        return getPublicKey(Base64.decodeBase64(publicKey.getBytes()));
    }

    /**
     * 获取公钥
     * @param key 公钥（X509格式）
     */
    public static ECPublicKey getPublicKey(byte[] key) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(Algorithm.EC.getAlgorithm());
            return (ECPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new CipherException(e);
        }
    }

    /**
     * 获取私钥（PKCS8格式）
     * @param privateKey 私钥（PKCS8格式，经过base64编码）
     */
    public static ECPrivateKey getPrivateKey(String privateKey) {
        return getPrivateKey(Base64.decodeBase64(privateKey.getBytes()));
    }

    /**
     * 获取私钥
     * @param key 私钥（PKCS8格式）
     */
    public static ECPrivateKey getPrivateKey(byte[] key) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(Algorithm.EC.getAlgorithm());
            return (ECPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new CipherException(e);
        }
    }

}
