package com.dxy.library.util.cipher.hash;

import com.dxy.library.util.cipher.constant.HmacType;
import com.dxy.library.util.common.StringUtils;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Hmac工具类
 * @author duanxinyuan
 * 2019/2/26 22:54
 */
public class HmacUtils {

    /**
     * HmacMD5加密
     */
    public static String hmacMD5(String data, String key) {
        return hmac(data, key, HmacType.HmacMD5);
    }

    /**
     * HmacMD5加密
     */
    public static byte[] hmacMD5(byte[] data, byte[] key) {
        return hmac(data, key, HmacType.HmacMD5);
    }

    /**
     * HmacSHA1加密
     */
    public static String hmacSHA1(String data, String key) {
        return hmac(data, key, HmacType.HmacSHA1);
    }

    /**
     * HmacSHA1加密
     */
    public static byte[] hmacSHA1(byte[] data, byte[] key) {
        return hmac(data, key, HmacType.HmacSHA1);
    }

    /**
     * HmacSHA224加密
     */
    public static String hmacSHA224(String data, String key) {
        return hmac(data, key, HmacType.HmacSHA224);
    }

    /**
     * HmacSHA224加密
     */
    public static byte[] hmacSHA224(byte[] data, byte[] key) {
        return hmac(data, key, HmacType.HmacSHA224);
    }

    /**
     * HmacSHA256加密
     */
    public static String hmacSHA256(String data, String key) {
        return hmac(data, key, HmacType.HmacSHA256);
    }

    /**
     * HmacSHA256加密
     */
    public static byte[] hmacSHA256(byte[] data, byte[] key) {
        return hmac(data, key, HmacType.HmacSHA256);
    }

    /**
     * HmacSHA384加密
     */
    public static String hmacSHA384(String data, String key) {
        return hmac(data, key, HmacType.HmacSHA384);
    }

    /**
     * HmacSHA384加密
     */
    public static byte[] hmacSHA384(byte[] data, byte[] key) {
        return hmac(data, key, HmacType.HmacSHA384);
    }

    /**
     * HmacSHA512加密
     */
    public static String hmacSHA512(String data, String key) {
        return hmac(data, key, HmacType.HmacSHA512);
    }

    /**
     * HmacSHA512加密
     */
    public static byte[] hmacSHA512(byte[] data, byte[] key) {
        return hmac(data, key, HmacType.HmacSHA512);
    }

    /**
     * HMAC加密
     * @param data 加密内容
     * @param key 密钥
     * @param hmacType Hmac类型
     * @return 密文（16进制）
     */
    public static String hmac(String data, String key, HmacType hmacType) {
        byte[] bytes = hmac(data.getBytes(), StringUtils.parseHexToBytes(key), hmacType);
        return StringUtils.toHex(bytes);
    }

    /**
     * HMAC加密
     * @param data 加密内容
     * @param key 密钥
     * @param hmacType Hmac类型
     * @return 密文
     */
    public static byte[] hmac(byte[] data, byte[] key, HmacType hmacType) {
        try {
            Mac mac = Mac.getInstance(hmacType.getType());
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, hmacType.getType());
            mac.init(secretKeySpec);
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("hmac error", e);
        }
    }

    /**
     * 构建HmacMD5密钥
     * @return 密钥（16进制）
     */
    public static String genarateHmacMD5KeyHex() {
        return genarateKeyHex(HmacType.HmacMD5);
    }

    /**
     * 构建HmacMD5密钥
     * @return 密钥
     */
    public static byte[] genarateHmacMD5Key() {
        return genarateKey(HmacType.HmacMD5);
    }

    /**
     * 构建HmacSHA1密钥
     * @return 密钥（16进制）
     */
    public static String genarateHmacSHA1KeyHex() {
        return genarateKeyHex(HmacType.HmacSHA1);
    }

    /**
     * 构建HmacSHA1密钥
     * @return 密钥
     */
    public static byte[] genarateHmacSHA1Key() {
        return genarateKey(HmacType.HmacSHA1);
    }

    /**
     * 构建HmacSHA224密钥
     * @return 密钥（16进制）
     */
    public static String genarateHmacSHA224KeyHex() {
        return genarateKeyHex(HmacType.HmacSHA224);
    }

    /**
     * 构建HmacSHA224密钥
     * @return 密钥
     */
    public static byte[] genarateHmacSHA224Key() {
        return genarateKey(HmacType.HmacSHA224);
    }

    /**
     * 构建HmacSHA256密钥
     * @return 密钥（16进制）
     */
    public static String genarateHmacSHA256KeyHex() {
        return genarateKeyHex(HmacType.HmacSHA256);
    }

    /**
     * 构建HmacSHA256密钥
     * @return 密钥
     */
    public static byte[] genarateHmacSHA256Key() {
        return genarateKey(HmacType.HmacSHA256);
    }

    /**
     * 构建HmacSHA384密钥
     * @return 密钥（16进制）
     */
    public static String genarateHmacSHA384KeyHex() {
        return genarateKeyHex(HmacType.HmacSHA384);
    }

    /**
     * 构建HmacSHA384密钥
     * @return 密钥
     */
    public static byte[] genarateHmacSHA384Key() {
        return genarateKey(HmacType.HmacSHA384);
    }

    /**
     * 构建HmacSHA512密钥
     * @return 密钥（16进制）
     */
    public static String genarateHmacSHA512KeyHex() {
        return genarateKeyHex(HmacType.HmacSHA512);
    }

    /**
     * 构建HmacSHA512密钥
     * @return 密钥
     */
    public static byte[] genarateHmacSHA512Key() {
        return genarateKey(HmacType.HmacSHA512);
    }

    /**
     * 构建Hmac密钥
     * @param hmacType Hmac类型
     * @return 密钥（16进制）
     */
    public static String genarateKeyHex(HmacType hmacType) {
        byte[] key = genarateKey(hmacType);
        return StringUtils.toHex(key);
    }

    /**
     * 构建Hmac密钥
     * @param hmacType Hmac类型
     * @return 密钥
     */
    public static byte[] genarateKey(HmacType hmacType) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(hmacType.getType());
            // 产生密钥
            SecretKey secretKey = keyGenerator.generateKey();
            // 获得密钥
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
