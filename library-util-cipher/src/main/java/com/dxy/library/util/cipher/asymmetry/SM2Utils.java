package com.dxy.library.util.cipher.asymmetry;

import com.dxy.library.util.cipher.constant.Algorithm;
import com.dxy.library.util.cipher.pojo.SM2Cipher;
import com.dxy.library.util.cipher.pojo.SM2KeyPair;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.gm.SM2P256V1Curve;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;

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

    private static final String PEM_STRING_PUBLIC = "PUBLIC KEY";
    private static final String PEM_STRING_ECPRIVATEKEY = "EC PRIVATE KEY";
    /*
     * 以下为SM2推荐曲线参数
     */
    //固定椭圆曲线
    private static final SM2P256V1Curve sm2P256V1Curve = new SM2P256V1Curve();

    private final static BigInteger SM2_ECC_P = sm2P256V1Curve.getQ();
    private final static BigInteger SM2_ECC_A = sm2P256V1Curve.getA().toBigInteger();
    private final static BigInteger SM2_ECC_B = sm2P256V1Curve.getB().toBigInteger();
    private final static BigInteger SM2_ECC_N = sm2P256V1Curve.getOrder();
    private final static BigInteger SM2_ECC_H = sm2P256V1Curve.getCofactor();

    //ECC算法的参数
    private final static BigInteger SM2_ECC_GX = new BigInteger("32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", 16);
    private final static BigInteger SM2_ECC_GY = new BigInteger("BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0", 16);

    //公钥点ECPoint
    private static final ECPoint G_POINT = sm2P256V1Curve.createPoint(SM2_ECC_GX, SM2_ECC_GY);
    private static final ECDomainParameters DOMAIN_PARAMS = new ECDomainParameters(sm2P256V1Curve, G_POINT, SM2_ECC_N, SM2_ECC_H);

    //椭圆曲线长度
    private static final int sm2P256V1Curve_LEN = getCurveLength(DOMAIN_PARAMS);

    private static final EllipticCurve JDK_CURVE = new EllipticCurve(new ECFieldFp(SM2_ECC_P), SM2_ECC_A, SM2_ECC_B);
    private static final java.security.spec.ECPoint JDK_G_POINT = new java.security.spec.ECPoint(G_POINT.getAffineXCoord().toBigInteger(), G_POINT.getAffineYCoord().toBigInteger());
    private static final java.security.spec.ECParameterSpec JDK_EC_SPEC = new java.security.spec.ECParameterSpec(JDK_CURVE, JDK_G_POINT, SM2_ECC_N, SM2_ECC_H.intValue());

    //密钥长度
    private static final int SM3_DIGEST_LENGTH = 32;

    static {
        //导入Provider，BouncyCastle是一个开源的加解密解决方案，主页在http://www.bouncycastle.org/
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 生成ECC密钥对
     * @return ECC密钥对
     */
    public static SM2KeyPair generateKey() {
        SecureRandom random = new SecureRandom();
        ECKeyGenerationParameters keyGenerationParams = new ECKeyGenerationParameters(DOMAIN_PARAMS, random);
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();
        keyPairGenerator.init(keyGenerationParams);

        AsymmetricCipherKeyPair asymmetricCipherKeyPair = keyPairGenerator.generateKeyPair();
        ECPrivateKeyParameters ecPrivateKeyParameters = (ECPrivateKeyParameters) asymmetricCipherKeyPair.getPrivate();
        ECPublicKeyParameters ecPublicKeyParameters = (ECPublicKeyParameters) asymmetricCipherKeyPair.getPublic();
        SM2KeyPair sm2KeyPair = new SM2KeyPair();
        sm2KeyPair.setPublicKeyParameters(ecPublicKeyParameters);
        sm2KeyPair.setPrivateKeyParameters(ecPrivateKeyParameters);
        sm2KeyPair.setPublicKey(ecPublicKeyParameters.getQ());
        sm2KeyPair.setPrivateKey(ecPrivateKeyParameters.getD());
        return sm2KeyPair;
    }

    /**
     * 生成密钥对
     * @return 密钥对
     */
    public static KeyPair generateKeyPair() {
        SecureRandom random = new SecureRandom();
        try {
            ECParameterSpec parameterSpec = new ECParameterSpec(DOMAIN_PARAMS.getCurve(), DOMAIN_PARAMS.getG(), DOMAIN_PARAMS.getN(), DOMAIN_PARAMS.getH());
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(Algorithm.EC.getAlgorithm(), BouncyCastleProvider.PROVIDER_NAME);
            keyPairGenerator.initialize(parameterSpec, random);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] encrypt(BCECPublicKey publicKey, byte[] data) {
        ECPublicKeyParameters publicKeyParameters = getPublicKeyEC(publicKey);
        return encrypt(publicKeyParameters, data);
    }

    /**
     * ECC公钥加密
     * @param publicKey ECC公钥
     * @param data 源数据
     * @return SM2密文（Base64编码），实际包含三部分：ECC公钥、真正的密文、公钥和明文的SM3-HASH值
     */
    public static String encrypt(String publicKey, String data) {
        ECPublicKeyParameters publicKeyParameters = getPublicKeyEC(publicKey);
        byte[] encrypt = encrypt(publicKeyParameters, data.getBytes());
        return Base64.encodeBase64String(encrypt);
    }

    /**
     * ECC公钥加密
     * @param publicKeyParameters ECC公钥
     * @param data 源数据
     * @return SM2密文，实际包含三部分：ECC公钥、真正的密文、公钥和明文的SM3-HASH值
     */
    public static byte[] encrypt(ECPublicKeyParameters publicKeyParameters, byte[] data) {
        SM2Engine engine = new SM2Engine();
        ParametersWithRandom pwr = new ParametersWithRandom(publicKeyParameters, new SecureRandom());
        engine.init(true, pwr);
        try {
            return engine.processBlock(data, 0, data.length);
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ECC私钥解密
     * @param privateKey ECC私钥
     * @param sm2Cipher SM2密文（Base64编码），实际包含三部分：ECC公钥、真正的密文、公钥和明文的SM3-HASH值
     * @return 明文
     */
    public static String decrypt(String privateKey, String sm2Cipher) {
        ECPrivateKeyParameters ecPrivateKeyParameters = getPrivateKeyEC(privateKey);
        byte[] decrypt = decrypt(ecPrivateKeyParameters, Base64.decodeBase64(sm2Cipher));
        return new String(decrypt);
    }

    public static byte[] decrypt(BCECPrivateKey privateKey, byte[] sm2Cipher) {
        ECPrivateKeyParameters ecPrivateKeyParameters = getPrivateKeyEC(privateKey);
        return decrypt(ecPrivateKeyParameters, sm2Cipher);
    }

    /**
     * ECC私钥解密
     * @param ecPrivateKeyParameters ECC私钥
     * @param sm2Cipher SM2密文，实际包含三部分：ECC公钥、真正的密文、公钥和明文的SM3-HASH值
     * @return 明文
     */
    public static byte[] decrypt(ECPrivateKeyParameters ecPrivateKeyParameters, byte[] sm2Cipher) {
        SM2Engine engine = new SM2Engine();
        engine.init(false, ecPrivateKeyParameters);
        try {
            return engine.processBlock(sm2Cipher, 0, sm2Cipher.length);
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 分解SM2密文
     * @param cipherText SM2密文
     */
    public static SM2Cipher parseSM2Cipher(byte[] cipherText) {
        int curveLength = getCurveLength(DOMAIN_PARAMS);
        return parseSM2Cipher(curveLength, SM3_DIGEST_LENGTH, cipherText);
    }

    /**
     * 分解SM2密文
     * @param curveLength ECC曲线长度
     * @param digestLength HASH长度
     * @param cipherText SM2密文
     */
    public static SM2Cipher parseSM2Cipher(int curveLength, int digestLength, byte[] cipherText) {
        byte[] c1 = new byte[curveLength * 2 + 1];
        System.arraycopy(cipherText, 0, c1, 0, c1.length);
        byte[] c2 = new byte[cipherText.length - c1.length - digestLength];
        System.arraycopy(cipherText, c1.length, c2, 0, c2.length);
        byte[] c3 = new byte[digestLength];
        System.arraycopy(cipherText, c1.length + c2.length, c3, 0, c3.length);
        SM2Cipher result = new SM2Cipher();
        result.setC1(c1);
        result.setC2(c2);
        result.setC3(c3);
        result.setCipherText(cipherText);
        return result;
    }

    /**
     * DER编码C1C2C3密文（根据《SM2密码算法使用规范》 GM/T 0009-2012）
     */
    public static byte[] encodeCipherToDER(byte[] cipher) {
        int curveLength = getCurveLength(DOMAIN_PARAMS);
        return encodeCipherToDER(curveLength, SM3_DIGEST_LENGTH, cipher);
    }

    /**
     * DER编码C1C2C3密文（根据《SM2密码算法使用规范》 GM/T 0009-2012）
     * @param curveLength 椭圆曲线长度
     * @param digestLength 摘要长度
     * @param cipher 密文
     */
    public static byte[] encodeCipherToDER(int curveLength, int digestLength, byte[] cipher) {
        int startPos = 1;

        byte[] c1x = new byte[curveLength];
        System.arraycopy(cipher, startPos, c1x, 0, c1x.length);
        startPos += c1x.length;

        byte[] c1y = new byte[curveLength];
        System.arraycopy(cipher, startPos, c1y, 0, c1y.length);
        startPos += c1y.length;

        byte[] c2 = new byte[cipher.length - c1x.length - c1y.length - 1 - digestLength];
        System.arraycopy(cipher, startPos, c2, 0, c2.length);
        startPos += c2.length;

        byte[] c3 = new byte[digestLength];
        System.arraycopy(cipher, startPos, c3, 0, c3.length);

        ASN1Encodable[] arr = new ASN1Encodable[4];
        arr[0] = new ASN1Integer(c1x);
        arr[1] = new ASN1Integer(c1y);
        arr[2] = new DEROctetString(c3);
        arr[3] = new DEROctetString(c2);
        DERSequence ds = new DERSequence(arr);
        try {
            return ds.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解DER编码密文（根据《SM2密码算法使用规范》 GM/T 0009-2012）
     * @param derCipher DER编码的密文
     */
    public static byte[] decodeDERCipher(byte[] derCipher) {
        ASN1Sequence as = DERSequence.getInstance(derCipher);
        byte[] c1x = ((ASN1Integer) as.getObjectAt(0)).getValue().toByteArray();
        byte[] c1y = ((ASN1Integer) as.getObjectAt(1)).getValue().toByteArray();
        byte[] c3 = ((DEROctetString) as.getObjectAt(2)).getOctets();
        byte[] c2 = ((DEROctetString) as.getObjectAt(3)).getOctets();

        int pos = 0;
        byte[] cipherText = new byte[1 + c1x.length + c1y.length + c2.length + c3.length];

        final byte uncompressedFlag = 0x04;
        cipherText[0] = uncompressedFlag;
        pos += 1;

        System.arraycopy(c1x, 0, cipherText, pos, c1x.length);
        pos += c1x.length;

        System.arraycopy(c1y, 0, cipherText, pos, c1y.length);
        pos += c1y.length;

        System.arraycopy(c2, 0, cipherText, pos, c2.length);
        pos += c2.length;

        System.arraycopy(c3, 0, cipherText, pos, c3.length);

        return cipherText;
    }

    /**
     * ECC私钥签名
     * 不指定withId，则默认withId为字节数组:"1234567812345678".getBytes()
     * @param privateKey ECC私钥
     * @param data 源数据
     * @return 签名（Base64编码）
     */
    public static String sign(String privateKey, String data) {
        ECPrivateKeyParameters ecPrivateKeyParameters = getPrivateKeyEC(privateKey);
        byte[] sign = sign(ecPrivateKeyParameters, null, data.getBytes());
        return Base64.encodeBase64String(sign);
    }

    public static byte[] sign(BCECPrivateKey privateKey, byte[] data) {
        ECPrivateKeyParameters ecPrivateKeyParameters = getPrivateKeyEC(privateKey);
        return sign(ecPrivateKeyParameters, null, data);
    }

    public static byte[] sign(BCECPrivateKey privateKey, byte[] withId, byte[] data) {
        ECPrivateKeyParameters ecPrivateKeyParameters = getPrivateKeyEC(privateKey);
        return sign(ecPrivateKeyParameters, withId, data);
    }

    /**
     * ECC私钥签名
     * 不指定withId，则默认withId为字节数组:"1234567812345678".getBytes()
     * @param ecPrivateKeyParameters ECC私钥
     * @param data 源数据
     * @return 签名
     */
    public static byte[] sign(ECPrivateKeyParameters ecPrivateKeyParameters, byte[] data) {
        return sign(ecPrivateKeyParameters, null, data);
    }

    /**
     * ECC私钥签名
     * @param ecPrivateKeyParameters ECC私钥
     * @param withId 可以为null，若为null，则默认withId为字节数组:"1234567812345678".getBytes()
     * @param data 源数据
     * @return 签名
     */
    public static byte[] sign(ECPrivateKeyParameters ecPrivateKeyParameters, byte[] withId, byte[] data) {
        SM2Signer signer = new SM2Signer();
        ParametersWithRandom pwr = new ParametersWithRandom(ecPrivateKeyParameters, new SecureRandom());
        CipherParameters param;
        if (withId != null) {
            param = new ParametersWithID(pwr, withId);
        } else {
            param = pwr;
        }
        signer.init(true, param);
        signer.update(data, 0, data.length);
        try {
            return signer.generateSignature();
        } catch (CryptoException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将DER编码的SM2签名解析成64字节的纯R+S字节流
     * @param derSign DER编码的SM2签名
     */
    public static byte[] decodeDERSign(byte[] derSign) {
        ASN1Sequence as = DERSequence.getInstance(derSign);
        byte[] rBytes = ((ASN1Integer) as.getObjectAt(0)).getValue().toByteArray();
        byte[] sBytes = ((ASN1Integer) as.getObjectAt(1)).getValue().toByteArray();
        //由于大数的补0规则，所以可能会出现33个字节的情况，要修正回32个字节
        rBytes = fixToCurveLengthBytes(rBytes);
        sBytes = fixToCurveLengthBytes(sBytes);
        byte[] rawSign = new byte[rBytes.length + sBytes.length];
        System.arraycopy(rBytes, 0, rawSign, 0, rBytes.length);
        System.arraycopy(sBytes, 0, rawSign, rBytes.length, sBytes.length);
        return rawSign;
    }

    /**
     * 把64字节的纯R+S字节流转换成DER编码字节流
     * @param rawSign 签名
     */
    public static byte[] encodeSignToDER(byte[] rawSign) {
        //要保证大数是正数
        BigInteger r = new BigInteger(1, extractBytes(rawSign, 0, 32));
        BigInteger s = new BigInteger(1, extractBytes(rawSign, 32, 32));
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(r));
        v.add(new ASN1Integer(s));
        try {
            return new DERSequence(v).getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ECC公钥验签
     * 不指定withId，则默认withId为字节数组:"1234567812345678".getBytes()
     * @param publicKey ECC公钥
     * @param data 源数据
     * @param sign 签名（Base64编码）
     * @return 验签成功返回true，失败返回false
     */
    public static boolean verify(String publicKey, String data, String sign) {
        ECPublicKeyParameters publicKeyParameters = getPublicKeyEC(publicKey);
        return verify(publicKeyParameters, null, data.getBytes(), Base64.decodeBase64(sign));
    }

    public static boolean verify(BCECPublicKey publicKey, byte[] data, byte[] sign) {
        ECPublicKeyParameters publicKeyParameters = getPublicKeyEC(publicKey);
        return verify(publicKeyParameters, null, data, sign);
    }

    /**
     * ECC公钥验签
     */
    public static boolean verify(BCECPublicKey publicKey, byte[] withId, byte[] data, byte[] sign) {
        ECPublicKeyParameters publicKeyParameters = getPublicKeyEC(publicKey);
        return verify(publicKeyParameters, withId, data, sign);
    }

    /**
     * ECC公钥验签
     * 不指定withId，则默认withId为字节数组:"1234567812345678".getBytes()
     * @param publicKeyParameters ECC公钥
     * @param data 源数据
     * @param sign 签名
     * @return 验签成功返回true，失败返回false
     */
    public static boolean verify(ECPublicKeyParameters publicKeyParameters, byte[] data, byte[] sign) {
        return verify(publicKeyParameters, null, data, sign);
    }

    /**
     * ECC公钥验签
     * @param publicKeyParameters ECC公钥
     * @param withId 可以为null，若为null，则默认withId为字节数组:"1234567812345678".getBytes()
     * @param data 源数据
     * @param sign 签名
     * @return 验签成功返回true，失败返回false
     */
    public static boolean verify(ECPublicKeyParameters publicKeyParameters, byte[] withId, byte[] data, byte[] sign) {
        SM2Signer signer = new SM2Signer();
        CipherParameters param;
        if (withId != null) {
            param = new ParametersWithID(publicKeyParameters, withId);
        } else {
            param = publicKeyParameters;
        }
        signer.init(false, param);
        signer.update(data, 0, data.length);
        return signer.verifySignature(sign);
    }

    /**
     * 只获取私钥里的d，32字节
     * @param privateKey 私钥
     */
    public static byte[] getRawPrivateKey(BCECPrivateKey privateKey) {
        return fixToCurveLengthBytes(privateKey.getD().toByteArray());
    }

    /**
     * 只获取公钥里的XY分量，64字节
     * @param publicKey 公钥
     */
    public static byte[] getRawPublicKey(BCECPublicKey publicKey) {
        byte[] src65 = publicKey.getQ().getEncoded(false);
        //SM2的话这里应该是64字节
        byte[] rawXY = new byte[sm2P256V1Curve_LEN * 2];
        System.arraycopy(src65, 1, rawXY, 0, rawXY.length);
        return rawXY;
    }

    /**
     * 获取EC公钥
     */
    public static ECPublicKeyParameters getPublicKeyEC(BigInteger x, BigInteger y, ECCurve curve, ECDomainParameters domainParameters) {
        return getPublicKeyEC(x.toByteArray(), y.toByteArray(), curve, domainParameters);
    }

    /**
     * 获取EC公钥
     */
    public static ECPublicKeyParameters getPublicKeyEC(byte[] xBytes, byte[] yBytes, ECCurve curve, ECDomainParameters domainParameters) {
        final byte uncompressedFlag = 0x04;
        int curveLength = getCurveLength(domainParameters);
        xBytes = fixToCurveLengthBytes(curveLength, xBytes);
        yBytes = fixToCurveLengthBytes(curveLength, yBytes);
        byte[] encodedPubKey = new byte[1 + xBytes.length + yBytes.length];
        encodedPubKey[0] = uncompressedFlag;
        System.arraycopy(xBytes, 0, encodedPubKey, 1, xBytes.length);
        System.arraycopy(yBytes, 0, encodedPubKey, 1 + xBytes.length, yBytes.length);
        return new ECPublicKeyParameters(curve.decodePoint(encodedPubKey), domainParameters);
    }

    /**
     * 获取EC公钥（将X509标准的字节流转换成BCEC公钥，再将BCEC公钥转为EC公钥）
     * @param publicKey 公钥（经过base64编码）
     */
    public static ECPublicKeyParameters getPublicKeyEC(String publicKey) {
        BCECPublicKey bcecPublicKey = getPublicKeyBCEC(publicKey);
        return getPublicKeyEC(bcecPublicKey);
    }

    /**
     * 获取EC公钥（将BCEC公钥转为EC公钥）
     */
    public static ECPublicKeyParameters getPublicKeyEC(BCECPublicKey bcecPublicKey) {
        ECParameterSpec parameterSpec = bcecPublicKey.getParameters();
        ECDomainParameters domainParameters = new ECDomainParameters(parameterSpec.getCurve(), parameterSpec.getG(), parameterSpec.getN(), parameterSpec.getH());
        return new ECPublicKeyParameters(bcecPublicKey.getQ(), domainParameters);
    }

    /**
     * 获取EC私钥
     */
    public static ECPrivateKeyParameters getPrivateKeyEC(BigInteger d, ECDomainParameters domainParameters) {
        return new ECPrivateKeyParameters(d, domainParameters);
    }

    /**
     * 获取EC私钥（将BCEC私钥转为EC私钥）
     * @param privateKey BCEC私钥（经过base64编码，PKCS8格式）
     */
    public static ECPrivateKeyParameters getPrivateKeyEC(String privateKey) {
        BCECPrivateKey bcecPrivateKey = getPrivateKeyBCEC(privateKey);
        return getPrivateKeyEC(bcecPrivateKey);
    }


    /**
     * 获取EC私钥（将BCEC私钥转为EC私钥）
     * @param key BCEC私钥（PKCS8格式）
     */
    public static ECPrivateKeyParameters getPrivateKeyEC(byte[] key) {
        BCECPrivateKey bcecPrivateKey = getPrivateKeyBCEC(key);
        return getPrivateKeyEC(bcecPrivateKey);
    }

    /**
     * 获取EC私钥（将BCEC私钥转为EC私钥）
     */
    public static ECPrivateKeyParameters getPrivateKeyEC(BCECPrivateKey bcecPrivateKey) {
        ECParameterSpec parameterSpec = bcecPrivateKey.getParameters();
        ECDomainParameters domainParameters = new ECDomainParameters(parameterSpec.getCurve(), parameterSpec.getG(), parameterSpec.getN(), parameterSpec.getH());
        return new ECPrivateKeyParameters(bcecPrivateKey.getD(), domainParameters);
    }

    /**
     * 获取EC私钥（将SEC1标准的字节流转为EC私钥）
     * 用来识别openssl生成的ECC私钥（openssl i2d_ECPrivateKey函数生成的DER编码的ecc私钥是SEC1标准的、带有EC_GROUP、带有公钥的，）
     * @param key 私钥（SEC1格式）
     */
    public static ECPrivateKeyParameters getPrivateKeyECSEC1(byte[] key) {
        BCECPrivateKey privateKey = getPrivateKeyBCECSEC1(key);
        return getPrivateKeyEC(privateKey);
    }

    /**
     * 获取BCEC公钥
     */
    public static BCECPublicKey getPublicKeyBCEC(SubjectPublicKeyInfo subPubInfo) {
        try {
            return getPublicKeyBCEC(subPubInfo.toASN1Primitive().getEncoded(ASN1Encoding.DER));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取BCEC公钥（X509格式）
     * @param publicKey 公钥（经过base64编码）
     */
    public static BCECPublicKey getPublicKeyBCEC(String publicKey) {
        return getPublicKeyBCEC(Base64.decodeBase64(publicKey));
    }

    /**
     * 获取BCEC公钥（X509格式）
     * @param key 公钥
     */
    public static BCECPublicKey getPublicKeyBCEC(byte[] key) {
        X509EncodedKeySpec eks = new X509EncodedKeySpec(key);
        try {
            KeyFactory kf = KeyFactory.getInstance(Algorithm.EC.getAlgorithm(), BouncyCastleProvider.PROVIDER_NAME);
            return (BCECPublicKey) kf.generatePublic(eks);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取BCEC私钥（PKCS8格式，默认格式）
     * @param privateKey 私钥（经过base64编码，PKCS8格式）
     */
    public static BCECPrivateKey getPrivateKeyBCEC(String privateKey) {
        return getPrivateKeyBCEC(Base64.decodeBase64(privateKey));
    }

    /**
     * 获取BCEC私钥（PKCS8格式，默认格式）
     * @param key 私钥（PKCS8格式）
     */
    public static BCECPrivateKey getPrivateKeyBCEC(byte[] key) {
        PKCS8EncodedKeySpec peks = new PKCS8EncodedKeySpec(key);
        return getPrivateKeyBCEC(peks);
    }

    /**
     * 获取BCEC私钥（SEC1格式）
     * @param key 私钥（SEC1格式）
     */
    public static BCECPrivateKey getPrivateKeyBCECSEC1(byte[] key) {
        PKCS8EncodedKeySpec peks = new PKCS8EncodedKeySpec(convertECPrivateKeySEC1ToPKCS8(key));
        return getPrivateKeyBCEC(peks);
    }

    private static BCECPrivateKey getPrivateKeyBCEC(PKCS8EncodedKeySpec peks) {
        try {
            KeyFactory kf = KeyFactory.getInstance(Algorithm.EC.getAlgorithm(), BouncyCastleProvider.PROVIDER_NAME);
            return (BCECPrivateKey) kf.generatePrivate(peks);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将ECC私钥转换为PKCS8标准的字节流
     * @param publicKey 可以为空，但是如果为空的话得到的结果OpenSSL可能解析不了
     */
    public static byte[] convertECPrivateKeyToPKCS8(ECPrivateKeyParameters priKey, ECPublicKeyParameters publicKey) {
        ECDomainParameters domainParams = priKey.getParameters();
        ECParameterSpec spec = new ECParameterSpec(domainParams.getCurve(), domainParams.getG(), domainParams.getN(), domainParams.getH());
        BCECPublicKey bcecPublicKey = null;
        if (publicKey != null) {
            bcecPublicKey = new BCECPublicKey(Algorithm.EC.getAlgorithm(), publicKey, spec, BouncyCastleProvider.CONFIGURATION);
        }
        BCECPrivateKey privateKey = new BCECPrivateKey(Algorithm.EC.getAlgorithm(), priKey, bcecPublicKey, spec, BouncyCastleProvider.CONFIGURATION);
        return privateKey.getEncoded();
    }

    /**
     * 将PKCS8标准的私钥字节流转换为PEM
     */
    public static String convertECPrivateKeyPKCS8ToPEM(byte[] encodedKey) {
        return convertEncodedDataToPEM(PEM_STRING_ECPRIVATEKEY, encodedKey);
    }

    /**
     * 将PEM格式的私钥转换为PKCS8标准字节流
     */
    public static byte[] convertECPrivateKeyPEMToPKCS8(String pemString) {
        return convertPEMToEncodedData(pemString);
    }

    /**
     * 将ECC私钥转换为SEC1标准的字节流
     * 用来生成openssl能识别的ECC私钥（openssl d2i_ECPrivateKey函数要求的DER编码的私钥必须是SEC1标准的）
     * 相对RSA私钥的PKCS1标准，ECC私钥的标准为SEC1
     */
    public static byte[] convertECPrivateKeyToSEC1(ECPrivateKeyParameters priKey, ECPublicKeyParameters publicKey) {
        byte[] pkcs8Bytes = convertECPrivateKeyToPKCS8(priKey, publicKey);
        PrivateKeyInfo pki = PrivateKeyInfo.getInstance(pkcs8Bytes);
        try {
            ASN1Encodable encodable = pki.parsePrivateKey();
            ASN1Primitive primitive = encodable.toASN1Primitive();
            return primitive.getEncoded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将SEC1标准的私钥字节流恢复为PKCS8标准的字节流
     */
    public static byte[] convertECPrivateKeySEC1ToPKCS8(byte[] sec1Key) {
        //参考org.bouncycastle.asn1.pkcs.PrivateKeyInfo和org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey，逆向拼装
        X962Parameters params = getDomainParametersFromName(SM2Utils.JDK_EC_SPEC, false);
        ASN1OctetString privKey = new DEROctetString(sec1Key);
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(0)); //版本号
        v.add(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params)); //算法标识
        v.add(privKey);
        DERSequence ds = new DERSequence(v);
        try {
            return ds.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将ECC公钥对象转换为X509标准的字节流
     */
    public static byte[] convertECPublicKeyToX509(ECPublicKeyParameters publicKey) {
        ECDomainParameters domainParams = publicKey.getParameters();
        ECParameterSpec spec = new ECParameterSpec(domainParams.getCurve(), domainParams.getG(), domainParams.getN(), domainParams.getH());
        BCECPublicKey bcecPublicKey = new BCECPublicKey(Algorithm.EC.getAlgorithm(), publicKey, spec, BouncyCastleProvider.CONFIGURATION);
        return bcecPublicKey.getEncoded();
    }

    /**
     * 将X509标准的公钥字节流转为PEM
     */
    public static String convertECPublicKeyX509ToPEM(byte[] encodedKey) {
        return convertEncodedDataToPEM(PEM_STRING_PUBLIC, encodedKey);
    }

    /**
     * 将PEM格式的公钥转为X509标准的字节流
     */
    public static byte[] convertECPublicKeyPEMToX509(String pemString) {
        return convertPEMToEncodedData(pemString);
    }

    /**
     * copy from BC
     */
    public static X9ECParameters getDomainParametersFromGenSpec(ECGenParameterSpec genSpec) {
        return getDomainParametersFromName(genSpec.getName());
    }

    /**
     * copy from BC
     */
    private static X9ECParameters getDomainParametersFromName(String curveName) {
        X9ECParameters domainParameters;
        try {
            if (curveName.charAt(0) >= '0' && curveName.charAt(0) <= '2') {
                ASN1ObjectIdentifier oidID = new ASN1ObjectIdentifier(curveName);
                domainParameters = ECUtil.getNamedCurveByOid(oidID);
            } else {
                if (curveName.indexOf(' ') > 0) {
                    curveName = curveName.substring(curveName.indexOf(' ') + 1);
                    domainParameters = ECUtil.getNamedCurveByName(curveName);
                } else {
                    domainParameters = ECUtil.getNamedCurveByName(curveName);
                }
            }
        } catch (IllegalArgumentException ex) {
            domainParameters = ECUtil.getNamedCurveByName(curveName);
        }
        return domainParameters;
    }

    /**
     * copy from BC
     */
    private static X962Parameters getDomainParametersFromName(java.security.spec.ECParameterSpec ecSpec, boolean withCompression) {
        X962Parameters params;
        if (ecSpec instanceof ECNamedCurveSpec) {
            ASN1ObjectIdentifier curveOid = ECUtil.getNamedCurveOid(((ECNamedCurveSpec) ecSpec).getName());
            if (curveOid == null) {
                curveOid = new ASN1ObjectIdentifier(((ECNamedCurveSpec) ecSpec).getName());
            }
            params = new X962Parameters(curveOid);
        } else if (ecSpec == null) {
            params = new X962Parameters(DERNull.INSTANCE);
        } else {
            ECCurve curve = EC5Util.convertCurve(ecSpec.getCurve());

            X9ECParameters ecP = new X9ECParameters(curve, EC5Util.convertPoint(curve, ecSpec.getGenerator(), withCompression),
                    ecSpec.getOrder(), BigInteger.valueOf(ecSpec.getCofactor()), ecSpec.getCurve().getSeed());
            params = new X962Parameters(ecP);
        }
        return params;
    }

    private static String convertEncodedDataToPEM(String type, byte[] encodedData) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        try (PemWriter pWrt = new PemWriter(new OutputStreamWriter(bOut))) {
            PemObject pemObj = new PemObject(type, encodedData);
            pWrt.writeObject(pemObj);
            return new String(bOut.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] convertPEMToEncodedData(String pemString) {
        ByteArrayInputStream bIn = new ByteArrayInputStream(pemString.getBytes());
        try (InputStreamReader inputStreamReader = new InputStreamReader(bIn);
             PemReader pRdr = new PemReader(inputStreamReader)) {
            PemObject pemObject = pRdr.readPemObject();
            return pemObject.getContent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] extractBytes(byte[] src, int offset, int length) {
        byte[] result = new byte[length];
        System.arraycopy(src, offset, result, 0, result.length);
        return result;
    }

    private static byte[] fixToCurveLengthBytes(byte[] src) {
        return fixToCurveLengthBytes(sm2P256V1Curve_LEN, src);
    }

    private static byte[] fixToCurveLengthBytes(int curveLength, byte[] src) {
        if (src.length == curveLength) {
            return src;
        }

        byte[] result = new byte[curveLength];
        if (src.length > curveLength) {
            System.arraycopy(src, src.length - result.length, result, 0, result.length);
        } else {
            System.arraycopy(src, result.length - src.length, result, 0, src.length);
        }
        return result;
    }

    public static int getCurveLength(ECKeyParameters ecKey) {
        return getCurveLength(ecKey.getParameters());
    }

    static int getCurveLength(ECDomainParameters domainParams) {
        return (domainParams.getCurve().getFieldSize() + 7) / 8;
    }

}
