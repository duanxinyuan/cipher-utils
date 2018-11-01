package com.dxy.library.util.common;

import com.google.gson.reflect.TypeToken;
import com.dxy.library.json.gson.GsonUtil;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 时间工具类
 * @author duanxinyuan
 * 2017/9/6 17:55
 */
public interface DateUtils {
    String yyyy_MM_dd_HHmmss_VALUE = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter yyyy_MM_dd_HHmmss = DateTimeFormatter.ofPattern(yyyy_MM_dd_HHmmss_VALUE);

    String yyyy_MM_dd_HHmm_VALUE = "yyyy-MM-dd HH:mm";
    DateTimeFormatter yyyy_MM_dd_HHmm = DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmm_VALUE");

    String yyyyMMdd_VALUE = "yyyyMMdd";
    DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern(yyyyMMdd_VALUE);

    String MM_dd_VALUE = "MM-dd";
    DateTimeFormatter MM_dd = DateTimeFormatter.ofPattern(MM_dd_VALUE);

    String MM_dd_HHmm_VALUE = "MM-dd HH:mm";
    DateTimeFormatter MM_dd_HHmm = DateTimeFormatter.ofPattern("MM-dd HH:mm");

    String HHmm_VALUE = "HH:mm";
    DateTimeFormatter HHmm = DateTimeFormatter.ofPattern("HH:mm");

    String HHmmss_VALUE = "HH:mm:ss";
    DateTimeFormatter HHmmss = DateTimeFormatter.ofPattern("HH:mm:ss");

    static String format(LocalDateTime localDateTime, String pattern) {
        if (null == localDateTime) {
            return null;
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    static String format(Date date, String pattern) {
        if (null == date) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(pattern));
    }

    static String format(Date date, DateTimeFormatter dateTimeFormatter) {
        if (null == date) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(dateTimeFormatter);
    }

    static String format(Long timestamp, String pattern) {
        if (null == timestamp) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(pattern));
    }

