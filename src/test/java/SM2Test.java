import com.dxy.library.util.cipher.asymmetry.SM2Utils;
import com.dxy.library.util.cipher.constant.SM2SignType;
import com.dxy.library.util.cipher.pojo.SM2KeyPair;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author duanxinyuan
 * 2019/2/21 20:56
 */
public class SM2Test {

    private String content = "hello world";

    @Test
    public void generateKey() {
        SM2KeyPair sm2KeyPair = SM2Utils.generateKey(128);
        System.out.println(sm2KeyPair.getPublicKey());
        System.out.println(sm2KeyPair.getPrivateKey());

        System.out.println("私钥:" + ByteUtils.toHexString(sm2KeyPair.getEcPrivateKey().getS().toByteArray()).toUpperCase());
        System.out.println("公钥点X坐标:" + sm2KeyPair.getEcPublicKey().getW().getAffineX());
        System.out.println("公钥点Y坐标:" + sm2KeyPair.getEcPublicKey().getW().getAffineY());
        System.out.println("公钥点:" + ByteUtils.toHexString(sm2KeyPair.getEcPublicKey().getEncoded()).toUpperCase());
    }

    @Test
    public void test() {
        SM2KeyPair sm2KeyPair = SM2Utils.generateKey();

        //加密
        String encryptedData = SM2Utils.encrypt(content, sm2KeyPair.getEcPublicKey());
        System.out.println(encryptedData);

        //解密
        String decryptedData = SM2Utils.decrypt(encryptedData, sm2KeyPair.getEcPrivateKey());
        System.out.println(decryptedData);
        Assert.assertEquals(decryptedData, content);

        //加密
        String encryptedData1 = SM2Utils.encrypt(content, sm2KeyPair.getPublicKey());
        System.out.println(encryptedData1);

        //解密
        String decryptedData1 = SM2Utils.decrypt(encryptedData1, sm2KeyPair.getPrivateKey());
        System.out.println(decryptedData1);
        Assert.assertEquals(decryptedData1, content);
    }

    @Test
    public void testProperty() {
        SM2KeyPair sm2KeyPair = SM2Utils.generateKey();

        int count = 10000;

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            SM2Utils.encrypt(content, sm2KeyPair.getEcPublicKey());
        }
        System.out.println("加密" + count + "次，耗时" + (System.currentTimeMillis() - startTime) + "毫秒");

        //加密
        String encryptedData = SM2Utils.encrypt(content, sm2KeyPair.getEcPublicKey());
        System.out.println(encryptedData);

        //解密
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            SM2Utils.decrypt(encryptedData, sm2KeyPair.getEcPrivateKey());
        }
        System.out.println("解密" + count + "次，耗时" + (System.currentTimeMillis() - startTime) + "毫秒");
    }

    @Test
    public void sign() {
        SM2KeyPair sm2KeyPair = SM2Utils.generateKey();

        String sign = SM2Utils.sign(SM2SignType.SHA256withSM2, content, sm2KeyPair.getEcPrivateKey());
        System.out.println(sign);
        System.out.println(SM2Utils.verifySign(SM2SignType.SHA256withSM2, content, sm2KeyPair.getEcPublicKey(), sign));
    }

}
