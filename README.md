# 基础工具类

## Maven依赖：
```xml
<dependency>
    <groupId>com.github.duanxinyuan</groupId>
    <artifactId>library-util-common</artifactId>
    <version>1.2.0</version>
</dependency>
```

* Class操作工具类：ClassUtils
* 时间工具类：DateUtils
* 线程池工具类：ExecutorUtils
* 文件工具类：FileUtils
* 身份证号码工具类：IDNumberUtils
* IP工具类：IpUtils
* List工具类：ListUtils
* 数字工具类：MathUtils
* 反射工具类：ReflectUtils
* 字符串工具类：StringUtils
* 配置文件工具类：ConfigUtils
* 正则表达式工具类：PatternUtils
* 系统工具类：SystemUtils

* Twitter雪花算法：SnowflakeId


# 加密解密工具类

## 包含加密算法
Hmac、MD5、SHA、RSA、AES、Blowfish、DES、DESede、RC4、SM2、SM3、SM4

## Maven依赖：
```xml
<dependency>
    <groupId>com.github.duanxinyuan</groupId>
    <artifactId>library-util-cipher</artifactId>
    <version>1.1.0</version>
</dependency>
```

### 非对称加密
* RSAUtils（国际非对称加密标准）
* SM2Utils（椭圆曲线公钥密码算法，国内非对称加密标准）

### 对称加密
* AESUtils（高级加密标准(Advanced Encryption Standard)）
* BlowfishUtils
* DESUtils（美国数据加密标准(Data Encryption Standard)）
* DESedeUtils（三重DES）
* RC4Utils
* SM4Utils（无线局域网标准的分组数据算法，国密对称加密）

### 散列/摘要/杂凑
* HmacUtils
* MD5Utils
* SHAUtils
* SM3Utils（国密杂凑算法）


# 邮件工具类


## Maven依赖：
```xml
<dependency>
    <groupId>com.github.duanxinyuan</groupId>
    <artifactId>library-util-email</artifactId>
    <version>1.1.0</version>
</dependency>
```

* 邮件工具类：EmailUtils

## 使用示例
```java
public class Test{
    
    public void test(){
        Email email = Email.builder()
                .host("test.com")
                .port(25)
                .from("test@test.com")
                .to("testto@test.com")
                .password("password")
                .title("title")
                .content("content")
                .isHtml(false).build();
        //同步发送
        boolean send = EmailUtils.send(email);
        System.out.println(send);
        //异步发送
        EmailUtils.sendAsync(email, ((receiver, isSuccess) -> {
            System.out.println(receiver);
            System.out.println(isSuccess);
        }));
    }
    
}
```


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
