package com.dxy.library.util.cipher.symmetry;

import com.dxy.library.util.cipher.constant.Algorithm;
import com.dxy.library.util.cipher.constant.AlgorithmUtils;
import com.dxy.library.util.cipher.constant.Mode;
import com.dxy.library.util.cipher.constant.Padding;
import com.dxy.library.util.common.StringUtils;
import org.apache.commons.codec.binary.Base64;
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
 * 三重DES工具类
 * DESede：密钥长 112/168，块长64，速度很慢，中等安全, 适合加密较小的数据
 * @author duanxinyuan
 * 2019/2/15 19:14
 */
public class DESedeUtils {
    static {
        //导入Provider，BouncyCastle是一个开源的加解密解决方案，主页在http://www.bouncycastle.org/
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * DESede加密（最常用方式之一，使用DESede/ECB/PKCS5Padding方式，无偏移量）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是24位
     */
    public static String encrypt(String data, String key) {
        return encrypt(data, key, null, Mode.ECB, Padding.PKCS5Padding);
    }

    /**
     * DESede加密（最常用方式之一，使用DESede/ECB/PKCS5Padding方式，无偏移量）
     * @param data 密文
     * @param key 密钥，长度必须是24位
     */
    public static byte[] encrypt(byte[] data, byte[] key) {
        return encrypt(data, key, null, Mode.ECB, Padding.PKCS5Padding);
    }

    /**
     * DESede加密（最常用方式之一，使用DESede/CBC/PKCS7Padding方式）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是24位
     * @param iv 偏移量，长度必须是8位
     */
    public static String encrypt(String data, String key, String iv) {
        return encrypt(data, key, iv, Mode.CBC, Padding.PKCS7Padding);
    }

    /**
     * DESede加密（最常用方式之一，使用DESede/CBC/PKCS7Padding方式）
     * @param data 密文
     * @param key 密钥，长度必须是24位
     * @param iv 偏移量，长度必须是8位
     */
    public static byte[] encrypt(byte[] data, byte[] key, String iv) {
        return encrypt(data, key, iv, Mode.CBC, Padding.PKCS7Padding);
    }

    /**
     * DESede加密（不带偏移量）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是24位
     */
    public static String encrypt(String data, String key, Mode mode, Padding padding) {
        return encrypt(data, key, null, mode, padding);
    }

    /**
     * DESede加密（不带偏移量）
     * @param data 密文
     * @param key 密钥，长度必须是24位
     * @return 密文（Base64编码）
     */
    public static byte[] encrypt(byte[] data, byte[] key, Mode mode, Padding padding) {
        return encrypt(data, key, null, mode, padding);
    }

    /**
     * DESede加密
     * @param data 明文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是24位
     * @param iv 偏移量，长度必须是8位
     * @param mode 密码块工作模式
     * @param padding 填充方式
     * @return 密文（Base64编码）
     */
    public static String encrypt(String data, String key, String iv, Mode mode, Padding padding) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        byte[] encrypt = encrypt(data.getBytes(), Base64.decodeBase64(key), iv, mode, padding);
        return Base64.encodeBase64String(encrypt);
    }

    /**
     * DESede加密
     * @param data 明文
     * @param key 密钥，长度必须是24位
     * @param iv 偏移量，长度必须是8位
     * @param mode 密码块工作模式
     * @param padding 填充方式
     * @return 密文
     */
    public static byte[] encrypt(byte[] data, byte[] key, String iv, Mode mode, Padding padding) {
        check(key, iv, mode, padding);
        try {
            SecretKeySpec secretKeySpec = getSecretKeySpec(key);
            String algorithm = AlgorithmUtils.getAlgorithm(Algorithm.DESede, mode, padding);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(algorithm);
            // 初始化
            if (StringUtils.isNotEmpty(iv)) {
                AlgorithmParameters parameters = AlgorithmParameters.getInstance(Algorithm.DESede.getAlgorithm());
                parameters.init(new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameters);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            }
            //加密
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("DESede encrypt error", e);
        }
    }

    /**
     * DESede解密（最常用方式之一，使用DESede/ECB/PKCS5Padding方式，无偏移量）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是24位
     * @return 明文
     */
    public static String decrypt(String data, String key) {
        return decrypt(data, key, null, Mode.ECB, Padding.PKCS5Padding);
    }

    /**
     * DESede解密（最常用方式之一，使用DESede/ECB/PKCS5Padding方式，无偏移量）
     * @param data 密文
     * @param key 密钥，长度必须是24位
     * @return 明文
     */
    public static byte[] decrypt(byte[] data, byte[] key) {
        return decrypt(data, key, null, Mode.ECB, Padding.PKCS5Padding);
    }

    /**
     * DESede解密（最常用方式之一，使用DESede/CBC/PKCS7Padding方式）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是24位
     * @param iv 偏移量，长度必须是8位
     * @return 明文
     */
    public static String decrypt(String data, String key, String iv) {
        return decrypt(data, key, iv, Mode.CBC, Padding.PKCS7Padding);
    }

