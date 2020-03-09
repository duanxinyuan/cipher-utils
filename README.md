# 加密解密工具类

## 包含加密算法
* Base32
* Base64
* BinaryCodec
* Hex
* Hmac
* MD5
* SHA
* RSA
* AES
* Blowfish
* DES
* DESede
* RC4
* SM2
* SM3
* SM4

## Maven依赖：
```xml
<dependency>
    <groupId>com.github.duanxinyuan</groupId>
    <artifactId>util-cipher</artifactId>
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

### 依赖的其他工具类
Base32
Base64
BinaryCodec
Hex