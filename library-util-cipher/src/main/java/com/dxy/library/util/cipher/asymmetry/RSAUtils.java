package com.dxy.library.util.cipher.asymmetry;

import com.dxy.library.util.cipher.constant.*;
import com.dxy.library.util.cipher.pojo.RSAKeyPair;
import com.dxy.library.util.common.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA工具类
 * 最典型的非对称加密算法，1978由Ron Rivest, AdiShamir 和Leonard Adleman三人发明
 * 同时有两把钥匙，公钥与私钥。支持数字签名，能用签名对传输过来的数据进行校验
 * 默认公钥X509格式，私钥PKCS8格式
 * SM2密文采用Base64方式编码
 * 包括：签名，验签； 公钥加密，私钥解密；私钥加密，公钥解密
 * @author duanxinyuan
 * 2017/9/6 19:20
 */
public class RSAUtils {

    static {
        //导入Provider，BouncyCastle是一个开源的加解密解决方案，主页在http://www.bouncycastle.org/
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 公钥加密（最常用的模式，使用RSA/ECB/PKCS1Padding方式）
     * @param data 加密内容
     * @param publicKey 公钥
     * @return 密文（Base64编码）
     */
    public static String encryptByPublicKey(String data, String publicKey) {
        return encryptByPublicKey(data, publicKey, Mode.ECB, Padding.PKCS1Padding);
    }

    /**
     * 公钥加密（最常用的模式，使用RSA/ECB/PKCS1Padding方式）
     * @param data 加密内容
     * @param publicKey 公钥
     * @return 密文
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] publicKey) {
        return encryptByPublicKey(data, publicKey, Mode.ECB, Padding.PKCS1Padding);
    }

    /**
     * 公钥加密
     * @param data 加密内容
     * @param publicKey 公钥
     * @return 密文（Base64编码）
     */
    public static String encryptByPublicKey(String data, String publicKey, Mode mode, Padding padding) {
        RSAPublicKey rsaPublicKey = getPublicKey(publicKey);
        return encrypt(data, rsaPublicKey, mode, padding);
    }

    /**
     * 公钥加密
     * @param data 加密内容
     * @param publicKey 公钥
     * @return 密文
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] publicKey, Mode mode, Padding padding) {
        RSAPublicKey rsaPublicKey = getPublicKey(publicKey);
        return encrypt(data, rsaPublicKey, mode, padding);
    }

    /**
     * 私钥加密（最常用的模式，使用RSA/ECB/PKCS1Padding方式）
     * @param data 加密内容
     * @param privateKey 私钥
     * @return 密文（Base64编码）
     */
    public static String encryptByPrivateKey(String data, String privateKey) {
        return encryptByPrivateKey(data, privateKey, Mode.ECB, Padding.PKCS1Padding);
    }

    /**
     * 私钥加密（最常用的模式，使用RSA/ECB/PKCS1Padding方式）
     * @param data 加密内容
     * @param privateKey 私钥
     * @return 密文
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] privateKey) {
        return encryptByPrivateKey(data, privateKey, Mode.ECB, Padding.PKCS1Padding);
    }

    /**
     * 私钥加密
     * @param data 加密内容
     * @param privateKey 私钥
     * @return 密文（Base64编码）
     */
    public static String encryptByPrivateKey(String data, String privateKey, Mode mode, Padding padding) {
        RSAPrivateKey rsaPrivateKey = getPrivateKey(privateKey);
        return encrypt(data, rsaPrivateKey, mode, padding);
    }

    /**
     * 私钥加密
     * @param data 加密内容
     * @param privateKey 私钥
     * @return 密文
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] privateKey, Mode mode, Padding padding) {
        RSAPrivateKey rsaPrivateKey = getPrivateKey(privateKey);
        return encrypt(data, rsaPrivateKey, mode, padding);
    }

    /**
     * 加密
     * @param data 加密内容
     * @param rsaKey 公钥/私钥
     * @return 密文（Base64编码）
     */
    public static String encrypt(String data, RSAKey rsaKey, Mode mode, Padding padding) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        byte[] encrypt = encrypt(data.getBytes(), rsaKey, mode, padding);
        return Base64.encodeBase64String(encrypt);
    }

