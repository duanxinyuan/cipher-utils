import com.dxy.library.util.cipher.constant.Algorithm;
import com.dxy.library.util.cipher.constant.Mode;
import com.dxy.library.util.cipher.constant.Padding;
import com.dxy.library.util.cipher.symmetry.BlowfishUtils;
import org.junit.Test;

import java.time.Clock;
import java.util.Arrays;

/**
 * @author duanxinyuan
 * 2019/2/19 21:29
 */
public class BlowfishTest {

    private String content = "12345678";
    private String key = "hello world, hi!";
    private String iv = "01020304";

    @Test
    public void getAlgorithm() {
        System.out.println(Algorithm.getAlgorithm(Algorithm.Blowfish, Mode.CBC, Padding.PKCS7Padding));
        System.out.println(Algorithm.getAlgorithm(Algorithm.Blowfish, Mode.ECB, Padding.NoPadding));
    }

    @Test
    public void generateKey() {
        System.out.println(Arrays.toString(BlowfishUtils.generateKey()));
        System.out.println(Arrays.toString(BlowfishUtils.generateKey(256)));
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
        System.out.println(BlowfishUtils.encrypt(content, key));
        System.out.println(BlowfishUtils.decrypt(BlowfishUtils.encrypt(content, key), key));

        System.out.println(BlowfishUtils.encrypt(content, key, iv));
        System.out.println(BlowfishUtils.decrypt(BlowfishUtils.encrypt(content, key, iv), key, iv));

        System.out.println(BlowfishUtils.encrypt(content, key, Mode.ECB, Padding.NoPadding));
        System.out.println(BlowfishUtils.decrypt(BlowfishUtils.encrypt(content, key, Mode.ECB, Padding.NoPadding), key, Mode.ECB, Padding.NoPadding));

        System.out.println(BlowfishUtils.encrypt(content, key, Mode.ECB, Padding.PKCS7Padding));
        System.out.println(BlowfishUtils.decrypt(BlowfishUtils.encrypt(content, key, Mode.ECB, Padding.PKCS7Padding), key, Mode.ECB, Padding.PKCS7Padding));

        System.out.println(BlowfishUtils.encrypt(content, key, iv, Mode.CBC, Padding.NoPadding));
        System.out.println(BlowfishUtils.decrypt(BlowfishUtils.encrypt(content, key, iv, Mode.CBC, Padding.NoPadding), key, iv, Mode.CBC, Padding.NoPadding));

        System.out.println(BlowfishUtils.encrypt(content, key, iv, Mode.CBC, Padding.PKCS7Padding));
        System.out.println(BlowfishUtils.decrypt(BlowfishUtils.encrypt(content, key, iv, Mode.CBC, Padding.PKCS7Padding), key, iv, Mode.CBC, Padding.PKCS7Padding));

        System.out.println(BlowfishUtils.encrypt(content, key, iv, Mode.CFB, Padding.PKCS7Padding));
        System.out.println(BlowfishUtils.decrypt(BlowfishUtils.encrypt(content, key, iv, Mode.CFB, Padding.PKCS7Padding), key, iv, Mode.CFB, Padding.PKCS7Padding));

        System.out.println(BlowfishUtils.encrypt(content, key, iv, Mode.CTR, Padding.PKCS7Padding));
        System.out.println(BlowfishUtils.decrypt(BlowfishUtils.encrypt(content, key, iv, Mode.CTR, Padding.PKCS7Padding), key, iv, Mode.CTR, Padding.PKCS7Padding));

        System.out.println(BlowfishUtils.encrypt(content, key, iv, Mode.OFB, Padding.PKCS7Padding));
        System.out.println(BlowfishUtils.decrypt(BlowfishUtils.encrypt(content, key, iv, Mode.OFB, Padding.PKCS7Padding), key, iv, Mode.OFB, Padding.PKCS7Padding));

    }

}
