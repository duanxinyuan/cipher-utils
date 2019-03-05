# FTP操作工具类

## Maven依赖：
```xml
<dependency>
    <groupId>com.github.duanxinyuan</groupId>
    <artifactId>util-ftp</artifactId>
    <version>library-1.1.0</version>
</dependency>
```

* FTP操作工具类：FtpUtils

```java
public class Test{
    
    public void test(){
        //连接
        FtpUtils.connect(url);
        
        //断开连接
        FtpUtils.disconnect(url);
        
        //获取指定目录下所有文件名的列表
        FtpUtils.listFileNames();
        
        //获取指定目录下所有文件对象的列表
        FtpUtils.listFiles();
      
        //上传文件
        FtpUtils.upload();
        
        //下载文件
        FtpUtils.download();
        
        //删除文件
        FtpUtils.remove();
        
        //重命名文件或移动文件位置
        FtpUtils.rename();
        
    }
    
}
```