    /**
     * 加密
     * @param data 加密内容
     * @param rsaKey 公钥/私钥
     * @return 密文
     */
    public static byte[] encrypt(byte[] data, RSAKey rsaKey, Mode mode, Padding padding) {
        check(mode, padding);
        try {
            String algorithm = AlgorithmUtils.getAlgorithm(Algorithm.RSA, mode, padding);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, (Key) rsaKey);

            // 加密时超过maxEncryptBlockSize字节就报错。为此采用分段加密的办法来加密
            int keyLength = rsaKey.getModulus().bitLength();
            //必须比 RSA密钥的模长(modulus) 短至少11个字节
            int blockSize = keyLength / 8 - 11;
            return segmentHandling(cipher, data, blockSize);
        } catch (Exception e) {
            throw new RuntimeException("RSA encrypt error", e);
        }
    }

    /**
     * 公钥解密
     * @param data 密文（Base64编码）
     * @param publicKey 公钥
     * @return 明文
     */
    public static String decryptByPublicKey(String data, String publicKey) {
        return decryptByPublicKey(data, publicKey, Mode.ECB, Padding.PKCS1Padding);
    }

    /**
     * 公钥解密
     * @param data 密文
     * @param publicKey 公钥
     * @return 明文
     */
    public static byte[] decryptByPublicKey(byte[] data, String publicKey) {
        return decryptByPublicKey(data, publicKey, Mode.ECB, Padding.PKCS1Padding);
    }

    /**
     * 公钥解密
     * @param data 密文（Base64编码）
     * @param publicKey 公钥
     * @return 明文
     */
    public static String decryptByPublicKey(String data, String publicKey, Mode mode, Padding padding) {
        try {
            RSAPublicKey rsaPublicKey = getPublicKey(publicKey);
            return decrypt(data, rsaPublicKey, mode, padding);
        } catch (Exception e) {
            throw new RuntimeException("RSA decrypt error", e);
        }
    }

    /**
     * 公钥解密
     * @param data 密文
     * @param publicKey 公钥
     * @return 明文
     */
    public static byte[] decryptByPublicKey(byte[] data, String publicKey, Mode mode, Padding padding) {
        RSAPublicKey rsaPublicKey = getPublicKey(publicKey);
        return decrypt(data, rsaPublicKey, mode, padding);
    }

    /**
     * 私钥解密
     * @param data 密文（Base64编码）
     * @param privateKey 私钥
     * @return 明文
     */
    public static String decryptByPrivateKey(String data, String privateKey) {
        return decryptByPrivateKey(data, privateKey, Mode.ECB, Padding.PKCS1Padding);
    }

    /**
     * 私钥解密
     * @param data 密文
     * @param privateKey 私钥
     * @return 明文
     */
    public static byte[] decryptByPrivateKey(byte[] data, String privateKey) {
        return decryptByPrivateKey(data, privateKey, Mode.ECB, Padding.PKCS1Padding);
    }

    /**
     * 私钥解密
     * @param data 密文（Base64编码）
     * @param privateKey 私钥
     * @return 明文
     */
    public static String decryptByPrivateKey(String data, String privateKey, Mode mode, Padding padding) {
        RSAPrivateKey rsaPrivateKey = getPrivateKey(privateKey);
        return decrypt(data, rsaPrivateKey, mode, padding);
    }

    /**
     * 私钥解密
     * @param data 密文
     * @param privateKey 私钥
     * @return 明文
     */
    public static byte[] decryptByPrivateKey(byte[] data, String privateKey, Mode mode, Padding padding) {
        RSAPrivateKey rsaPrivateKey = getPrivateKey(privateKey);
        return decrypt(data, rsaPrivateKey, mode, padding);
    }

    /**
     * 解密
     * @param data 密文（Base64编码）
     * @param rsaKey 公钥/私钥
     * @return 明文
     */
    public static String decrypt(String data, RSAKey rsaKey, Mode mode, Padding padding) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        byte[] decrypt = decrypt(Base64.decodeBase64(data.getBytes()), rsaKey, mode, padding);
        return new String(decrypt);
    }

    /**
     * 解密
     * @param data 密文
     * @param rsaKey 公钥/私钥
     * @return 明文
     */
    public static byte[] decrypt(byte[] data, RSAKey rsaKey, Mode mode, Padding padding) {
        check(mode, padding);
        try {
            String algorithm = AlgorithmUtils.getAlgorithm(Algorithm.RSA, mode, padding);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, (Key) rsaKey);

            // 解密时超过maxDecryptBlockSize字节就报错。为此采用分段解密的办法来解密
            int keyLength = rsaKey.getModulus().bitLength();
            int blockSize = keyLength / 8;
            return segmentHandling(cipher, data, blockSize);
        } catch (Exception e) {
            throw new RuntimeException("RSA decrypt error", e);
        }
    }

    /**
     * 用私钥对信息生成数字签名
     * @param signType 签名类型
     * @param data 签名的数据
     * @param privateKey 私钥
     * @return 签名（base64编码）
     */
    public static String sign(SignType signType, String data, String privateKey) {
        byte[] sign = sign(signType, data.getBytes(), privateKey);
        return Base64.encodeBase64String(sign);
    }

    /**
     * 用私钥对信息生成数字签名
     * @param signType 签名类型
     * @param data 签名的数据
     * @param privateKey 私钥
     * @return 签名
     */
    public static byte[] sign(SignType signType, byte[] data, String privateKey) {
        try {
            RSAPrivateKey rsaPrivateKey = getPrivateKey(privateKey);
            //用私钥对信息生成数字签名
            Signature signature = Signature.getInstance(signType.getType());
            signature.initSign(rsaPrivateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw new RuntimeException("RSA sign error", e);
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
    public static boolean verifySign(SignType signType, String data, String publicKey, String sign) {
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
    public static boolean verifySign(SignType signType, byte[] data, String publicKey, byte[] sign) {
        try {
            RSAPublicKey rsaPublicKey = getPublicKey(publicKey);
            Signature signature = Signature.getInstance(signType.getType());
            signature.initVerify(rsaPublicKey);
            signature.update(data);
            //验证签名是否正常
            return signature.verify(sign);
        } catch (Exception e) {
            throw new RuntimeException("RSA verify sign error", e);
        }
    }

    /**
     * 生成公钥和私钥
     */
    public static RSAKeyPair generateKey() {
        return generateKey(2048);
    }

    /**
     * 生成公钥和私钥
     */
    public static RSAKeyPair generateKey(int keysize) {
        SecureRandom sr = new SecureRandom();
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance(Algorithm.RSA.getAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("RSA key generate error", e);
        }
        kpg.initialize(keysize, sr);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();

        //得到公钥
        String pub = Base64.encodeBase64String(keyPair.getPublic().getEncoded());
        //得到私钥
        String pri = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());

        RSAKeyPair rsaKeyPair = new RSAKeyPair();
        rsaKeyPair.setPublicKey(pub);
        rsaKeyPair.setPrivateKey(pri);
        RSAPublicKey rsp = (RSAPublicKey) keyPair.getPublic();
        rsaKeyPair.setModules(new String(Base64.encodeBase64(rsp.getModulus().toByteArray())));
        return rsaKeyPair;
    }

    /**
     * 分段处理密文
     * @param cipher 加密算法
     * @param data 密文或者明文
     * @param blockSize 单次加解密最大长度，加解密时超过maxDecryptBlockSize字节就报错。为此采用分段解密的办法来解密
     */
    private static byte[] segmentHandling(Cipher cipher, byte[] data, int blockSize) throws IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] result;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            int dataLength = data.length;
            for (int i = 0; i < dataLength; i += blockSize) {
                int contentLength = dataLength - i < blockSize ? dataLength - i : blockSize;
                byte[] doFinal = cipher.doFinal(data, i, contentLength);
                byteArrayOutputStream.write(doFinal);
            }
            result = byteArrayOutputStream.toByteArray();
        }
        return result;
    }

    /**
     * 获取公钥（X509格式）
     * @param publicKey 公钥（经过base64编码）
     */
    public static RSAPublicKey getPublicKey(String publicKey) {
        return getPublicKey(Base64.decodeBase64(publicKey.getBytes()));
    }

    /**
     * 获取公钥（X509格式）
     * @param key 公钥
     */
    public static RSAPublicKey getPublicKey(byte[] key) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(Algorithm.RSA.getAlgorithm());
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取私钥（PKCS8格式）
     * @param privateKey 私钥（经过base64编码）
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) {
        return getPrivateKey(Base64.decodeBase64(privateKey.getBytes()));
    }

    /**
     * 获取私钥（PKCS8格式）
     * @param key 私钥
     */
    public static RSAPrivateKey getPrivateKey(byte[] key) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(Algorithm.RSA.getAlgorithm());
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void check(Mode mode, Padding padding) {
        if (mode != Mode.NONE && mode != Mode.ECB) {
            throw new RuntimeException("invalid RSA mode");
        }
        if (padding != Padding.PKCS1Padding && padding != Padding.NoPadding) {
            throw new RuntimeException("invalid RSA padding");
        }
    }

}
