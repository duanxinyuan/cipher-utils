package com.dxy.library.util.cipher.hash;

import com.dxy.library.util.cipher.constant.Algorithm;
import com.dxy.library.util.cipher.exception.CipherException;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 * Secure Hash Algorithm（Secure Hash Algorithm）
 * @author duanxinyuan
 * 2019/2/15 18:28
 */
public class MD5Utils {

    /**
     * MD5加密
     * @param data 加密内容
     * @return 密文（16进制）
     */
    public static String md5(String data) {
        byte[] bytes = md5(data.getBytes());
        return Hex.encodeHexString(bytes);
    }

    /**
     * MD5加密
     * @param data 加密内容
     * @return 密文
     */
    public static byte[] md5(byte[] data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(Algorithm.MD5.getAlgorithm());
            return messageDigest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("MD5 error", e);
        }
    }

    /**
     * MD5加密（加盐，多加密一次）
     * @param data 加密内容
     * @param salt 盐
     * @return 密文（16进制）
     */
    public static String md5(String data, String salt) {
        byte[] bytes = md5WithSalt(data.getBytes(), salt.getBytes(), 1);
        return Hex.encodeHexString(bytes);
    }


    /**
     * MD5加密（加盐，多加密一次）
     * @param data 加密内容
     * @param salt 盐
     * @return 密文
     */
    public static byte[] md5(byte[] data, byte[] salt) {
        return md5WithSalt(data, salt, 1);
    }

    /**
     * MD5加密（加盐，多加密hashCount次）
     * @param data 加密内容
     * @param salt 盐
     * @param hashCount 哈希次数
     * @return 密文（16进制）
     */
    public static String md5(String data, String salt, int hashCount) {
        byte[] bytes = md5WithSalt(data.getBytes(), salt.getBytes(), hashCount);
        return Hex.encodeHexString(bytes);
    }


    /**
     * MD5加密（加盐，多加密hashCount次）
     * @param data 加密内容
     * @param salt 盐
     * @param hashCount 哈希次数
     * @return 密文
     */
    public static byte[] md5(byte[] data, byte[] salt, int hashCount) {
        return md5WithSalt(data, salt, hashCount);
    }

    /**
     * MD5加密（加盐）
     * @param data 加密内容
     * @param salt 盐
     * @param hashCount 哈希次数
     * @return 密文
     */
    private static byte[] md5WithSalt(byte[] data, byte[] salt, int hashCount) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(Algorithm.MD5.getAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            throw new CipherException("MD5 error", e);
        }
        if (salt != null) {
            messageDigest.reset();
            messageDigest.update(salt);
        }

        byte[] bytes = messageDigest.digest(data);
        int iterations = hashCount - 1;

        for (int i = 0; i < iterations; ++i) {
            messageDigest.reset();
            bytes = messageDigest.digest(bytes);
        }

        return bytes;
    }
}
