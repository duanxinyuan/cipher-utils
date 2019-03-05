import com.dxy.library.util.common.DateUtils;
import org.junit.Test;

import java.util.Date;

/**
 * @author duanxinyuan
 * 2018/11/15 15:15
 */
public class DateUtilsTest {

    @Test
    public void test() {
        System.out.println(DateUtils.format(new Date(), DateUtils.yyyy_MM_dd_HHmmss));
        System.out.println(DateUtils.format(new Date(), DateUtils.yyyy_MM_dd_HHmm));
        System.out.println(DateUtils.format(new Date(), DateUtils.yyyyMMdd));
        System.out.println(DateUtils.format(new Date(), DateUtils.yyyyMMdd_VALUE));
        System.out.println(DateUtils.format(new Date(), DateUtils.MM_dd));
        System.out.println(DateUtils.format(new Date(), DateUtils.MM_dd_HHmm));
        System.out.println(DateUtils.format(new Date(), DateUtils.HHmm));
        System.out.println(DateUtils.format(new Date(), DateUtils.HHmmss));

        System.out.println(DateUtils.parse("2018-11-15 15:16:24", DateUtils.yyyy_MM_dd_HHmmss_VALUE));
        System.out.println(DateUtils.parse("2018-11-15 15:16", DateUtils.yyyy_MM_dd_HHmm_VALUE));
        System.out.println(DateUtils.parse("20181115", DateUtils.yyyyMMdd_VALUE));
        System.out.println(DateUtils.parse("11-15", DateUtils.MM_dd_VALUE));
        System.out.println(DateUtils.parse("15:16", DateUtils.HHmm_VALUE));
        System.out.println(DateUtils.parse("15:16:24", DateUtils.HHmmss_VALUE));
    }

}
