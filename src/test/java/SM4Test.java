import com.dxy.library.util.cipher.constant.Algorithm;
import com.dxy.library.util.cipher.constant.Mode;
import com.dxy.library.util.cipher.constant.Padding;
import com.dxy.library.util.cipher.symmetry.SM4Utils;
import org.junit.Test;

import java.time.Clock;
import java.util.Arrays;

/**
 * @author duanxinyuan
 * 2019/2/25 15:52
 */
public class SM4Test {

    private String content = "0123456789ABCDEF";
    private String key = "hello world, hi!";
    private String iv = "0102030405060708";

    @Test
    public void getAlgorithm() {
        System.out.println(Algorithm.getAlgorithm(Algorithm.SM4, Mode.CBC, Padding.PKCS7Padding));
        System.out.println(Algorithm.getAlgorithm(Algorithm.SM4, Mode.ECB, Padding.NoPadding));
    }

    @Test
    public void generateKey() {
        System.out.println(SM4Utils.encrypt(content, key, Mode.ECB, Padding.NoPadding));
        System.out.println(Arrays.toString(SM4Utils.generateKey()));
    }

    @Test
    public void testTime() {
        long millis = Clock.systemUTC().millis();
        for (int i = 0; i < 10000; i++) {
            test();
        }
        System.out.println(Clock.systemUTC().millis() - millis);
    }

    @Test
    public void test() {
        System.out.println(SM4Utils.encrypt(content, key));
        System.out.println(SM4Utils.decrypt(SM4Utils.encrypt(content, key), key));

        System.out.println(SM4Utils.encrypt(content, key, iv));
        System.out.println(SM4Utils.decrypt(SM4Utils.encrypt(content, key, iv), key, iv));

        System.out.println(SM4Utils.encrypt(content, key, Mode.ECB, Padding.NoPadding));
        System.out.println(SM4Utils.decrypt(SM4Utils.encrypt(content, key, Mode.ECB, Padding.NoPadding), key, Mode.ECB, Padding.NoPadding));

        System.out.println(SM4Utils.encrypt(content, key, Mode.ECB, Padding.PKCS7Padding));
        System.out.println(SM4Utils.decrypt(SM4Utils.encrypt(content, key, Mode.ECB, Padding.PKCS7Padding), key, Mode.ECB, Padding.PKCS7Padding));

        System.out.println(SM4Utils.encrypt(content, key, iv, Mode.CBC, Padding.NoPadding));
        System.out.println(SM4Utils.decrypt(SM4Utils.encrypt(content, key, iv, Mode.CBC, Padding.NoPadding), key, iv, Mode.CBC, Padding.NoPadding));

        System.out.println(SM4Utils.encrypt(content, key, iv, Mode.CBC, Padding.PKCS7Padding));
        System.out.println(SM4Utils.decrypt(SM4Utils.encrypt(content, key, iv, Mode.CBC, Padding.PKCS7Padding), key, iv, Mode.CBC, Padding.PKCS7Padding));

        System.out.println(SM4Utils.encrypt(content, key, iv, Mode.CFB, Padding.PKCS7Padding));
        System.out.println(SM4Utils.decrypt(SM4Utils.encrypt(content, key, iv, Mode.CFB, Padding.PKCS7Padding), key, iv, Mode.CFB, Padding.PKCS7Padding));

        System.out.println(SM4Utils.encrypt(content, key, iv, Mode.CTR, Padding.PKCS7Padding));
        System.out.println(SM4Utils.decrypt(SM4Utils.encrypt(content, key, iv, Mode.CTR, Padding.PKCS7Padding), key, iv, Mode.CTR, Padding.PKCS7Padding));

        System.out.println(SM4Utils.encrypt(content, key, iv, Mode.OFB, Padding.PKCS7Padding));
        System.out.println(SM4Utils.decrypt(SM4Utils.encrypt(content, key, iv, Mode.OFB, Padding.PKCS7Padding), key, iv, Mode.OFB, Padding.PKCS7Padding));

    }

}
