package com.dxy.library.util.cipher;

import com.dxy.library.util.common.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;

/**
 * 加密解密工具类
 * @author duanxinyuan
 * 2017/9/6 19:20
 */
public class CipherUtils {
    /**
     * SHA加密的方式
     */
    private static String SHA = "SHA";
    private static String SHA1 = "SHA-1";
    private static String SHA256 = "SHA-256";
    private static String SHA512 = "SHA-512";

    /**
     * HmacSHA256加密的方式
     */
    private static String HmacSHA256 = "HmacSHA256";

    /**
     * AES加密的方式
     */
    private static String AES_ALGORITHM_ECB = "AES/ECB/PKCS5Padding";
    private static String AES_ALGORITHM_CBC = "AES/CBC/PKCS7Padding";

    /**
     * 加密算法
     */
    private static String KEY_ALGORTHM = "RSA";

    /**
     * 具体加密算法，包括padding的方式
     */
    private static String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";

    /**
     * SHA512加密
     */
    public static String encryptSHA512(String content) {
        return encryptSHA(content, SHA512);
    }

    /**
     * SHA256加密
     */
    public static String encryptSHA256(String content) {
        return encryptSHA(content, SHA256);
    }

    /**
     * SHA1加密
     */
    public static String encryptSHA1(String content) {
        return encryptSHA(content, SHA1);
    }

    /**
     * SHA加密
     */
    public static String encryptSHA(String content) {
        return encryptSHA(content, SHA);
    }

