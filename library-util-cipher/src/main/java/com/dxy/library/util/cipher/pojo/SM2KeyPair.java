package com.dxy.library.util.cipher.pojo;

import lombok.Data;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

/**
 * SM2公私钥
 * @author duanxinyuan
 * 2019/2/25 14:13
 */
@Data
public class SM2KeyPair {

    //公钥点（公钥），满足y^2=x^3-x
    private ECPoint publicKey;

    //倍数（私钥）
    private BigInteger privateKey;

    //私钥
    private ECPrivateKeyParameters privateKeyParameters;

    //公钥
    private ECPublicKeyParameters publicKeyParameters;

}
