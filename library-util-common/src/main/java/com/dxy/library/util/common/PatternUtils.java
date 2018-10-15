package com.dxy.library.util.common;/*
 * Copyright 2015-2020 uuzu.com All right reserved.
 */

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zxc Jun 26, 2017 7:48:50 PM
 */
@Slf4j
public class PatternUtils {
    //邮箱地址
    private static Pattern COMPILE_EMAIL = Pattern.compile("\\w+@\\w+\\.(com|cn)");
    //日期
    private static Pattern COMPILE_DATE = Pattern.compile("20\\d{2}-((0[1-9])|(1[0-2]))-((0[1-9])|([1-2][0-9])|(3[0-1]))");
    //IP
    private static Pattern COMPILE_IP = Pattern.compile("((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))");
    //手机号
    private static Pattern COMPILE_MOBILE = Pattern.compile("^[1][0-9]{10}$");
    //数字
    private static Pattern COMPILE_DIGIT = Pattern.compile("[0-9]{1,}");
    //包含数字的字符串
    private static Pattern COMPILE_DIGIT_CONTAINS = Pattern.compile(".*\\d+.*");
    //中文
    private static Pattern COMPILE_CHINESE = Pattern.compile("[\u4e00-\u9fa5]");
    //URI
    private static Pattern COMPILE_URI = Pattern.compile("(/[A-Za-z0-9_]+(/[A-Za-z0-9_]+)+)");
    //URL
    private static Pattern COMPILE_URL = Pattern.compile("(http://|https://){1}[\\w\\.\\-/:\\?&%=,;\\[\\]\\{\\}`~!@#\\$\\^\\*\\(\\)_\\+\\\\\\|]+");
    //特殊字符串
    private static Pattern COMPILE_STRING_SPECIAL = Pattern.compile(".*[^A-Za-z0-9]$");

    // 正则表达式枚举类
    public enum PatternEnum {
        EMAIL, DATE, IP, MOBILE, DIGIT, DIGIT_CONTAINS, CHINESE;
    }

    /**
     * @param patternType 正则表达式枚举类
     * @param value 源字符串
     * @return true/false
     */
    public static boolean matches(PatternEnum patternType, String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        boolean result = false;
        switch (patternType) {
            case EMAIL:
                result = COMPILE_EMAIL.matcher(value).matches();
                break;
            case DATE:
                result = COMPILE_DATE.matcher(value).matches();
                break;
            case IP:
                result = COMPILE_IP.matcher(value).matches();
                break;
            case MOBILE:
                result = COMPILE_MOBILE.matcher(value).matches();
                break;
            case DIGIT:
                result = COMPILE_DIGIT.matcher(value).matches();
                break;
            case DIGIT_CONTAINS:
                result = COMPILE_DIGIT_CONTAINS.matcher(value).matches();
                break;
            case CHINESE:
                result = COMPILE_CHINESE.matcher(value).matches();
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 校验字符串的前几位不包含特殊字符
     * @param res 字符串
     * @param length 前几位包含特殊字符
     * @return 不包含返回true， 包含返回false
     */
    public static boolean prefixNotSpecial(String res, int length) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]{" + length + "}.*$");
        return pattern.matcher(res).matches();
    }

    /**
     * 检验字符串是否以特殊字符结尾
     * @param res 字符串
     * @return 以特殊字符结尾，返回true， 不以特殊字符结尾返回false
     */
    public static boolean suffixContainsSpecial(String res) {
        return COMPILE_STRING_SPECIAL.matcher(res).matches();
    }

    /**
     * 字母匹配，不区分大小
     * @return true/false
     */
    public static boolean letterMatchesIgnoreCase(String format, String res) {
        Pattern p = Pattern.compile(format, Pattern.CASE_INSENSITIVE);
        return p.matcher(res).matches();
    }

    /**
     * 匹配URL地址，并且返回匹配的结果
     * @return 返回url列表
     */
    public static List<String> genPatternUrl(String value) {
        List<String> urlList = new ArrayList<>();
        Matcher m = COMPILE_URL.matcher(value);
        while (m.find()) {
            urlList.add(m.group());
        }
        return urlList;
    }

    /**
     * 自定义正则表达式匹配
     * @return true/false
     */
    public static boolean customMatches(String patternFormat, String value) {
        if (StringUtils.isBlank(patternFormat) || StringUtils.isBlank(value)) {
            return false;
        }
        Pattern p = Pattern.compile(patternFormat);
        return p.matcher(value).matches();
    }

    /**
     * 过滤URI
     */
    public static String filterUri(String uri) {
        if (StringUtils.isBlank(uri)) {
            return null;
        }
        Matcher m = COMPILE_URI.matcher(uri);
        while (m.find()) {
            return m.group();
        }
        return null;
    }

}
