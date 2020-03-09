import com.dxy.library.util.cipher.constant.Algorithm;
import com.dxy.library.util.cipher.constant.Mode;
import com.dxy.library.util.cipher.constant.Padding;
import com.dxy.library.util.cipher.symmetry.DESedeUtils;
import org.junit.Test;

import java.time.Clock;
import java.util.Arrays;

/**
 * @author duanxinyuan
 * 2019/2/20 21:41
 */
public class DESedeTest {

    private String content = "12345678";
    private String key = "hello world, hi!";
    private String iv = "01020304";

    @Test
    public void getAlgorithm() {
        System.out.println(Algorithm.getAlgorithm(Algorithm.DESede, Mode.CBC, Padding.PKCS7Padding));
        System.out.println(Algorithm.getAlgorithm(Algorithm.DESede, Mode.ECB, Padding.NoPadding));
    }

    @Test
    public void generateKey() {
        System.out.println(DESedeUtils.encrypt(content, key, Mode.ECB, Padding.NoPadding));
        System.out.println(Arrays.toString(DESedeUtils.generateKey()));
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
        System.out.println(DESedeUtils.encrypt(content, key));
        System.out.println(DESedeUtils.decrypt(DESedeUtils.encrypt(content, key), key));

        System.out.println(DESedeUtils.encrypt(content, key, iv));
        System.out.println(DESedeUtils.decrypt(DESedeUtils.encrypt(content, key, iv), key, iv));

        System.out.println(DESedeUtils.encrypt(content, key, Mode.ECB, Padding.NoPadding));
        System.out.println(DESedeUtils.decrypt(DESedeUtils.encrypt(content, key, Mode.ECB, Padding.NoPadding), key, Mode.ECB, Padding.NoPadding));

        System.out.println(DESedeUtils.encrypt(content, key, Mode.ECB, Padding.PKCS7Padding));
        System.out.println(DESedeUtils.decrypt(DESedeUtils.encrypt(content, key, Mode.ECB, Padding.PKCS7Padding), key, Mode.ECB, Padding.PKCS7Padding));

        System.out.println(DESedeUtils.encrypt(content, key, iv, Mode.CBC, Padding.NoPadding));
        System.out.println(DESedeUtils.decrypt(DESedeUtils.encrypt(content, key, iv, Mode.CBC, Padding.NoPadding), key, iv, Mode.CBC, Padding.NoPadding));

        System.out.println(DESedeUtils.encrypt(content, key, iv, Mode.CBC, Padding.PKCS7Padding));
        System.out.println(DESedeUtils.decrypt(DESedeUtils.encrypt(content, key, iv, Mode.CBC, Padding.PKCS7Padding), key, iv, Mode.CBC, Padding.PKCS7Padding));

        System.out.println(DESedeUtils.encrypt(content, key, iv, Mode.CFB, Padding.PKCS7Padding));
        System.out.println(DESedeUtils.decrypt(DESedeUtils.encrypt(content, key, iv, Mode.CFB, Padding.PKCS7Padding), key, iv, Mode.CFB, Padding.PKCS7Padding));

        System.out.println(DESedeUtils.encrypt(content, key, iv, Mode.CTR, Padding.PKCS7Padding));
        System.out.println(DESedeUtils.decrypt(DESedeUtils.encrypt(content, key, iv, Mode.CTR, Padding.PKCS7Padding), key, iv, Mode.CTR, Padding.PKCS7Padding));

        System.out.println(DESedeUtils.encrypt(content, key, iv, Mode.OFB, Padding.PKCS7Padding));
        System.out.println(DESedeUtils.decrypt(DESedeUtils.encrypt(content, key, iv, Mode.OFB, Padding.PKCS7Padding), key, iv, Mode.OFB, Padding.PKCS7Padding));
    }


}
