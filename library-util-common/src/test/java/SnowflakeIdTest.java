import com.dxy.library.util.common.snowflake.SnowflakeId;
import org.junit.Test;

/**
 * @author duanxinyuan
 * 2019/1/14 21:54
 */
public class SnowflakeIdTest {

    @Test
    public void testSnowflakeId() {
        for (int i = 0; i < 100000; i++) {
            System.out.println(SnowflakeId.generate());
        }
    }

}
