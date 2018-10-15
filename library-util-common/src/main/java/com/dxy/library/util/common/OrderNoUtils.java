package com.dxy.library.util.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 订单号工具类
 * 订单生成规则:
 * 正常情况下:年(2位)+月(2位)+日(2位)+秒(5位)+循环自增值(3位)+随机码(2位)共16位
 * 非正常情况下:年(2位)+月(2位)+日(2位)+秒(5位)+随机码(5位)共16位
 * 注:非正常情况在同时开启1W个线程的情况下并未发生
 * 业务编码(1位)：自营、通用件、全车件待定
* @author duanxinyuan
 * 2017/5/14 19:39
 */
public class OrderNoUtils {
    //循环自增上限,超出则从初始值开始
    private static int maxLoop = 999;
    private static int initNum = 100;
    private volatile static AtomicInteger atomicInteger;

    public static int matchRating = 1;

    /**
     * DCL获取单例atomicInteger
     */
    private static AtomicInteger getAtomicInteger() {
        if (atomicInteger == null) {
            synchronized (OrderNoUtils.class) {
                if (atomicInteger == null) {
                    atomicInteger = new AtomicInteger(initNum);//初始值为100
                }
            }
        }
        return atomicInteger;
    }

    /**
     * 订单号生成
     */
    public static Long next() {
        //获取单例
        AtomicInteger atomicInteger = getAtomicInteger();
        //原子性自增并返回自增前的值
        int nextValue = atomicInteger.getAndIncrement();
        //若循环次数已超过上限
        if (nextValue > maxLoop) {
            //CAS更新值成功
            if (atomicInteger.compareAndSet(nextValue + 1, initNum)) {
                atomicInteger.getAndIncrement();
                return getNextNormally(initNum);
            } else {//CAS更新值失败
                //重新获取
                nextValue = atomicInteger.getAndIncrement();
                if (nextValue <= maxLoop) {
                    return getNextNormally(nextValue);
                } else {//非正常情况下
                    return getNextUnNormally();
                }
            }
        }
        return getNextNormally(nextValue);
    }

    private static Long getNextNormally(int nextValue) {
        int randomNum = (int) (Math.random() * 90) + 10;//2位随机码
        return Long.valueOf(getDateNum() + nextValue + randomNum);
    }

    private static Long getNextUnNormally() {
        int randomNum = (int) (Math.random() * 90000) + 10000;//5位随机码
        return Long.valueOf(getDateNum() + randomNum);
    }

    /**
     * 获取订单号中的时间部分
     */
    private static String getDateNum() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String sTime = simpleDateFormat.format(date);
        Integer nSecond = Integer.valueOf(sTime.substring(6, 8)) * 60 * 60 + Integer.valueOf(sTime.substring(8, 10)) * 60
                + Integer.valueOf(sTime.substring(10, 12));
        StringBuilder sSecond = new StringBuilder(String.valueOf(nSecond));
        if (sSecond.length() < 5) {
            for (int i = 5, j = sSecond.length(); i > j; i--) {
                sSecond.insert(0, "0");
            }
        }
        return sTime.substring(0, 6) + sSecond;
    }

    public static String next(Integer type) {
        String result = "";
        if (type == matchRating) {
            result = "m" + next();
        }
        return result;
    }
}
