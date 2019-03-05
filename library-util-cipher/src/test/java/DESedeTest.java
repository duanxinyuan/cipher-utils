import com.dxy.library.util.cipher.constant.Algorithm;
import com.dxy.library.util.cipher.constant.AlgorithmUtils;
import com.dxy.library.util.cipher.constant.Mode;
import com.dxy.library.util.cipher.constant.Padding;
import com.dxy.library.util.cipher.symmetry.DESedeUtils;
import org.junit.Test;

import java.time.Clock;

/**
 * @author duanxinyuan
 * 2019/2/20 21:41
 */
public class DESedeTest {

    private String content = "hello world";
    private String key = "bVeeVCqhp/KPq17mOEXC1v5d9xBRKf7j";
    private String iv = "01020304";

    @Test
    public void getAlgorithm() {
        System.out.println(AlgorithmUtils.getAlgorithm(Algorithm.DESede, Mode.CBC, Padding.PKCS7Padding));
        System.out.println(AlgorithmUtils.getAlgorithm(Algorithm.DESede, Mode.ECB, Padding.NoPadding));
    }

    @Test
    public void genarateKey() {
        System.out.println(DESedeUtils.genarateKeyBase64());
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

        System.out.println(DESedeUtils.encrypt(content, key, Mode.ECB, Padding.PKCS7Padding));
        System.out.println(DESedeUtils.decrypt(DESedeUtils.encrypt(content, key, Mode.ECB, Padding.PKCS7Padding), key, Mode.ECB, Padding.PKCS7Padding));

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
