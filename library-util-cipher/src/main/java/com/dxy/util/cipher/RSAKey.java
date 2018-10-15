package com.dxy.util.cipher;

import lombok.Data;

/**
 * RSA公私钥
 * @author duanxinyuan
 * 2018/10/15 16:19
 */
@Data
public class RSAKey {

    private String publicKey;

    private String privateKey;

    private String modules;

}
