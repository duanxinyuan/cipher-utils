import com.dxy.library.util.cipher.asymmetry.SM2Utils;
import com.dxy.library.util.cipher.pojo.SM2KeyPair;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author duanxinyuan
 * 2019/2/21 20:56
 */
public class SM2Test {

    private byte[] content = "hello world".getBytes();
    private byte[] withId = "123456".getBytes();

    @Test
    public void generateKey() {
        SM2KeyPair sm2KeyPair = SM2Utils.generateKey();

        System.out.println("私钥:" + ByteUtils.toHexString(sm2KeyPair.getPrivateKey().toByteArray()).toUpperCase());
        System.out.println("公钥点X坐标:" + ByteUtils.toHexString(sm2KeyPair.getPublicKey().getAffineXCoord().getEncoded()).toUpperCase());
        System.out.println("公钥点Y坐标:" + ByteUtils.toHexString(sm2KeyPair.getPublicKey().getAffineYCoord().getEncoded()).toUpperCase());
        System.out.println("公钥点:" + ByteUtils.toHexString(sm2KeyPair.getPublicKey().getEncoded(false)).toUpperCase());
    }

    @Test
    public void test() {
        SM2KeyPair sm2KeyPair = SM2Utils.generateKey();

        //加密
        byte[] encryptedData = SM2Utils.encrypt(sm2KeyPair.getPublicKeyParameters(), content);
        System.out.println(Arrays.toString(encryptedData));

        //解密
        byte[] decryptedData = SM2Utils.decrypt(sm2KeyPair.getPrivateKeyParameters(), encryptedData);
        System.out.println(new String(decryptedData));

        //密文DER编码
        byte[] derCipher = SM2Utils.encodeCipherToDER(encryptedData);
        System.out.println(new String(derCipher));

        //密文DER解码
        byte[] derDecryptedData = SM2Utils.decrypt(sm2KeyPair.getPrivateKeyParameters(), SM2Utils.decodeDERCipher(derCipher));
        System.out.println(new String(derDecryptedData));
    }

    @Test
    public void sign() {
        SM2KeyPair sm2KeyPair = SM2Utils.generateKey();

        byte[] sign = SM2Utils.sign(sm2KeyPair.getPrivateKeyParameters(), withId, content);
        System.out.println(Arrays.toString(sign));
        System.out.println(SM2Utils.verify(sm2KeyPair.getPublicKeyParameters(), withId, content, sign));

        byte[] sign1 = SM2Utils.sign(sm2KeyPair.getPrivateKeyParameters(), content);
        System.out.println(Arrays.toString(sign1));
        System.out.println(SM2Utils.verify(sm2KeyPair.getPublicKeyParameters(), content, sign1));

        //签名DER编解码
        byte[] decodeDERSign = SM2Utils.decodeDERSign(sign);
        System.out.println(Arrays.toString(decodeDERSign));
        byte[] encodeSignToDER = SM2Utils.encodeSignToDER(decodeDERSign);
        System.out.println(Arrays.toString(encodeSignToDER));
    }

}
