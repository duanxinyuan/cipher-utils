import com.dxy.library.util.cipher.asymmetry.RSAUtils;
import com.dxy.library.util.cipher.constant.Mode;
import com.dxy.library.util.cipher.constant.Padding;
import com.dxy.library.util.cipher.constant.RSASignType;
import com.dxy.library.util.cipher.pojo.RSAKeyPair;
import org.junit.Test;

import java.time.Clock;

/**
 * @author duanxinyuan
 * 2019/2/20 22:30
 */
public class RSATest {
    private String content = "hello world";

    private String modules = "AMC4hLGE4ksIH2vfLCXV8Z199pmKB1QqmY6dQRx41IYLj1btbblZO9x5gE6pdiyubIZxLKpMsrjc28DjbVcqymOpkdkjIuMm80ERNpDRBA92QAnA1axh0LXDtEPjkWUdYqi2MZeeTAFyQQr6OECRGOIou51oEgKFuxd7kFkgAnin";

    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDAuISxhOJLCB9r3ywl1fGdffaZigdUKpmOnUEceNSGC49W7W25WTvceYBOqXYsrmyGcSyqTLK43NvA421XKspjqZHZIyLjJvNBETaQ0QQPdkAJwNWsYdC1w7RD45FlHWKotjGXnkwBckEK+jhAkRjiKLudaBIChbsXe5BZIAJ4pwIDAQAB";

    private String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMC4hLGE4ksIH2vfLCXV8Z199pmKB1QqmY6dQRx41IYLj1btbblZO9x5gE6pdiyubIZxLKpMsrjc28DjbVcqymOpkdkjIuMm80ERNpDRBA92QAnA1axh0LXDtEPjkWUdYqi2MZeeTAFyQQr6OECRGOIou51oEgKFuxd7kFkgAninAgMBAAECgYBvy1nmJGClB9w6Viak+BuFsalYXdJsh522NhCsNIeMDq6izW5GA7aO6ch9WR5dQv1fa81uKLnQNQYhOYyW8RKfhbcWMXY87PGqmgovp95fIDPZg4JPdjOtYa0jWCkPwTJtCxd7tRUpgEQwL+OrBThUeIcbitGFnFN0Dh9Fd8eXmQJBAOdmqQC1rO41QcJaUXb5BYs0JBTsn8XzRubYJrA2/nG1J890ZflMgbiy0+J551DxxpQdzBjw9zJ7Nv81bzxuacMCQQDVNTce2Rk+qEAFqkA2mlCBSP0Fo8rBWeyWcojCD3lsLumixV0IlCvU9tzTQvA3kk4/L+2CyKqkUwuowXxv3SNNAkEAw8kOkTUl/d49p013w+vqjt8s8C9M99VgVgzonwvIuTiHWHWpmgbrcvSLZgGyf8AxPjz/5NJstN+fpsr1NPJOtQJAFLn2oM4UES5EAwj48xXvS5Iv3rN8i21VfY6m0s60TBsHZWZwU9wroqlY8ESqm4xihOwA19zwEcds87vTgABsZQJAQelvWjab1VsavHm6uoRhVSB8rcsEwvUz+lqY9qGREt0m4G+4USMdCsn+pfG8hSV5kQGcHFBa0cCM40S1Hh9mZA==";

    @Test
    public void generateKey() {
        RSAKeyPair rsaKeyPair = RSAUtils.generateKey();
        System.out.println(rsaKeyPair.getModules());
        System.out.println(rsaKeyPair.getPublicKey());
        System.out.println(rsaKeyPair.getPrivateKey());
    }

    @Test
    public void testTime() {
        System.out.println(Clock.systemUTC().millis());
        test1();
        System.out.println(Clock.systemUTC().millis());
        test2();
        System.out.println(Clock.systemUTC().millis());
    }

    @Test
    public void testSign() {
        String sign = RSAUtils.sign(RSASignType.SHA256withRSA, content, privateKey);
        System.out.println(sign);
        System.out.println(RSAUtils.verifySign(RSASignType.SHA256withRSA, content, publicKey, sign));
    }