    static String format(Long timestamp, DateTimeFormatter dateTimeFormatter) {
        if (null == timestamp) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).format(dateTimeFormatter);
    }

    static Date parse(String str, String pattern) {
        if (StringUtils.isEmail(str)) {
            return null;
        }
        return Date.from(ZonedDateTime.parse(str, DateTimeFormatter.ofPattern(pattern)).toInstant());
    }

    static Date parse(String str, DateTimeFormatter dateTimeFormatter) {
        if (StringUtils.isEmail(str)) {
            return null;
        }
        return Date.from(ZonedDateTime.parse(str, dateTimeFormatter).toInstant());
    }

    static Date from(Long timestamp) {
        if (null == timestamp) {
            return null;
        }
        return Date.from(Instant.ofEpochMilli(timestamp));
    }

    /**
     * 获取UTC时间
     */
    static OffsetDateTime getUTCTime() {
        ZoneOffset zoneOffset = ZoneOffset.of("+00:00");
        LocalDateTime localDateTime = LocalDateTime.now(zoneOffset);
        String formatter = yyyy_MM_dd_HHmmss.format(localDateTime);
        LocalDateTime localDateTime1 = LocalDateTime.parse(formatter, yyyy_MM_dd_HHmmss);
        return OffsetDateTime.of(localDateTime1, ZoneOffset.UTC);
    }

    /**
     * 计算两个时间相差的小时数目(精确到小数位)
     * @param beginDateStr 开始时间
     * @param endDateStr 结束时间
     * @return 小时数
     */
    static double getHourGap(String beginDateStr, String endDateStr, String parseFormat) {
        Date beginDate = parse(beginDateStr, parseFormat);
        Date endDate = parse(endDateStr, parseFormat);
        return getHourGap(beginDate, endDate);
    }

    /**
     * 计算两个时间相差的小时数目(精确到小数位)
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @return 小时数
     */
    static double getHourGap(Date beginDate, Date endDate) {
        if (null == beginDate || null == endDate) {
            return 0;
        }
        long nh = 1000 * 60 * 60;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - beginDate.getTime();
        // 计算差多少小时
        return (double) diff / (double) nh;
    }

    /**
     * 获取离当前时间的时差
     * @param date 开始时间
     */
    static Duration duration(Date date) {
        return duration(date, null);
    }

    /**
     * 获取时间差
     * @param beginDate 开始时间
     * @param endDate 结束时间
     */
    static Duration duration(Date beginDate, Date endDate) {
        GregorianCalendar beginCalendar = new GregorianCalendar();
        beginCalendar.setTime(beginDate);
        if (null == endDate) {
            return Duration.between(beginCalendar.toZonedDateTime(), ZonedDateTime.now());
        }
        GregorianCalendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);
        return Duration.between(beginCalendar.toZonedDateTime(), endCalendar.toZonedDateTime());
    }

    /**
     * 给时间添加小时数
     */
    static String addHours(String dateStr, double value, String parseFormat) {
        Date parse = parse(dateStr, parseFormat);
        if (null == parse) {
            return null;
        }
        Calendar ca = Calendar.getInstance();
        ca.setTime(parse);
        ca.add(Calendar.SECOND, (int) (value * 3600));//以秒为单位计算
        return format(ca.getTime(), parseFormat);
    }

    /**
     * 给时间添加小时数
     */
    static Date addHours(Date date, double value) {
        if (null == date) {
            return null;
        }
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.SECOND, (int) (value * 3600));//以秒为单位计算
        return ca.getTime();
    }

    /**
     * 查询是否是节假日
     */
    static boolean isHoliday(Date date) {
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
    static List<Date> getHoliday() {
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
    static Date getWorkDay(Date date, int workDay) {
        List<Date> holidays = getHoliday();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        for (int i = 0; i < workDay; i++) {
            //判断日期是否是法定假日
            if (null != holidays) {
                boolean isHolidays = false;
                for (Date holiday : holidays) {
                    if (isSameDate(holiday, calendar.getTime())) {
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
    static boolean isSameDate(String dateStr1, String dateStr2) {
        return isSameDate(parse(dateStr1, yyyyMMdd), parse(dateStr2, yyyyMMdd));
    }

    /**
     * 比较两个时间是否为同一天
     */
    static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 判断是否是当月最后一天
     * @param date 日期
     */
    static boolean isLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.get(Calendar.DAY_OF_MONTH) == 1;
    }

    /**
     * 获取时间描述(用于简化时间)
     */
    static String getTimeDisplay(Date date) {
        Calendar nowCalendar = new GregorianCalendar();
        int currentYear = nowCalendar.get(Calendar.YEAR);
        int currentMonth = nowCalendar.get(Calendar.MONTH) + 1;
        // 计算服务器返回时间与当前时间差值
        long seconds = (System.currentTimeMillis() - date.getTime()) / 1000;
        long minute = seconds / 60;
        long hours = minute / 60;
        long day = hours / 24;
        long month = day / calculationDaysOfMonth(currentYear, currentMonth);
        long year = month / 12;

        if (year > 0) {
            return year + "年前";
        } else {
            if (month > 0) {
                return month + "月前";
            } else {
                if (day > 0) {
                    return day + "天前";
                } else {
                    if (hours > 0) {
                        return hours + "小时前";
                    } else {
                        if (minute > 0) {
                            return minute + "分钟前";
                        } else {
                            return "1" + "分钟前";
                        }
                    }
                }
            }
        }
    }

    /**
     * 计算月数
     */
    static int calculationDaysOfMonth(int year, int month) {
        int day = 0;
        switch (month) {
            // 31天
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            // 30天
            case 4:
            case 6:
            case 9:
            case 11:
                day = 30;
                break;
            // 计算2月天数
            case 2:
                day = year % 100 == 0 ? year % 400 == 0 ? 29 : 28 : year % 4 == 0 ? 29 : 28;
                break;
            default:
                break;
        }
        return day;
    }

    /**
     * 根据星期获取描述
     */
    static String getWeekDesc(String weeks) {
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
