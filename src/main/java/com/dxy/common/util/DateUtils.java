package com.dxy.common.util;

import com.dxy.library.json.GsonUtil;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public interface DateUtils {

    /**
     * 服务器的时间格式
     */
    String FORMAT_YEAR_SECOND = "yyyy-MM-dd HH:mm:ss";
    /**
     * 年到分的时间格式
     */
    String FORMAT_YEAR_MINUTE = "yyyy-MM-dd HH:mm";
    /**
     * 年到天的时间格式
     */
    String FORMAT_YEAR_DAY = "yyyy-MM-dd";
    /**
     * 月到天的时间格式
     */
    String FORMAT_MONTH_DAY = "MM-dd";
    /**
     * 月到分的时间格式
     */
    String FORMAT_MONTH_MINUTE = "MM-dd HH:mm";
    /**
     * 时到分的时间格式
     */
    String FORMAT_HOUR_MINUTE = "HH:mm";
    /**
     * 时到秒的时间格式
     */
    String FORMAT_HOUR_SECOND = "HH:mm:ss";
    /**
     * 小时的时间格式
     */
    String FORMAT_HOUR = "HH";

    /**
     * Date转换成String
     */
    static String getFormat(Date date, String parseFormat) {
        if (null == date) {
            return "";
        }
        return DateFormatUtils.format(date, parseFormat);
    }

    /**
     * String解析成Date
     * @return Date
     */
    static Date getParse(String str, String parseFormat) {
        try {
            return new SimpleDateFormat(parseFormat, Locale.getDefault()).parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 转换时间字符串
     * @return String
     */
    static String getParseFormat(String date, String parseFormat) {
        if (StringUtils.isEmpty(date)) {
            return "";
        }
        return DateFormatUtils.format(getParse(date, parseFormat), parseFormat);
    }

    /**
     * 转换时间字符串
     * @return String
     */
    static Date getParseFormat(Date date, String parseFormat) {
        if (null == date) {
            return null;
        }
        return getParse(DateFormatUtils.format(date, parseFormat), parseFormat);
    }

    /**
     * 获取UTC时间
     */
    static OffsetDateTime getUTCTime() {
        ZoneOffset zoneOffset = ZoneOffset.of("+00:00");
        LocalDateTime localDateTime = LocalDateTime.now(zoneOffset);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtils.FORMAT_YEAR_SECOND);
        String formatter = dateTimeFormatter.format(localDateTime);
        LocalDateTime localDateTime1 = LocalDateTime.parse(formatter, dateTimeFormatter);
        return OffsetDateTime.of(localDateTime1, ZoneOffset.UTC);
    }

    /**
     * 计算两个时间相差的小时数目(精确到小数位)
     * @param beginDateStr 开始时间
     * @param endDateStr 结束时间
     * @return 小时数
     */
    static double getHourGap(String beginDateStr, String endDateStr, String parseFormat) {
        Date beginDate = getParse(beginDateStr, parseFormat);
        Date endDate = getParse(endDateStr, parseFormat);
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
        Date parse = getParse(dateStr, parseFormat);
        if (null == parse) {
            return null;
        }
        Calendar ca = Calendar.getInstance();
        ca.setTime(parse);
        ca.add(Calendar.SECOND, (int) (value * 3600));//以秒为单位计算
        return getFormat(ca.getTime(), parseFormat);
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
        String holidayStr = "[2017-01-01,2017-01-02,2017-01-27,2017-01-28,2017-01-29,2017-01-30,2017-01-31,2017-02-01,2017-02-02,2017-04-02,2017-04-03,2017-04-04,2017-04-04 ,2017-05-01,2017-05-28,2017-05-29,2017-05-30,2017-10-01 ,2017-10-02 ,2017-10-03 ,2017-10-04 ,2017-10-05 ,2017-10-06 ,2017-10-07 ,2017-10-08," +
                "2018-01-01,2018-02-15,2018-02-16,2018-02-17,2018-02-18,2018-02-19,2018-02-20,2018-02-21,2018-04-05,2018-04-06,2018-04-07,2018-04-29,2018-04-30,2018-05-01,2018-06-18,2018-09-24,2018-10-01 ,2018-10-02 ,2018-10-03 ,2018-10-04 ,2018-10-05 ,2018-10-06 ,2018-10-07," +
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
        return isSameDate(getParse(dateStr1, FORMAT_YEAR_DAY), getParse(dateStr2, FORMAT_YEAR_DAY));
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
     * 获取时间描述
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

    /**
     * 获取某段时间内的所有日期
     * @param days 多少天之前的
     * @param today 是否包含今天
     */
    static List<Date> findDates(int days, boolean today) {
        List<Date> lDate = new ArrayList<>();
        Calendar calBegin = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        calBegin.add(Calendar.DAY_OF_MONTH, -days);
        Date now = getParseFormat(new Date(), "yyyy-MM-dd 00:00:00");
        while (calEnd.getTime().after(calBegin.getTime())) {
            lDate.add(calBegin.getTime());
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            if (today && now.before(calBegin.getTime())) {
                break;
            }
        }
        return lDate;
    }

    /**
     * 获取某段时间(年 月 )内的所有日期
     * @param count 多少天之前的
     * @param years 是否是年
     */
    static List<Date> findMonthsOrYears(int count, boolean years) {
        List<Date> lDate = new ArrayList<>();
        Calendar calBegin = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int beginYear = calBegin.get(Calendar.YEAR);
        int beginMonth = calBegin.get(Calendar.MONTH);
        if (!years) {
            calBegin.clear();
            calBegin.set(Calendar.YEAR, beginYear);
            calBegin.set(Calendar.MONTH, beginMonth);
            int firstDay = calBegin.getActualMinimum(Calendar.DAY_OF_MONTH);
            //设置日历中月份的最小天数
            calBegin.set(Calendar.DAY_OF_MONTH, firstDay);
            calBegin.add(Calendar.MONTH, -count);
        } else {
            calBegin.clear();
            calBegin.set(Calendar.YEAR, beginYear);
            calBegin.add(Calendar.YEAR, -count);
        }
        while (calEnd.getTime().after(calBegin.getTime())) {
            lDate.add(calBegin.getTime());
            if (years) {
                calBegin.add(Calendar.YEAR, 1);
            } else {
                calBegin.add(Calendar.MONTH, 1);
            }
        }
        return lDate;
    }

    /**
     * 根据给的前几个月 得到每一天date  对象
     * 给3个月 ， 今天27号  结果为2个完整月 加这个月的27天
     * @param count 向前计算的月份
     */
    static List<Date> findBeforeMulitpleMonthAllDays(int count) {
        List<Date> lDate = new ArrayList<>();
        Calendar calBegin = Calendar.getInstance();
        int firstDay = calBegin.getActualMinimum(Calendar.DAY_OF_MONTH);
        Calendar firstDayCal = Calendar.getInstance();
        firstDayCal.set(Calendar.DAY_OF_MONTH, firstDay);
        firstDayCal.set(Calendar.HOUR_OF_DAY, 0); //设置0点
        Calendar firstDayCalCopyBegin = (Calendar) firstDayCal.clone(); //克隆日历
        Calendar firstDayCalCopyEnd = (Calendar) firstDayCal.clone(); //克隆日历
        while (calBegin.getTime().after(firstDayCal.getTime())) {//得到当前月所过的天
            lDate.add(firstDayCal.getTime());
            firstDayCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        firstDayCalCopyBegin.add(Calendar.MONTH, -(count - 1));//舍弃掉当前月
        while (firstDayCalCopyEnd.getTime().after(firstDayCalCopyBegin.getTime())) {//得到之前月所过的天
            lDate.add(firstDayCalCopyBegin.getTime());
            firstDayCalCopyBegin.add(Calendar.DAY_OF_MONTH, 1);
        }
        return lDate;
    }

    /***
     * 根据指定月 来查询该月下的所有date
     * @param value
     * @return
     */
    static List<Date> findDateByMonth(String value) {
        if (!value.contains("-")) {
            return new ArrayList<>();
        }

        List<Date> list = new ArrayList<>();
        String[] arr = value.split("-");
        int year = NumberUtils.toInt(arr[0]);
        int month = NumberUtils.toInt(arr[1]);
        Calendar calendar = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendarEnd.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); //月份默认从0 开始
        calendarEnd.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendarEnd.set(Calendar.DAY_OF_MONTH, calculationDaysOfMonth(year, month));
        Date nowDate = getParseFormat(new Date(), "yyyy-MM-dd 23:59:59");
        while (calendarEnd.getTime().after(calendar.getTime())) {//得到当前月所过的天
            list.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            if (nowDate.before(calendar.getTime())) {
                break; //给的日期  不能超过今天
            }
        }
        return list;
    }

    /***
     * 根据指定年 来查询该年下的所有month    yyyy-MM
     * @param value
     * @return
     */
    static List<Date> findMonthByYear(String value) {
        List<Date> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        calendar.set(Calendar.YEAR, NumberUtils.toInt(value));
        calendarEnd.set(Calendar.YEAR, NumberUtils.toInt(value));
        int actualMaximum = calendar.getActualMaximum(Calendar.MONTH);
        int actualMinimum = calendar.getActualMinimum(Calendar.MONTH);
        calendar.set(Calendar.MONTH, actualMinimum);
        calendarEnd.set(Calendar.MONTH, actualMaximum);
        Date nowDate = getParseFormat(new Date(), "yyyy-MM-dd");
        while (calendarEnd.getTime().after(calendar.getTime())) {//得到当前月所过的天
            list.add(getParseFormat(calendar.getTime(), "yyyy-MM"));
            calendar.add(Calendar.MONTH, 1);
            if (nowDate.before(calendar.getTime())) {
                break; //给的日期  不能超过今天所在 月
            }
        }
        return list;
    }


}
