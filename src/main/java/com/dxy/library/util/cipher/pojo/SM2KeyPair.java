package com.dxy.library.util.cipher.pojo;

import lombok.Data;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

/**
 * SM2公私钥
 * @author duanxinyuan
 * 2019/2/25 14:13
 */
@Data
public class SM2KeyPair {

    //公钥（X509格式）
    private String publicKey;

    //私钥（PKCS8格式）
    private String privateKey;

    //公钥
    private ECPublicKey ecPublicKey;

    //私钥
    private ECPrivateKey ecPrivateKey;

}
