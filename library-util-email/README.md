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
