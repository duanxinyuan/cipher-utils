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
 * AES工具类
 * 高级加密标准(Advanced Encryption Standard)，密钥长可以选 128, 192, 256，块长128，用来替代原先的DES，速度快，安全
 * 优点：AES具有比DES更好的安全性、效率、灵活性，在软件及硬件上都能快速地加解密，相对来说较易于实作，且只需要很少的存储器。
 * @author duanxinyuan
 * 2019/2/15 18:41
 */
public class AESUtils {

    static {
        //导入Provider，BouncyCastle是一个开源的加解密解决方案，主页在http://www.bouncycastle.org/
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * AES加密（最常用方式之一，使用AES/ECB/PKCS5Padding方式，无偏移量）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是16或24或者32位
     */
    public static String encrypt(String data, String key) {
        return encrypt(data, key, null, Mode.ECB, Padding.PKCS5Padding);
    }

    /**
     * AES加密（最常用方式之一，使用AES/ECB/PKCS5Padding方式，无偏移量）
     * @param data 密文
     * @param key 密钥，长度必须是16或24或者32位
     */
    public static byte[] encrypt(byte[] data, byte[] key) {
        return encrypt(data, key, null, Mode.ECB, Padding.PKCS5Padding);
    }

    /**
     * AES加密（最常用方式之一，使用AES/CBC/PKCS7Padding方式）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是16或24或者32位
     * @param iv 偏移量，长度必须为16位
     */
    public static String encrypt(String data, String key, String iv) {
        return encrypt(data, key, iv, Mode.CBC, Padding.PKCS7Padding);
    }

    /**
     * AES加密（最常用方式之一，使用AES/CBC/PKCS7Padding方式）
     * @param data 密文
     * @param key 密钥，长度必须是16或24或者32位
     * @param iv 偏移量，长度必须为16位
     */
    public static byte[] encrypt(byte[] data, byte[] key, String iv) {
        return encrypt(data, key, iv, Mode.CBC, Padding.PKCS7Padding);
    }

    /**
     * AES加密（不带偏移量）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是16或24或者32位
     */
    public static String encrypt(String data, String key, Mode mode, Padding padding) {
        return encrypt(data, key, null, mode, padding);
    }

    /**
     * AES加密（不带偏移量）
     * @param data 密文
     * @param key 密钥，长度必须是16或24或者32位
     * @return 密文（Base64编码）
     */
    public static byte[] encrypt(byte[] data, byte[] key, Mode mode, Padding padding) {
        return encrypt(data, key, null, mode, padding);
    }

    /**
     * AES加密
     * @param data 明文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是16或24或者32位
     * @param iv 偏移量，长度必须为16位
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
     * AES加密
     * @param data 明文
     * @param key 密钥，长度必须是16或24或者32位
     * @param iv 偏移量，长度必须为16位
     * @param mode 密码块工作模式
     * @param padding 填充方式
     * @return 密文
     */
    public static byte[] encrypt(byte[] data, byte[] key, String iv, Mode mode, Padding padding) {
        check(key, iv, mode, padding);
        try {
            SecretKeySpec secretKeySpec = getSecretKeySpec(key);
            String algorithm = AlgorithmUtils.getAlgorithm(Algorithm.AES, mode, padding);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(algorithm);
            // 初始化
            if (StringUtils.isNotEmpty(iv)) {
                AlgorithmParameters parameters = AlgorithmParameters.getInstance(Algorithm.AES.getAlgorithm());
                parameters.init(new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameters);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            }
            //加密
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("AES encrypt error", e);
        }
    }

    /**
     * AES解密（最常用方式之一，使用AES/ECB/PKCS5Padding方式，无偏移量）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是16或24或者32位
     * @return 明文
     */
    public static String decrypt(String data, String key) {
        return decrypt(data, key, null, Mode.ECB, Padding.PKCS5Padding);
    }

    /**
     * AES解密（最常用方式之一，使用AES/ECB/PKCS5Padding方式，无偏移量）
     * @param data 密文
     * @param key 密钥，长度必须是16或24或者32位
     * @return 明文
     */
    public static byte[] decrypt(byte[] data, byte[] key) {
        return decrypt(data, key, null, Mode.ECB, Padding.PKCS5Padding);
    }

    /**
     * AES解密（最常用方式之一，使用AES/CBC/PKCS7Padding方式）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是16或24或者32位
     * @param iv 偏移量，长度必须为16位
     * @return 明文
     */
    public static String decrypt(String data, String key, String iv) {
        return decrypt(data, key, iv, Mode.CBC, Padding.PKCS7Padding);
    }

    /**
     * AES解密（最常用方式之一，使用AES/CBC/PKCS7Padding方式）
     * @param data 密文
     * @param key 密钥，长度必须是16或24或者32位
     * @param iv 偏移量，长度必须为16位
     * @return 明文
     */
    public static byte[] decrypt(byte[] data, byte[] key, String iv) {
        return decrypt(data, key, iv, Mode.CBC, Padding.PKCS7Padding);
    }

    /**
     * AES解密（不带偏移量）
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是16或24或者32位
     * @return 明文
     */
    public static String decrypt(String data, String key, Mode mode, Padding padding) {
        return decrypt(data, key, null, mode, padding);
    }

    /**
     * AES解密（不带偏移量）
     * @param data 密文
     * @param key 密钥，长度必须是16或24或者32位
     * @return 明文
     */
    public static byte[] decrypt(byte[] data, byte[] key, Mode mode, Padding padding) {
        return decrypt(data, key, null, mode, padding);
    }

    /**
     * AES解密
     * @param data 密文（Base64编码）
     * @param key 密钥（Base64编码），长度必须是16或24或者32位
     * @param iv 偏移量，长度必须为16位
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
     * AES解密
     * @param data 密文
     * @param key 密钥，长度必须是16或24或者32位
     * @param iv 偏移量，长度必须为16位
     * @param mode 密码块工作模式
     * @param padding 填充方式
     * @return 明文
     */
    public static byte[] decrypt(byte[] data, byte[] key, String iv, Mode mode, Padding padding) {
        check(key, iv, mode, padding);
        try {
            SecretKeySpec secretKeySpec = getSecretKeySpec(key);
            String algorithm = AlgorithmUtils.getAlgorithm(Algorithm.AES, mode, padding);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(algorithm);
            // 初始化
            if (StringUtils.isNotEmpty(iv)) {
                AlgorithmParameters parameters = AlgorithmParameters.getInstance(Algorithm.AES.getAlgorithm());
                parameters.init(new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameters);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            }
            //解密
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("AES decrypt error", e);
        }
    }

    /**
     * 生成AES的Key（128位，Base64编码）
     * @return 密钥（Base64编码）
     */
    public static String genarateKeyBase64() {
        return genarateKeyBase64(128);
    }

    /**
     * 生成AES的Key（Base64编码）
     * @return 密钥（Base64编码）
     */
    public static String genarateKeyBase64(int length) {
        return Base64.encodeBase64String(genarateKey(length));
    }

    /**
     * 生成AES的Key
     * @param length 密钥长度，可以选 128, 192, 256
     * @return 密钥
     */
    public static byte[] genarateKey(int length) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(Algorithm.AES.getAlgorithm());
            keyGenerator.init(length);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES key genarate error", e);
        }
    }

    private static SecretKeySpec getSecretKeySpec(byte[] key) {
        return new SecretKeySpec(key, Algorithm.AES.getAlgorithm());
    }

    private static void check(byte[] key, String iv, Mode mode, Padding padding) {
        checkKey(key);
        checkModeAndPadding(mode, padding);
        if (StringUtils.isNotEmpty(iv)) {
            checkIv(iv);
            if (mode == Mode.ECB) {
                throw new RuntimeException("AES ECB mode does not use an IV");
            }
        }
    }

    /**
     * 校验AES密码块工作模式和填充模式
     */
    private static void checkModeAndPadding(Mode mode, Padding padding) {
        if (mode == Mode.NONE) {
            throw new RuntimeException("invalid AES mode");
        }
        if (padding == Padding.SSL3Padding || padding == Padding.PKCS1Padding) {
            throw new RuntimeException("invalid AES padding");
        }
        if (padding == Padding.NoPadding) {
            if (mode == Mode.ECB || mode == Mode.CBC) {
                throw new RuntimeException("invalid AES algorithm");
            }
        }
    }

    /**
     * 校验AES密钥，长度必须是16或24或者32位
     */
    private static void checkKey(byte[] key) {
        if (key == null) {
            throw new RuntimeException("AES key cannot be null");
        }
        if (key.length != 16 && key.length != 24 && key.length != 32) {
            throw new RuntimeException("AES key not 16/24/32 bytes long");
        }
    }

    /**
     * 校验AES偏移量，长度必须是16位
     */
    private static void checkIv(String iv) {
        if (iv.length() != 16) {
            throw new RuntimeException("AES iv not 16 bytes long");
        }
    }

}
