package com.dxy.library.util.cipher.symmetry;

import com.dxy.library.util.cipher.constant.Algorithm;
import com.dxy.library.util.cipher.constant.Mode;
import com.dxy.library.util.cipher.constant.Padding;
import com.dxy.library.util.cipher.exception.CipherException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * SM4工具类
 * 无线局域网标准的分组数据算法，密钥长 128，块长128，类似AES
 * @author duanxinyuan
 * 2019/2/25 15:47
 */
public class SM4Utils {

    static {
        //导入Provider，BouncyCastle是一个开源的加解密解决方案，主页在http://www.bouncycastle.org/
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * SM4加密（最常用方式之一，使用SM4/ECB/PKCS5Padding方式，无偏移量）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是16位
     */
    public static String encrypt(String data, String key) {
        return encrypt(data, key, null, Mode.ECB, Padding.PKCS5Padding);
    }

    /**
     * SM4加密（最常用方式之一，使用SM4/ECB/PKCS5Padding方式，无偏移量）
     * @param data 密文
     * @param key 密钥，长度必须是16位
     */
    public static byte[] encrypt(byte[] data, byte[] key) {
        return encrypt(data, key, null, Mode.ECB, Padding.PKCS5Padding);
    }

    /**
     * SM4加密（最常用方式之一，使用SM4/CBC/PKCS7Padding方式）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是16位
     * @param iv 偏移量，长度必须为16位
     */
    public static String encrypt(String data, String key, String iv) {
        return encrypt(data, key, iv, Mode.CBC, Padding.PKCS7Padding);
    }

    /**
     * SM4加密（最常用方式之一，使用SM4/CBC/PKCS7Padding方式）
     * @param data 密文
     * @param key 密钥，长度必须是16位
     * @param iv 偏移量，长度必须为16位
     */
    public static byte[] encrypt(byte[] data, byte[] key, String iv) {
        return encrypt(data, key, iv, Mode.CBC, Padding.PKCS7Padding);
    }

    /**
     * SM4加密（不带偏移量）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是16位
     */
    public static String encrypt(String data, String key, Mode mode, Padding padding) {
        return encrypt(data, key, null, mode, padding);
    }

    /**
     * SM4加密（不带偏移量）
     * @param data 密文
     * @param key 密钥，长度必须是16位
     * @return 密文（Base64编码）
     */
    public static byte[] encrypt(byte[] data, byte[] key, Mode mode, Padding padding) {
        return encrypt(data, key, null, mode, padding);
    }

    /**
     * SM4加密
     * @param data 明文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是16位
     * @param iv 偏移量，长度必须为16位
     * @param mode 密码块工作模式
     * @param padding 填充方式
     * @return 密文（Base64编码）
     */
    public static String encrypt(String data, String key, String iv, Mode mode, Padding padding) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        byte[] encrypt = encrypt(data.getBytes(), key.getBytes(), iv, mode, padding);
        return Base64.encodeBase64String(encrypt);
    }

    /**
     * SM4加密
     * @param data 明文
     * @param key 密钥，长度必须是16位
     * @param iv 偏移量，长度必须为16位
     * @param mode 密码块工作模式
     * @param padding 填充方式
     * @return 密文
     */
    public static byte[] encrypt(byte[] data, byte[] key, String iv, Mode mode, Padding padding) {
        check(data, key, iv, mode, padding);
        try {
            SecretKeySpec secretKeySpec = getSecretKeySpec(key);
            String algorithm = Algorithm.getAlgorithm(Algorithm.SM4, mode, padding);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(algorithm);
            // 初始化
            if (StringUtils.isNotEmpty(iv)) {
                AlgorithmParameters parameters = AlgorithmParameters.getInstance(Algorithm.SM4.getAlgorithm());
                parameters.init(new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameters);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            }
            //加密
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new CipherException("SM4 encrypt error", e);
        }
    }

    /**
     * SM4解密（最常用方式之一，使用SM4/ECB/PKCS5Padding方式，无偏移量）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是16位
     * @return 明文
     */
    public static String decrypt(String data, String key) {
        return decrypt(data, key, null, Mode.ECB, Padding.PKCS5Padding);
    }

    /**
     * SM4解密（最常用方式之一，使用SM4/ECB/PKCS5Padding方式，无偏移量）
     * @param data 密文
     * @param key 密钥，长度必须是16位
     * @return 明文
     */
    public static byte[] decrypt(byte[] data, byte[] key) {
        return decrypt(data, key, null, Mode.ECB, Padding.PKCS5Padding);
    }

