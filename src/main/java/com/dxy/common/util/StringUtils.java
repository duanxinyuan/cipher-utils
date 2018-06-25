package com.dxy.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * @author duanxinyuan
 * 2015-01-16 20:43
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    //匹配数字的正则表达式
    private static Pattern digit_pattern = Pattern.compile(".*\\d+.*");

    private static Pattern email_pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private static Pattern mobile_pattern = Pattern.compile("^[1][0-9]{10}$");

    /**
     * 检测邮箱地址是否合法
     * @return true合法 false不合法
     */
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) {
            return false;
        }
        Matcher m = email_pattern.matcher(email);
        return m.matches();
    }


    public static boolean isMobile(String mobile) {
        Matcher m = mobile_pattern.matcher(mobile);
        return m.find();
    }


    public static boolean isIdNumber(String idNumber) {
        return IdNumberUtil.strongVerifyIdNumber(idNumber);
    }

    /**
     * 获取字符串长度（一个中文表示两个字符）
     */
    public static int getWordCount(String s) {
        if (org.apache.commons.lang.StringUtils.isEmpty(s)) {
            return 0;
        }
        s = s.replaceAll("[^\\x00-\\xff]", "**");
        return s.length();
    }

    /**
     * 图片Url是否是网络地址
     */
    public static boolean isHttpUrl(String url) {
        return !org.apache.commons.lang.StringUtils.isEmpty(url) && (url.startsWith("http://") || url.startsWith("https://"));
    }


    // 判断一个字符串是否都为数字
    public static boolean isDigit(String strNum) {
        return strNum.matches("[0-9]{1,}");
    }

    // 判断一个字符串是否含有数字
    public static boolean hasDigit(String content) {
        boolean flag = false;
        Matcher m = digit_pattern.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        return flag;
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
     * 将字节码转成16进制字符串
     */
    public static String byteToHexString(byte[] bytes) {
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
    public static byte[] parseHexStr2Byte(String hexStr) {
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
    public static <T> String jointFieldForListByComma(List<T> list, String fieldName) {
        if (!ListUtil.isNN(list)) {
            return "";
        }
        StringBuilder jointFieldForListByComma = new StringBuilder();
        List<Object> objects = new ArrayList<>();
        for (T t : list) {
            Object fieldValue = ReflectUtil.getFieldValue(t, fieldName);
            if (null != fieldValue) {
                objects.add(fieldValue);
            }
        }
        for (int i = 0; i < objects.size(); i++) {
            Object o = objects.get(i);
            if (null == o) {
                continue;
            }
            jointFieldForListByComma.append(o);
            if (i != objects.size() - 1) {
                jointFieldForListByComma.append(",");
            }
        }
        return jointFieldForListByComma.toString();
    }

    /**
     * 以逗号分隔，拼接List中每个id
     * @param list id集合
     */
    public static String jointIdsByComma(List<Long> list) {
        if (!ListUtil.isNN(list)) {
            return "";
        }
        StringBuilder jointFieldForListByComma = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Long l = list.get(i);
            if (null == l) {
                continue;
            }
            jointFieldForListByComma.append(l);
            if (i != list.size() - 1) {
                jointFieldForListByComma.append(",");
            }
        }
        return jointFieldForListByComma.toString();
    }

    /**
     * 以逗号分隔，拼接数组中每个字符串
     * @param strings 字符串数组
     */
    public static String jointStringsByComma(String[] strings) {
        if (strings == null || strings.length == 0) {
            return "";
        }
        StringBuilder jointFieldForListByComma = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            if (isEmpty(s)) {
                continue;
            }
            jointFieldForListByComma.append(s);
            if (i != strings.length - 1) {
                jointFieldForListByComma.append(",");
            }
        }
        return jointFieldForListByComma.toString();
    }

    /**
     * 以逗号分隔，拼接数组中每个字符串
     * @param strings 字符串数组
     */
    public static String jointStringsByComma(List<String> strings) {
        if (strings == null || strings.size() == 0) {
            return "";
        }
        StringBuilder jointFieldForListByComma = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            String s = strings.get(i);
            if (isEmpty(s)) {
                continue;
            }
            jointFieldForListByComma.append(s);
            if (i != strings.size() - 1) {
                jointFieldForListByComma.append(",");
            }
        }
        return jointFieldForListByComma.toString();
    }

    /**
     * 以逗号分隔，拼接数组中每个字符串，作为查询条件，需要拼接单引号
     * @param strings 字符串数组
     */
    public static String jointStringsByCommaForSql(String[] strings) {
        if (strings == null || strings.length == 0) {
            return "";
        }
        StringBuilder jointFieldForListByComma = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            if (isEmpty(s)) {
                continue;
            }
            jointFieldForListByComma.append("'").append(s).append("'");
            if (i != strings.length - 1) {
                jointFieldForListByComma.append(",");
            }
        }
        return jointFieldForListByComma.toString();
    }

    /**
     * 以逗号分隔，拼接数组中每个字符串，作为查询条件，需要拼接单引号
     * @param strings 字符串数组
     */
    public static String jointStringsByCommaForSql(List<String> strings) {
        if (strings == null || strings.size() == 0) {
            return "";
        }
        StringBuilder jointFieldForListByComma = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            String s = strings.get(i);
            if (isEmpty(s)) {
                continue;
            }
            jointFieldForListByComma.append("'").append(s).append("'");
            if (i != strings.size() - 1) {
                jointFieldForListByComma.append(",");
            }
        }
        return jointFieldForListByComma.toString();
    }

}
