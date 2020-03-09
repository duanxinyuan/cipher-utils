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
    public void generateKey() {
        System.out.println(HmacUtils.generateKeyHex(HmacType.HmacMD5));
        System.out.println(HmacUtils.generateKeyHex(HmacType.HmacSHA1));
        System.out.println(HmacUtils.generateKeyHex(HmacType.HmacSHA224));
        System.out.println(HmacUtils.generateKeyHex(HmacType.HmacSHA256));
        System.out.println(HmacUtils.generateKeyHex(HmacType.HmacSHA384));
        System.out.println(HmacUtils.generateKeyHex(HmacType.HmacSHA512));

        System.out.println();

        System.out.println(HmacUtils.generateHmacMD5KeyHex());
        System.out.println(HmacUtils.generateHmacSHA1KeyHex());
        System.out.println(HmacUtils.generateHmacSHA224KeyHex());
        System.out.println(HmacUtils.generateHmacSHA256KeyHex());
        System.out.println(HmacUtils.generateHmacSHA384KeyHex());
        System.out.println(HmacUtils.generateHmacSHA512KeyHex());
    }

    @Test
    public void test() {
        String keyHex = HmacUtils.generateHmacMD5KeyHex();
        System.out.println(HmacUtils.hmacMD5(content, keyHex));
        System.out.println(HmacUtils.hmacSHA1(content, HmacUtils.generateHmacSHA1KeyHex()));
        System.out.println(HmacUtils.hmacSHA224(content, HmacUtils.generateHmacSHA224KeyHex()));
        System.out.println(HmacUtils.hmacSHA256(content, HmacUtils.generateHmacSHA256KeyHex()));
        System.out.println(HmacUtils.hmacSHA384(content, HmacUtils.generateHmacSHA384KeyHex()));
        System.out.println(HmacUtils.hmacSHA512(content, HmacUtils.generateHmacSHA512KeyHex()));
    }

}
