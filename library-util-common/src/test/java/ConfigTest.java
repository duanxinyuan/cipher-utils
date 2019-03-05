import com.dxy.library.json.gson.GsonUtil;
import com.dxy.library.util.common.config.ConfigUtils;
import org.junit.Test;

/**
 * @author duanxinyuan
 * 2018/8/6 12:54
 */
public class ConfigTest {

    @Test
    public void test() {
        System.out.println(ConfigUtils.getConfig("test.name"));
        System.out.println(ConfigUtils.getConfig("test.age", Long.class));
        System.out.println(ConfigUtils.getConfig("test.info.game", GameInfo.class));

        System.out.println(GsonUtil.to(ConfigUtils.getConfigs("test.info.game")));
    }

}
