import com.dxy.library.util.cipher.hash.SHAUtils;
import org.junit.Test;

/**
 * @author duanxinyuan
 * 2019/2/26 22:54
 */
public class SHATest {
    private String content = "hello world";

    @Test
    public void test() {
        System.out.println(SHAUtils.sha(content));
        System.out.println(SHAUtils.sha1(content));
        System.out.println(SHAUtils.sha224(content));
        System.out.println(SHAUtils.sha256(content));
        System.out.println(SHAUtils.sha384(content));
        System.out.println(SHAUtils.sha512(content));
        System.out.println(SHAUtils.sha3_224(content));
        System.out.println(SHAUtils.sha3_256(content));
        System.out.println(SHAUtils.sha3_384(content));
        System.out.println(SHAUtils.sha3_512(content));
    }

}
