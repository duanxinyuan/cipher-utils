import com.dxy.library.util.cipher.hash.SM3Utils;
import org.junit.Test;

/**
 * @author duanxinyuan
 * 2019/2/25 15:22
 */
public class SM3Test {
    private String content = "hello world";

    @Test
    public void test() {
        System.out.println(SM3Utils.sm3(content));
    }

}