    @Test
    public void test1() {
        //私钥加密，公钥解密
        String encrypt = RSAUtils.encryptByPrivateKey(content, privateKey);
        System.out.println(encrypt);
        System.out.println(RSAUtils.decryptByPublicKey(encrypt, publicKey));
    }

    @Test
    public void test2() {
        //公钥加密，私钥解密
        String encrypt1 = RSAUtils.encryptByPublicKey(content, publicKey);
        System.out.println(encrypt1);
        System.out.println(RSAUtils.decryptByPrivateKey(encrypt1, privateKey));
    }

    @Test
    public void test21() {
        //公钥加密，私钥解密
        System.out.println(RSAUtils.encryptByPublicKey(content, publicKey));
        System.out.println(RSAUtils.decryptByPrivateKey(RSAUtils.encryptByPublicKey(content, publicKey), privateKey));

        System.out.println(RSAUtils.encryptByPublicKey(content, publicKey, Mode.NONE, Padding.PKCS1Padding));
        System.out.println(RSAUtils.decryptByPrivateKey(RSAUtils.encryptByPublicKey(content, publicKey, Mode.NONE, Padding.PKCS1Padding), privateKey));

        System.out.println(RSAUtils.encryptByPublicKey(content, publicKey, Mode.ECB, Padding.NoPadding));
        System.out.println(RSAUtils.decryptByPrivateKey(RSAUtils.encryptByPublicKey(content, publicKey, Mode.ECB, Padding.PKCS1Padding), privateKey));

        System.out.println(RSAUtils.encryptByPublicKey(content, publicKey, Mode.ECB, Padding.PKCS1Padding));
        System.out.println(RSAUtils.decryptByPrivateKey(RSAUtils.encryptByPublicKey(content, publicKey, Mode.ECB, Padding.PKCS1Padding), privateKey));

        //私钥加密，公钥解密
        System.out.println(RSAUtils.encryptByPrivateKey(content, privateKey));
        System.out.println(RSAUtils.decryptByPublicKey(RSAUtils.encryptByPrivateKey(content, privateKey), publicKey));

        System.out.println(RSAUtils.encryptByPrivateKey(content, privateKey, Mode.NONE, Padding.PKCS1Padding));
        System.out.println(RSAUtils.decryptByPublicKey(RSAUtils.encryptByPrivateKey(content, privateKey, Mode.NONE, Padding.PKCS1Padding), publicKey));

        System.out.println(RSAUtils.encryptByPrivateKey(content, privateKey, Mode.ECB, Padding.NoPadding));
        System.out.println(RSAUtils.decryptByPublicKey(RSAUtils.encryptByPrivateKey(content, privateKey, Mode.ECB, Padding.PKCS1Padding), publicKey));

        System.out.println(RSAUtils.encryptByPrivateKey(content, privateKey, Mode.ECB, Padding.PKCS1Padding));
        System.out.println(RSAUtils.decryptByPublicKey(RSAUtils.encryptByPrivateKey(content, privateKey, Mode.ECB, Padding.PKCS1Padding), publicKey));
    }

    @Test
    public void testProperty() {
        RSAKeyPair rsaKeyPair = RSAUtils.generateKey();

        int count = 10000;

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            RSAUtils.encryptByPublicKey(content, rsaKeyPair.getPublicKey());
        }
        System.out.println("加密" + count + "次，耗时" + (System.currentTimeMillis() - startTime) + "毫秒");

        //加密
        String encryptedData = RSAUtils.encryptByPublicKey(content, rsaKeyPair.getPublicKey());
        System.out.println(encryptedData);

        //解密
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            RSAUtils.decryptByPrivateKey(encryptedData, rsaKeyPair.getPrivateKey());
        }
        System.out.println("解密" + count + "次，耗时" + (System.currentTimeMillis() - startTime) + "毫秒");
    }

}
