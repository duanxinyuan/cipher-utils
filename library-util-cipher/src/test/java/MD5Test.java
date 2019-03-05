import com.dxy.library.util.cipher.hash.MD5Utils;
import org.junit.Test;

/**
 * @author duanxinyuan
 * 2019/2/26 23:14
 */
public class MD5Test {
    private String content = "hello world";
    private String salt = "123456";

    @Test
    public void test() {
        System.out.println(MD5Utils.md5(content));
        System.out.println(MD5Utils.md5(content, salt));
        System.out.println(MD5Utils.md5(content, salt, 1024));
    }

}
