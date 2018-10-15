package com.dxy.util.common;

import com.google.common.collect.Lists;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 字符串工具类
 * @author duanxinyuan
 * 2015-01-16 20:43
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 检测邮箱地址是否合法
     */
    public static boolean isEmail(String email) {
        return PatternUtils.matches(PatternUtils.PatternEnum.EMAIL, email);
    }


    /**
     * 检测手机号是否合法
     */
    public static boolean isMobile(String mobile) {
        return PatternUtils.matches(PatternUtils.PatternEnum.MOBILE, mobile);
    }

    /**
     * 检测是否是身份证号
     */
    public static boolean isIdNumber(String idNumber) {
        return IdNumberUtils.strongVerifyIdNumber(idNumber);
    }

    /**
     * 获取字符串长度（一个中文表示两个字符）
     */
    public static int getWordCount(String s) {
        if (StringUtils.isEmpty(s)) {
            return 0;
        }
        s = s.replaceAll("[^\\x00-\\xff]", "**");
        return s.length();
    }

    /**
     * 图片Url是否是网络地址
     */
    public static boolean isHttpUrl(String url) {
        return !StringUtils.isEmpty(url) && (url.startsWith("http://") || url.startsWith("https://"));
    }

    /**
     * 判断字符串是否存在中文
     * @return true/false
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    /**
     * 判断字符串中是否包含中文
     * @return boolean
     */
    public static boolean isContainChinese(String str) {
        return PatternUtils.matches(PatternUtils.PatternEnum.CHINESE, str);
    }

    /**
     * 判断一个字符串是否都为数字
     */
    public static boolean isDigit(String str) {
        return PatternUtils.matches(PatternUtils.PatternEnum.DIGIT, str);
    }

    /**
     * 判断一个字符串是否含有数字
     */
    public static boolean hasDigit(String str) {
        return PatternUtils.matches(PatternUtils.PatternEnum.DIGIT_CONTAINS, str);
    }

    /**
     * 产生一个随机的字符串
     */
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(62);
            buf.append(str.charAt(num));
        }
        return buf.toString();
    }

    /**
     * 将字符串转成16进制字符串
     */
    public static String toHex(String s) {
        return toHex(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 将字节码转成16进制字符串
     */
    public static String toHex(byte[] bytes) {
        // 把密文转换成十六进制的字符串形式
        StringBuilder hexString = new StringBuilder();
        // 字节数组转换为 十六进制 数
        for (byte aMd : bytes) {
            String shaHex = Integer.toHexString(aMd & 0xFF);
            if (shaHex.length() < 2) {
                hexString.append(0);
            }
            hexString.append(shaHex);
        }
        return hexString.toString();
    }

    /**
     * 将16进制转换为二进制
     */
    public static String parseHexToString(String hexStr) {
        byte[] bytes = parseHexToBytes(hexStr);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 将16进制转换为二进制
     */
    public static byte[] parseHexToBytes(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * 以逗号分隔，拼接List中每个对象的某个字段
     * @param list 数组
     * @param fieldName 字段名
     * @param <T> List数据的范型
     */
    public static <T> String join(List<T> list, String fieldName, String separator) {
        if (!ListUtils.isNotEmpty(list)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        List<Object> objects = new ArrayList<>();
        for (T t : list) {
            Object fieldValue = ReflectUtils.getFieldValue(t, fieldName);
            if (null != fieldValue) {
                objects.add(fieldValue);
            }
        }
        for (int i = 0; i < objects.size(); i++) {
            Object o = objects.get(i);
            if (null == o) {
                continue;
            }
            stringBuilder.append(o);
            if (i != objects.size() - 1) {
                stringBuilder.append(separator);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 以逗号分隔，拼接List中每个id
     * @param list id集合
     */
    public static <T> String join(List<T> list, String separator) {
        if (!ListUtils.isNotEmpty(list)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            if (null == t) {
                continue;
            }
            stringBuilder.append(t);
            if (i != list.size() - 1) {
                stringBuilder.append(separator);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 以逗号分隔，拼接数组中每个字符串，作为查询条件，需要拼接单引号
     * @param strings 字符串数组
     */
    public static String joinSql(String[] strings) {
        return joinSql(Lists.newArrayList(strings));
    }

    /**
     * 以逗号分隔，拼接数组中每个字符串，作为查询条件，需要拼接单引号
     * @param strings 字符串数组
     */
    public static String joinSql(List<String> strings) {
        if (ListUtils.isEmpty(strings)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            String s = strings.get(i);
            if (isEmpty(s)) {
                continue;
            }
            stringBuilder.append("'").append(s).append("'");
            if (i != strings.size() - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

}
