import com.dxy.library.util.common.FileUtils;
import org.junit.Test;

import java.io.File;

/**
 * @author duanxinyuan
 * 2018/10/29 14:30
 */
public class FileUtilsTest {

    @Test
    public void testDelete() {
        FileUtils.delete("/data/logs/test2");
    }

    @Test
    public void testZip() {
        // 存放待压缩文件的目录
        File file = new File("/data/logs/test");
        // 压缩后的zip文件路径
        String zipFilePath = "/data/logs/test/test.zip";
        FileUtils.zip(file, zipFilePath);
        // 调用解压方法
        FileUtils.unzip(zipFilePath, "/data/logs/test1");
    }

    @Test
    public void testTar() {
        // 存放待压缩文件的目录
        File file = new File("/data/logs/test");
        // 压缩后的zip文件路径
        String zipFilePath = "/data/logs/test/test.tar.gz";
        FileUtils.tar(file, zipFilePath);
        // 调用解压方法
        FileUtils.untar(zipFilePath, "/data/logs/test1");
    }

}
