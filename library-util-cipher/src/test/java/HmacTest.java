import com.dxy.library.util.cipher.constant.HmacType;
import com.dxy.library.util.cipher.hash.HmacUtils;
import org.junit.Test;

/**
 * @author duanxinyuan
 * 2019/2/26 23:01
 */
public class HmacTest {
    private String content = "hello world";

    @Test
    public void genarateKey() {
        System.out.println(HmacUtils.genarateKeyHex(HmacType.HmacMD5));
        System.out.println(HmacUtils.genarateKeyHex(HmacType.HmacSHA1));
        System.out.println(HmacUtils.genarateKeyHex(HmacType.HmacSHA224));
        System.out.println(HmacUtils.genarateKeyHex(HmacType.HmacSHA256));
        System.out.println(HmacUtils.genarateKeyHex(HmacType.HmacSHA384));
        System.out.println(HmacUtils.genarateKeyHex(HmacType.HmacSHA512));

        System.out.println();

        System.out.println(HmacUtils.genarateHmacMD5KeyHex());
        System.out.println(HmacUtils.genarateHmacSHA1KeyHex());
        System.out.println(HmacUtils.genarateHmacSHA224KeyHex());
        System.out.println(HmacUtils.genarateHmacSHA256KeyHex());
        System.out.println(HmacUtils.genarateHmacSHA384KeyHex());
        System.out.println(HmacUtils.genarateHmacSHA512KeyHex());
    }

    @Test
    public void test() {
        System.out.println(HmacUtils.hmacMD5(content, HmacUtils.genarateHmacMD5KeyHex()));
        System.out.println(HmacUtils.hmacSHA1(content, HmacUtils.genarateHmacSHA1KeyHex()));
        System.out.println(HmacUtils.hmacSHA224(content, HmacUtils.genarateHmacSHA224KeyHex()));
        System.out.println(HmacUtils.hmacSHA256(content, HmacUtils.genarateHmacSHA256KeyHex()));
        System.out.println(HmacUtils.hmacSHA384(content, HmacUtils.genarateHmacSHA384KeyHex()));
        System.out.println(HmacUtils.hmacSHA512(content, HmacUtils.genarateHmacSHA512KeyHex()));
    }

}
