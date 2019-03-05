package com.dxy.library.util.cipher.symmetry;

/**
 * RC4工具类
 * RC4：在1987年被RSA三人组中的头号人物罗纳德所创建，密钥长40-1024，块长64，用于保护商业机密和互联网中
 * 优点：算法简单，运行速度快，安全性高，算法的速度可以达到DES加密的10倍左右，且具有很高级别的非线性，
 * 密钥长度是可变的，可变范围为1-256字节(8-2048比特)，但一般为256字节
 * @author duanxinyuan
 * 2019/2/15 19:19
 */
public class RC4Utils {

    //S盒长度
    private static final int box_length = 256;

    /**
     * 加密和解密
     * @param content 内容
     * @param key 密钥
     */
    public static String encryptOrDecrypt(String content, String key) {
        Integer[] S = new Integer[box_length]; // S盒
        Character[] characters = new Character[content.length()]; // 生成的密钥流
        StringBuilder stringBuilder = new StringBuilder();

        ksa(S, key);
        rpga(S, characters, content.length());

        for (int i = 0; i < content.length(); ++i) {
            stringBuilder.append((char) (content.charAt(i) ^ characters[i]));
        }
        return stringBuilder.toString();
    }

    /**
     * 密钥调度算法--利用key来对S盒做一个置换，也就是对S盒重新排列
     */
    private static void ksa(Integer[] s, String key) {
        for (int i = 0; i < box_length; ++i) {
            s[i] = i;
        }

        int j = 0;
        for (int i = 0; i < box_length; ++i) {
            j = (j + s[i] + key.charAt(i % key.length())) % box_length;
            swap(s, i, j);
        }
    }

    /**
     * 伪随机生成算法--利用上面重新排列的S盒来产生任意长度的密钥流
     */
    private static void rpga(Integer[] s, Character[] characters, int length) {
        int i = 0, j = 0;
        for (int k = 0; k < length; ++k) {
            i = (i + 1) % box_length;
            j = (j + s[i]) % box_length;
            swap(s, i, j);
            characters[k] = (char) (s[(s[i] + s[j]) % box_length]).intValue();
        }
    }

    /**
     * 置换
     */
    private static void swap(Integer[] s, int i, int j) {
        Integer mTemp = s[i];
        s[i] = s[j];
        s[j] = mTemp;
    }

}
