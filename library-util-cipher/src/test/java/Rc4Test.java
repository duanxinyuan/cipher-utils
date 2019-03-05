import com.dxy.library.util.cipher.symmetry.RC4Utils;
import org.junit.Test;

/**
 * @author duanxinyuan
 * 2019/2/20 21:27
 */
public class Rc4Test {
    private String content = "hello world";
    private String key = "58e5d06e89260420";

    @Test
    public void test() {
        String ciphertext = RC4Utils.encryptOrDecrypt(content, key);
        System.out.println(ciphertext);
        String decryptText = RC4Utils.encryptOrDecrypt(ciphertext, key);
        System.out.println(decryptText);
    }

}