    /**
     * 字符串 SHA 加密
     */
    public static String encryptSHA(final String content, final String encryptType) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(encryptType);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("encryptSHA error", e);
        }
        digest.update(content.getBytes());
        byte[] messageDigest = digest.digest();
        return StringUtils.toHex(messageDigest);
    }

    /**
     * sha256_HMAC加密
     * @param message 消息
     * @param secret 秘钥
     * @return 加密后字符串
     */
    public static String encryptSHA256Hmac(String message, String secret) {
        Mac mac;
        try {
            mac = Mac.getInstance(HmacSHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), HmacSHA256);
            mac.init(secretKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("encryptSHA256Hmac error", e);
        }
        byte[] bytes = mac.doFinal(message.getBytes());
        return StringUtils.toHex(bytes);
    }

    /**
     * MD5加密
     */
    public static String encryptMD5(String content) {
        // 获得MD5摘要算法的 MessageDigest 对象
        MessageDigest mdInst;
        try {
            mdInst = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("encryptMD5 error", e);
        }
        // 使用指定的字节更新摘要
        mdInst.update(content.getBytes());
        // 获得密文
        byte[] md = mdInst.digest();
        // 把密文转换成十六进制的字符串形式
        return StringUtils.toHex(md);
    }

    /**
     * AES加密
     * @param content 需要加密的内容
     * @param key 加密密码
     */
    public static String encryptAES(String content, String key) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(key)) {
            return null;
        }
        byte[] keyBytes = Base64.encodeBase64(key.getBytes());
        if (keyBytes.length != 16) {
            throw new RuntimeException("无效的AES密钥长度(必须为16字节)");
        }
        byte[] enCodeFormat = new SecretKeySpec(keyBytes, "AES").getEncoded();
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM_ECB);
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(enCodeFormat, "AES"));
            // 加密
            return Base64.encodeBase64String(cipher.doFinal(content.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("encryptAES error", e);
        }
    }

    /**
     * AES解密
     * @param encrypt 密文
     * @param key 解密密钥
     */
    public static String decryptAES(String encrypt, String key) {
        if (StringUtils.isEmpty(encrypt) || StringUtils.isEmpty(key)) {
            return null;
        }
        byte[] keyBytes = Base64.decodeBase64(key);
        if (keyBytes.length != 16) {
            throw new RuntimeException("无效的AES密钥长度(必须为16字节)");
        }
        byte[] enCodeFormat = new SecretKeySpec(keyBytes, "AES").getEncoded();
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM_ECB);
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(enCodeFormat, "AES"));
            // 解密
            return new String(cipher.doFinal(Base64.decodeBase64(encrypt)));
        } catch (Exception e) {
            throw new RuntimeException("decryptAES error", e);
        }
    }

    /**
     * AES解密
     * @param encrypt 密文
     * @param key 解密密钥
     * @param iv 偏移量
     */
    public static String decryptAES(String encrypt, String key, String iv) {
        if (StringUtils.isEmpty(encrypt) || StringUtils.isEmpty(key)) {
            return null;
        }
        //BouncyCastle是一个开源的加解密解决方案，主页在http://www.bouncycastle.org/
        Security.addProvider(new BouncyCastleProvider());
        byte[] keyBytes = Base64.decodeBase64(key);
        if (keyBytes.length != 16) {
            throw new RuntimeException("无效的AES密钥长度(必须为16字节)");
        }
        try {
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(Base64.decodeBase64(iv)));
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES_ALGORITHM_CBC);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM_CBC, "BC");
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameters);
            // 解密
            return new String(cipher.doFinal(Base64.decodeBase64(encrypt)));
        } catch (Exception e) {
            throw new RuntimeException("decryptAES error", e);
        }
    }

    /**
     * 生成AES的Key
     */
    public static byte[] AESGenarateKey() {
        KeyGenerator keygen;
        try {
            keygen = KeyGenerator.getInstance(AES_ALGORITHM_ECB);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AESGenarateKey error", e);
        }
        SecureRandom random = new SecureRandom();
        keygen.init(random);
        Key key = keygen.generateKey();
        return key.getEncoded();
    }

    /**
     * 生成公钥和私钥
     */
    public static RSAKey generateRSAKey() {
        SecureRandom sr = new SecureRandom();
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("generateRSAKey error", e);
        }
        //指定key的大小为1024
        kpg.initialize(1024, sr);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();

        //得到公钥
        String pub = Base64.encodeBase64String(keyPair.getPublic().getEncoded());
        //得到私钥
        String pri = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());

        RSAKey rsaKey = new RSAKey();
        rsaKey.setPublicKey(pub);
        rsaKey.setPrivateKey(pri);
        RSAPublicKey rsp = (RSAPublicKey) keyPair.getPublic();
        rsaKey.setModules(new String(Base64.encodeBase64(rsp.getModulus().toByteArray())));
        return rsaKey;
    }

    /**
     * 加密
     * @param content 加密内容
     * @param publicKey 公钥
     */
    public static String encryptRSA(String content, String publicKey) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey key = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            // 加密时超过maxEncryptBlockSize字节就报错。为此采用分段加密的办法来加密
            int maxEncryptBlockSize = getMaxEncryptBlockSize(keyFactory, key);

            byte[] data = content.getBytes();
            byte[] encryptedData;
            try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
                int dataLength = data.length;
                for (int i = 0; i < data.length; i += maxEncryptBlockSize) {
                    int encryptLength = dataLength - i < maxEncryptBlockSize ? dataLength - i : maxEncryptBlockSize;
                    byte[] doFinal = cipher.doFinal(data, i, encryptLength);
                    bout.write(doFinal);
                }
                encryptedData = bout.toByteArray();
            }
            return Base64.encodeBase64String(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("encryptRSA error", e);
        }
    }

    /**
     * 解密
     * @param encrypt 密文
     * @param privateKey 私钥
     */
    public static String decryptRSA(String encrypt, String privateKey) {
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
            PrivateKey key = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decryptedData;
            // 解密时超过maxDecryptBlockSize字节就报错。为此采用分段解密的办法来解密
            int maxDecryptBlockSize = getMaxDecryptBlockSize(keyFactory, key);

            byte[] data = Base64.decodeBase64(encrypt.getBytes());

            try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
                int dataLength = data.length;
                for (int i = 0; i < dataLength; i += maxDecryptBlockSize) {
                    int decryptLength = dataLength - i < maxDecryptBlockSize ? dataLength - i : maxDecryptBlockSize;
                    byte[] doFinal = cipher.doFinal(data, i, decryptLength);
                    bout.write(doFinal);
                }
                decryptedData = bout.toByteArray();
                return new String(decryptedData);
            }
        } catch (Exception e) {
            throw new RuntimeException("decryptRSA error", e);
        }
    }

    /**
     * 用私钥对信息生成数字签名
     * @param signType 签名类型
     * @param data 签名的数据
     * @param privateKey 私钥
     * @return 签名后的base64值
     */
    public static String sign(String signType, String data, String privateKey) throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        PrivateKey privateKey2 = getRSAPrivateKey(privateKey);
        //用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(signType);
        signature.initSign(privateKey2);
        signature.update(data.getBytes());
        return Base64.encodeBase64String(signature.sign());
    }

    /**
     * 校验数字签名
     * @param signType 签名类型
     * @param data 加密数据
     * @param publicKey 公钥
     * @param sign 数字签名
     * @return 验签结果
     * @throws Exception 异常
     */
    public static boolean verify(String signType, byte[] data, String publicKey, String sign) {
        try {
            PublicKey key = getRSAPublicKey(publicKey);
            Signature signature = Signature.getInstance(signType);
            signature.initVerify(key);
            signature.update(data);
            //验证签名是否正常
            return signature.verify(Base64.decodeBase64(sign));
        } catch (Exception e) {
            throw new RuntimeException("decryptRSA error", e);
        }
    }

    /**
     * 得到公钥
     * @param key 密钥字符串（经过base64编码）
     */
    private static PublicKey getRSAPublicKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 得到私钥
     * @param key 密钥字符串（经过base64编码）
     */
    private static PrivateKey getRSAPrivateKey(String key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORTHM);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 获取每次加密的最大长度
     * @param keyFactory KeyFactory
     * @param key 公钥
     * @return 单词加密最大长度
     */
    private static int getMaxEncryptBlockSize(KeyFactory keyFactory, Key key) throws InvalidKeySpecException {
        RSAPublicKeySpec publicKeySpec = keyFactory.getKeySpec(key, RSAPublicKeySpec.class);
        int keyLength = publicKeySpec.getModulus().bitLength();
        return keyLength / 8 - 11;
    }

    /***
     * 获取每次解密最大长度
     * @param keyFactory KeyFactory
     * @param key 私钥
     * @return 单次解密最大长度
     */
    private static int getMaxDecryptBlockSize(KeyFactory keyFactory, Key key) throws InvalidKeySpecException {
        RSAPrivateKeySpec publicKeySpec = keyFactory.getKeySpec(key, RSAPrivateKeySpec.class);
        int keyLength = publicKeySpec.getModulus().bitLength();
        return keyLength / 8;
    }

}
