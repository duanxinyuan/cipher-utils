package com.dxy.library.util.cipher.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 密钥编码工具类
 * @author duanxinyuan
 * 2019/12/30 18:41
 */
public class KeyEncodedUtils {

    /**
     * PKCS8编码
     * @param bytes 被编码的内容
     */
    public static String encodePkcs8Base64(byte[] bytes) {
        return Base64.encodeBase64String(encodePkcs8(bytes));
    }

    /**
     * PKCS8编码
     * @param bytes 被编码的内容
     */
    public static byte[] encodePkcs8(byte[] bytes) {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        return keySpec.getEncoded();
    }

    /**
     * X509编码
     * @param bytes 被编码的内容
     */
    public static String encodeX509Base64(byte[] bytes) {
        return Base64.encodeBase64String(encodeX509(bytes));
    }

    /**
     * X509编码
     * @param bytes 被编码的内容
     */
    public static byte[] encodeX509(byte[] bytes) {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
        return keySpec.getEncoded();
    }

}
