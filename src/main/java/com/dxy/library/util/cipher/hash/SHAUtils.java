package com.dxy.library.util.cipher.hash;

import com.dxy.library.util.cipher.constant.Algorithm;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA工具类
 * Secure Hash Algorithm（Secure Hash Algorithm）
 * @author duanxinyuan
 * 2019/2/15 18:32
 */
public class SHAUtils {

    /**
     * SHA加密
     */
    public static String sha(String data) {
        return sha(data, Algorithm.SHA);
    }

    /**
     * SHA加密
     */
    public static byte[] sha(byte[] data) {
        return sha(data, Algorithm.SHA);
    }

    /**
     * SHA1加密
     */
    public static String sha1(String data) {
        return sha(data, Algorithm.SHA1);
    }

    /**
     * SHA1加密
     */
    public static byte[] sha1(byte[] data) {
        return sha(data, Algorithm.SHA1);
    }

    /**
     * SHA224加密
     */
    public static String sha224(String data) {
        return sha(data, Algorithm.SHA224);
    }

    /**
     * SHA224加密
     */
    public static byte[] sha224(byte[] data) {
        return sha(data, Algorithm.SHA224);
    }

    /**
     * SHA256加密
     */
    public static String sha256(String data) {
        return sha(data, Algorithm.SHA256);
    }

    /**
     * SHA256加密
     */
    public static byte[] sha256(byte[] data) {
        return sha(data, Algorithm.SHA256);
    }

    /**
     * SHA384加密
     */
    public static String sha384(String data) {
        return sha(data, Algorithm.SHA384);
    }

    /**
     * SHA384加密
     */
    public static byte[] sha384(byte[] data) {
        return sha(data, Algorithm.SHA384);
    }

    /**
     * SHA512加密
     */
    public static String sha512(String data) {
        return sha(data, Algorithm.SHA512);
    }

    /**
     * SHA512加密
     */
    public static byte[] sha512(byte[] data) {
        return sha(data, Algorithm.SHA512);
    }

    /**
     * SHA3-224加密（JDK9以上）
     */
    public static String sha3_224(String data) {
        return sha(data, Algorithm.SHA3_224);
    }

    /**
     * SHA3-224加密（JDK9以上）
     */
    public static byte[] sha3_224(byte[] data) {
        return sha(data, Algorithm.SHA3_224);
    }

    /**
     * SHA3-256加密（JDK9以上）
     */
    public static String sha3_256(String data) {
        return sha(data, Algorithm.SHA3_256);
    }

    /**
     * SHA3-256加密（JDK9以上）
     */
    public static byte[] sha3_256(byte[] data) {
        return sha(data, Algorithm.SHA3_256);
    }

    /**
     * SHA3-384加密（JDK9以上）
     */
    public static String sha3_384(String data) {
        return sha(data, Algorithm.SHA3_384);
    }

    /**
     * SHA3-384加密（JDK9以上）
     */
    public static byte[] sha3_384(byte[] data) {
        return sha(data, Algorithm.SHA3_384);
    }

    /**
     * SHA3-512加密（JDK9以上）
     */
    public static String sha3_512(String data) {
        return sha(data, Algorithm.SHA3_512);
    }

    /**
     * SHA3-512加密（JDK9以上）
     */
    public static byte[] sha3_512(byte[] data) {
        return sha(data, Algorithm.SHA3_512);
    }

    /**
     * 字符串 SHA 加密
     * @param data 加密内容
     * @param algorithm 算法类型
     * @return 密文（16进制）
     */
    public static String sha(String data, Algorithm algorithm) {
        byte[] bytes = sha(data.getBytes(), algorithm);
        return Hex.encodeHexString(bytes);
    }

    /**
     * 字符串 SHA 加密
     * @param data 加密内容
     * @param algorithm 算法类型
     * @return 密文
     */
    public static byte[] sha(byte[] data, Algorithm algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm.getAlgorithm());
            return digest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("SHA error", e);
        }
    }

}
