package com.dxy.library.util.common;

import com.google.gson.reflect.TypeToken;
import com.dxy.library.json.gson.GsonUtil;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 时间工具类
 * @author duanxinyuan
 * 2017/9/6 17:55
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static final String yyyy_MM_dd_HHmmss_VALUE = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter yyyy_MM_dd_HHmmss = DateTimeFormatter.ofPattern(yyyy_MM_dd_HHmmss_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String yyyy_MM_dd_HHmm_VALUE = "yyyy-MM-dd HH:mm";
    public static final DateTimeFormatter yyyy_MM_dd_HHmm = DateTimeFormatter.ofPattern(yyyy_MM_dd_HHmm_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String yyyy_MM_dd_VALUE = "yyyy-MM-dd";
    public static final DateTimeFormatter yyyy_MM_dd = DateTimeFormatter.ofPattern(yyyy_MM_dd_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String yyyyMMdd_VALUE = "yyyyMMdd";
    public static final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern(yyyyMMdd_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String MM_dd_VALUE = "MM-dd";
    public static final DateTimeFormatter MM_dd = DateTimeFormatter.ofPattern(MM_dd_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String MM_dd_HHmm_VALUE = "MM-dd HH:mm";
    public static final DateTimeFormatter MM_dd_HHmm = DateTimeFormatter.ofPattern(MM_dd_HHmm_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String HHmm_VALUE = "HH:mm";
    public static final DateTimeFormatter HHmm = DateTimeFormatter.ofPattern(HHmm_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String HHmmss_VALUE = "HH:mm:ss";
    public static final DateTimeFormatter HHmmss = DateTimeFormatter.ofPattern(HHmmss_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static String format(LocalDateTime localDateTime, String pattern) {
        if (null == localDateTime) {
            throw new IllegalArgumentException("datetime is null");
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(Date date, String pattern) {
        if (null == date) {
            throw new IllegalArgumentException("date is null");
        }
        return DateFormatUtils.format(date, pattern);
    }

    public static String format(Date date, DateTimeFormatter dateTimeFormatter) {
        if (null == date) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(dateTimeFormatter);
    }

    public static String format(Long timestamp, String pattern) {
        if (null == timestamp) {
            throw new IllegalArgumentException("timestamp is null");
        }
        return format(timestamp, DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(Long timestamp, DateTimeFormatter dateTimeFormatter) {
        if (null == timestamp) {
            throw new IllegalArgumentException("timestamp is null");
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).format(dateTimeFormatter);
    }

    public static Date parse(String str, String pattern) {
        if (StringUtils.isEmpty(str)) {
            throw new IllegalArgumentException("date is null");
        }
        try {
            return parseDate(str, Locale.getDefault(), pattern);
        } catch (ParseException e) {
            throw new IllegalArgumentException();
        }
    }

    public static Date from(Long timestamp) {
        if (null == timestamp) {
            throw new IllegalArgumentException("timestamp is null");
        }
        return Date.from(Instant.ofEpochMilli(timestamp));
    }

    /**
     * 计算两个时间相差的小时数目(精确到小数位)
     * @param beginDateStr 开始时间
     * @param endDateStr 结束时间
     * @return 小时数
     */
    public static double hourDifference(String beginDateStr, String endDateStr, String pattern) {
        Date beginDate = parse(beginDateStr, pattern);
        Date endDate = parse(endDateStr, pattern);
        return hourDifference(beginDate, endDate);
    }

    /**
     * 计算两个时间相差的小时数目(精确到小数位)
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @return 小时数
     */
    public static double hourDifference(Date beginDate, Date endDate) {
        if (null == beginDate || null == endDate) {
            throw new IllegalArgumentException("date is null");
        }
        long nh = 1000 * 60 * 60;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - beginDate.getTime();
        // 计算差多少小时
        return (double) diff / (double) nh;
    }

    /**
     * 计算两个时间相差的天数目(向下取整)
     * @param beginDateStr 开始时间
     * @param endDateStr 结束时间
     * @return 天数
     */
    public static int dayDifference(String beginDateStr, String endDateStr, String pattern) {
        Date beginDate = parse(beginDateStr, pattern);
        Date endDate = parse(endDateStr, pattern);
        return dayDifference(beginDate, endDate);
    }

    /**
     * 计算两个时间相差的天数目(向下取整)
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @return 天数
     */
    public static int dayDifference(Date beginDate, Date endDate) {
        long nh = 1000 * 60 * 60 * 24;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - beginDate.getTime();
        // 计算差多少小时
        return (int) (diff / nh);
    }

    /**
     * 获取离当前时间的时差
     * @param date 开始时间
     */
    public static Duration durationToNow(Date date) {
        return Duration.between(ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()), ZonedDateTime.now());
    }

    /**
     * 获取时间差
     * @param beginDate 开始时间
     * @param endDate 结束时间
     */
    public static Duration duration(Date beginDate, Date endDate) {
        GregorianCalendar beginCalendar = new GregorianCalendar();
        beginCalendar.setTime(beginDate);
        if (null == endDate) {
            throw new IllegalArgumentException("enddate is null");
        }
        GregorianCalendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);
        return Duration.between(beginCalendar.toZonedDateTime(), endCalendar.toZonedDateTime());
    }

    /**
     * 给时间添加小时数
     */
    public static String addHours(String dateStr, double hours, String pattern) {
        Date parse = parse(dateStr, pattern);
        Calendar ca = Calendar.getInstance();
        ca.setTime(parse);
        ca.add(Calendar.SECOND, (int) (hours * 3600));
        return format(ca.getTime(), pattern);
    }

    /**
     * 给时间添加小时数
     */
    public static Date addHours(Date date, double hours) {
        if (null == date) {
            throw new IllegalArgumentException("date is null");
        }
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.SECOND, (int) (hours * 3600));
        return ca.getTime();
    }

    /**
     * 给时间添加天数
     */
    public static String addDays(String dateStr, int days, String pattern) {
        if (StringUtils.isEmpty(dateStr)) {
            throw new IllegalArgumentException("date is null");
        }
        Date parse = parse(dateStr, pattern);
        Calendar ca = Calendar.getInstance();
        ca.setTime(parse);
        ca.add(Calendar.DAY_OF_MONTH, days);
        return format(ca.getTime(), pattern);
    }

    /**
     * 给时间添加天数
     */
    public static Date addDays(Date date, int days) {
        if (null == date) {
            throw new IllegalArgumentException("date is null");
        }
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.DAY_OF_MONTH, days);
        return ca.getTime();
    }

    /**
     * 查询是否是节假日
     */
    public static boolean isHoliday(Date date) {
        List<Date> holidays = getHoliday();
        Calendar dateCalendar = Calendar.getInstance();
        Calendar holidayCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        for (Date holiday : holidays) {
            holidayCalendar.setTime(holiday);
            if (dateCalendar.get(Calendar.YEAR) == holidayCalendar.get(Calendar.YEAR)
                    && dateCalendar.get(Calendar.MONTH) == holidayCalendar.get(Calendar.MONTH)
                    && dateCalendar.get(Calendar.DAY_OF_MONTH) == holidayCalendar.get(Calendar.DAY_OF_MONTH)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取工作日列表
     */
    public static List<Date> getHoliday() {
        String holidayStr = "[2018-01-01,2018-02-15,2018-02-16,2018-02-17,2018-02-18,2018-02-19,2018-02-20,2018-02-21,2018-04-05,2018-04-06,2018-04-07,2018-04-29,2018-04-30,2018-05-01,2018-06-18,2018-09-24,2018-10-01 ,2018-10-02 ,2018-10-03 ,2018-10-04 ,2018-10-05 ,2018-10-06 ,2018-10-07," +
                "2019-01-01,2019-02-04,2019-02-05,2019-02-06,2019-02-07,2019-02-08,2019-02-09,2019-02-10,2019-04-05,2019-04-30,2019-05-01,2019-06-07,2019-09-13,2019-10-01 ,2019-10-02 ,2019-10-03 ,2019-10-04 ,2019-10-05 ,2019-10-06 ,2019-10-07]";
        return GsonUtil.from(holidayStr, new TypeToken<ArrayList<Date>>() {});
    }

    /**
     * 根据开始日期 ，需要的工作日天数 ，计算工作截止日期，并返回截止日期，不计算当天
     * @param date 开始日期
     * @param workDay 工作日天数(周一到周五)
     * @return Date类型
     */
    public static Date getWorkDay(Date date, int workDay) {
        List<Date> holidays = getHoliday();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        for (int i = 0; i < workDay; i++) {
            //判断日期是否是法定假日
            if (null != holidays) {
                boolean isHolidays = false;
                for (Date holiday : holidays) {
                    if (isSameDay(holiday, calendar.getTime())) {
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
                        workDay += 1;
                        isHolidays = true;
                        break;
                    }
                }
                if (isHolidays) {
                    continue;
                }
            }

            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            // 判断当天是否为周末
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (Calendar.SATURDAY == dayOfWeek || Calendar.SUNDAY == dayOfWeek) {
                workDay += 1;
            }
        }
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        while (Calendar.SATURDAY == dayOfWeek || Calendar.SUNDAY == dayOfWeek) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        }
        return calendar.getTime();
    }

    /**
     * 比较两个时间是否为同一天
     */
    public static boolean isSameDay(String dateStr1, String dateStr2) {
        return isSameDay(parse(dateStr1, yyyyMMdd_VALUE), parse(dateStr2, yyyyMMdd_VALUE));
    }

    /**
     * 判断是否是当月最后一天
     * @param date 日期
     */
    public static boolean isLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.get(Calendar.DAY_OF_MONTH) == 1;
    }

    /**
     * 根据星期获取描述
     */
    public static String getWeekDesc(String weeks) {
        String[] week = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        StringBuilder weekCnStr = new StringBuilder();
        if (!StringUtils.isEmpty(weeks)) {
            String[] weekArr = weeks.split(",");
            int count = 0;
            int prevWeek = 0;
            for (int i = 0; i < weekArr.length; i++) {
                int w = Integer.parseInt(weekArr[i]);
                if (i == 0) {
                    weekCnStr = new StringBuilder(week[w]);
                    count = 0;
                } else if (w - prevWeek > 1) {
                    if (count > 1) {
                        weekCnStr.append("~").append(week[prevWeek]).append("、").append(week[w]);
                    } else if (count == 1) {
                        weekCnStr.append("、").append(week[prevWeek]).append("、").append(week[w]);
                    } else {
                        weekCnStr.append("、").append(week[w]);
                    }
                    count = 0;
                } else if (w - prevWeek <= 1) {
                    count++;
                    if (i == weekArr.length - 1) {
                        if (count > 1) {
                            weekCnStr.append("~").append(week[w]);
                        } else {
                            weekCnStr.append("、").append(week[w]);
                        }
                    }
                }
                prevWeek = w;
            }
        }
        return weekCnStr.toString();
    }

}
