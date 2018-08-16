import com.dxy.common.util.email.EmailUtil;
import org.junit.Test;

/**
 * @author duanxinyuan
 * 2018/8/16 下午1:17
 */
public class EmailTest {

    @Test
    public void send() {
        boolean send = EmailUtil.send("test.com", "25", "test@test.com", "testto@test.com", "password", "title", "content");
        System.out.println(send);
    }
}
