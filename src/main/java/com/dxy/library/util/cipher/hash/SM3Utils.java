package com.dxy.library.util.cipher.hash;

import com.dxy.library.util.cipher.constant.Algorithm;
import com.dxy.library.util.cipher.exception.CipherException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.Security;

/**
 * SM3工具类
 * SM3密码杂凑算法，类似MD5
 * @author duanxinyuan
 * 2019/2/25 14:59
 */
public class SM3Utils {

    static {
        //导入Provider，BouncyCastle是一个开源的加解密解决方案，主页在http://www.bouncycastle.org/
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * SM3加密
     * @param data 加密内容
     * @return 密文（16进制字符串）
     */
    public static String sm3(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance(Algorithm.SM3.getAlgorithm());
            byte[] bytes = md.digest(data.getBytes());
            return Hex.encodeHexString(bytes);
        } catch (Exception e) {
            throw new CipherException(e);
        }
    }

}