    /**
     * SM4解密（最常用方式之一，使用SM4/CBC/PKCS7Padding方式）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是16位
     * @param iv 偏移量，长度必须为16位
     * @return 明文
     */
    public static String decrypt(String data, String key, String iv) {
        return decrypt(data, key, iv, Mode.CBC, Padding.PKCS7Padding);
    }

    /**
     * SM4解密（最常用方式之一，使用SM4/CBC/PKCS7Padding方式）
     * @param data 密文
     * @param key 密钥，长度必须是16位
     * @param iv 偏移量，长度必须为16位
     * @return 明文
     */
    public static byte[] decrypt(byte[] data, byte[] key, String iv) {
        return decrypt(data, key, iv, Mode.CBC, Padding.PKCS7Padding);
    }

    /**
     * SM4解密（不带偏移量）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是16位
     * @return 明文
     */
    public static String decrypt(String data, String key, Mode mode, Padding padding) {
        return decrypt(data, key, null, mode, padding);
    }

    /**
     * SM4解密（不带偏移量）
     * @param data 密文
     * @param key 密钥，长度必须是16位
     * @return 明文
     */
    public static byte[] decrypt(byte[] data, byte[] key, Mode mode, Padding padding) {
        return decrypt(data, key, null, mode, padding);
    }

    /**
     * SM4解密
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是16位
     * @param iv 偏移量，长度必须为16位
     * @param mode 密码块工作模式
     * @param padding 填充方式
     * @return 明文
     */
    public static String decrypt(String data, String key, String iv, Mode mode, Padding padding) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        byte[] decrypt = decrypt(Base64.decodeBase64(data), key.getBytes(), iv, mode, padding);
        return new String(decrypt);
    }

    /**
     * SM4解密
     * @param data 密文
     * @param key 密钥，长度必须是16位
     * @param iv 偏移量，长度必须为16位
     * @param mode 密码块工作模式
     * @param padding 填充方式
     * @return 明文
     */
    public static byte[] decrypt(byte[] data, byte[] key, String iv, Mode mode, Padding padding) {
        check(data, key, iv, mode, padding);
        try {
            SecretKeySpec secretKeySpec = getSecretKeySpec(key);
            String algorithm = Algorithm.getAlgorithm(Algorithm.SM4, mode, padding);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(algorithm);
            // 初始化
            if (StringUtils.isNotEmpty(iv)) {
                AlgorithmParameters parameters = AlgorithmParameters.getInstance(Algorithm.SM4.getAlgorithm());
                parameters.init(new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameters);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            }
            //解密
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new CipherException("SM4 decrypt error", e);
        }
    }

    /**
     * 生成SM4的Key，密钥长度为 128
     * @return 密钥
     */
    public static byte[] generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(Algorithm.SM4.getAlgorithm());
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new CipherException("SM4 key generate error", e);
        }
    }

    private static SecretKeySpec getSecretKeySpec(byte[] key) {
        return new SecretKeySpec(key, Algorithm.SM4.getAlgorithm());
    }

    private static void check(byte[] data, byte[] key, String iv, Mode mode, Padding padding) {
        checkKey(key);
        checkModeAndPadding(data, mode, padding);
        if (StringUtils.isNotEmpty(iv)) {
            checkIv(iv);
            if (mode == Mode.ECB) {
                throw new CipherException("SM4 ECB mode does not use an IV");
            }
        }
    }

    /**
     * 校验SM4密码块工作模式和填充模式
     */
    private static void checkModeAndPadding(byte[] data, Mode mode, Padding padding) {
        if (mode == Mode.NONE) {
            throw new CipherException("invalid SM4 mode");
        }
        if (padding == Padding.SSL3Padding || padding == Padding.PKCS1Padding) {
            throw new CipherException("invalid SM4 padding");
        }
        boolean is16NotSupport = padding == Padding.NoPadding && (mode == Mode.ECB || mode == Mode.CBC) && data.length % 16 != 0;
        if (is16NotSupport) {
            throw new CipherException("data length must be multiple of 16 bytes on ECB/NoPadding or CBC/NoPadding mode");
        }
    }

    /**
     * 校验SM4密钥，长度必须是16位
     */
    private static void checkKey(byte[] key) {
        if (key == null) {
            throw new CipherException("SM4 key cannot be null");
        }
        if (key.length != 16) {
            throw new CipherException("SM4 key not 16 bytes long");
        }
    }

    /**
     * 校验SM4偏移量，长度必须是16位
     */
    private static void checkIv(String iv) {
        if (iv.length() != 16) {
            throw new CipherException("SM4 iv not 16 bytes long");
        }
    }

}
