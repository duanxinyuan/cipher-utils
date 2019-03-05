package com.dxy.library.util.cipher.pojo;

import lombok.Data;

/**
 * SM2密文对象
 * @author duanxinyuan
 * 2019/2/21 20:25
 */
@Data
public class SM2Cipher {

    //ECC密钥
    private byte[] c1;

    //真正的密文
    private byte[] c2;

    //对（c1+c2）的SM3-HASH值
    private byte[] c3;

    //SM2标准的密文，即（c1+c2+c3）
    private byte[] cipherText;

}