    /**
     * DESede解密（最常用方式之一，使用DESede/CBC/PKCS7Padding方式）
     * @param data 密文
     * @param key 密钥，长度必须是24位
     * @param iv 偏移量，长度必须是8位
     * @return 明文
     */
    public static byte[] decrypt(byte[] data, byte[] key, String iv) {
        return decrypt(data, key, iv, Mode.CBC, Padding.PKCS7Padding);
    }

    /**
     * DESede解密（不带偏移量）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是24位
     * @return 明文
     */
    public static String decrypt(String data, String key, Mode mode, Padding padding) {
        return decrypt(data, key, null, mode, padding);
    }

    /**
     * DESede解密（不带偏移量）
     * @param data 密文
     * @param key 密钥，长度必须是24位
     * @return 明文
     */
    public static byte[] decrypt(byte[] data, byte[] key, Mode mode, Padding padding) {
        return decrypt(data, key, null, mode, padding);
    }

    /**
     * DESede解密
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是24位
     * @param iv 偏移量，长度必须是8位
     * @param mode 密码块工作模式
     * @param padding 填充方式
     * @return 明文
     */
    public static String decrypt(String data, String key, String iv, Mode mode, Padding padding) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        byte[] decrypt = decrypt(Base64.decodeBase64(data), Base64.decodeBase64(key), iv, mode, padding);
        return new String(decrypt);
    }

    /**
     * DESede解密
     * @param data 密文
     * @param key 密钥，长度必须是24位
     * @param iv 偏移量，长度必须是8位
     * @param mode 密码块工作模式
     * @param padding 填充方式
     * @return 明文
     */
    public static byte[] decrypt(byte[] data, byte[] key, String iv, Mode mode, Padding padding) {
        check(key, iv, mode, padding);
        try {
            SecretKeySpec secretKeySpec = getSecretKeySpec(key);
            String algorithm = AlgorithmUtils.getAlgorithm(Algorithm.DESede, mode, padding);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(algorithm);
            // 初始化
            if (StringUtils.isNotEmpty(iv)) {
                AlgorithmParameters parameters = AlgorithmParameters.getInstance(Algorithm.DESede.getAlgorithm());
                parameters.init(new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameters);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            }
            //解密
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("DESede decrypt error", e);
        }
    }

    /**
     * 生成DESede的Key（168位，Base64编码）
     * @return 密钥（Base64编码）
     */
    public static String genarateKeyBase64() {
        return genarateKeyBase64(168);
    }

    /**
     * 生成DESede的Key（168位，Base64编码）
     * @return 密钥（Base64编码）
     */
    public static String genarateKeyBase64(int length) {
        return Base64.encodeBase64String(genarateKey(length));
    }

    /**
     * 生成DESede的Key，长度可以选112、168，生成的密钥长度为24位
     * @return 密钥
     */
    public static byte[] genarateKey(int length) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(Algorithm.DESede.getAlgorithm());
            keyGenerator.init(length);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("DESede key genarate error", e);
        }
    }

    private static SecretKeySpec getSecretKeySpec(byte[] key) {
        return new SecretKeySpec(key, Algorithm.DESede.getAlgorithm());
    }

    private static void check(byte[] key, String iv, Mode mode, Padding padding) {
        checkKey(key);
        checkModeAndPadding(mode, padding);
        if (StringUtils.isNotEmpty(iv)) {
            checkIv(iv);
            if (mode == Mode.ECB) {
                throw new RuntimeException("DESede ECB mode does not use an IV");
            }
        }
    }

    /**
     * 校验DESede密码块工作模式和填充模式
     */
    private static void checkModeAndPadding(Mode mode, Padding padding) {
        if (mode == Mode.NONE) {
            throw new RuntimeException("invalid DESede mode");
        }
        if (padding == Padding.SSL3Padding || padding == Padding.PKCS1Padding) {
            throw new RuntimeException("invalid DESede padding");
        }
        if (padding == Padding.NoPadding) {
            if (mode == Mode.ECB || mode == Mode.CBC) {
                throw new RuntimeException("invalid DESede algorithm");
            }
        }
        if (padding == Padding.PKCS7Padding) {
        }
    }

    /**
     * 校验DESede密钥，长度必须是24位
     */
    private static void checkKey(byte[] key) {
        if (key == null) {
            throw new RuntimeException("DESede key cannot be null");
        }
        if (key.length != 24) {
            throw new RuntimeException("DESede key not 16/24 bytes long");
        }
    }

    /**
     * 校验DESede偏移量，长度必须是8位
     */
    private static void checkIv(String iv) {
        if (iv.length() != 8) {
            throw new RuntimeException("DESede iv not 8 bytes long");
        }
    }

}
