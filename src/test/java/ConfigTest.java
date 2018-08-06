import com.dxy.common.util.ConfigUtil;
import org.junit.Test;

/**
 * @author duanxinyuan
 * 2018/8/6 12:54
 */
public class ConfigTest {

    @Test
    public void test() {
        System.out.println(ConfigUtil.getConfig("dxy.name"));
        System.out.println(ConfigUtil.getConfig("dxy.age", Long.class));
        System.out.println(ConfigUtil.getConfig("dxy.info.game", GameInfo.class));
    }

}
