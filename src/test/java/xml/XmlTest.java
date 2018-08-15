package xml;

import com.dxy.common.util.XmlUtil;
import com.dxy.library.json.JacksonUtil;
import org.junit.Test;

/**
 * @author duanxinyuan
 * 2018/8/14 下午9:20
 */
public class XmlTest {

    @Test
    public void test() {
        String s = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
                "<return>\n" +
                "    <status>Success</status>\n" +
                "    <amount>2.22</amount>\n" +
                "    <success>true</success>\n" +
                "    <count>2</count>\n" +

                "    <content>\n" +
                "    <name>ase</name>\n" +
                "    <age>asdha</age>\n" +
                "    </content>\n" +

                "    <list>\n" +
                "    <name>123</name>\n" +
                "    <name>231</name>\n" +
                "    </list>\n" +

                "    <contentList>\n" +
                "    <content>\n" +
                "    <name>ase</name>\n" +
                "    <age>123</age>\n" +
                "    </content>\n" +
                "    </contentList>\n" +

                "    <map>\n" +
                "    <key1>value1</key1>\n" +
                "    <key2>value2</key2>\n" +
                "    </map>\n" +

                "    <contentMap>\n" +
                "    <key1>" +
                "    <content>\n" +
                "    <name>ase</name>\n" +
                "    <age>asdha</age>\n" +
                "    </content>\n" +
                "    </key1>\n" +
                "    <key2>" +
                "    <content>\n" +
                "    <name>ase</name>\n" +
                "    <age>asdha</age>\n" +
                "    </content>\n" +
                "    </key2>\n" +
                "    </contentMap>\n" +

                "    </return>";
        TestPojo testPojo = XmlUtil.from(s, TestPojo.class);
        System.out.println(JacksonUtil.to(testPojo));
    }

}
