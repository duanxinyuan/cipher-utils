package com.dxy.util.common;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

/**
 * 线程池工具类
 * @author duanxinyuan
 * 2017/8/14 18:30
 */
public class ExecutorUtils {

    private ExecutorUtils() {
    }

    /**
     * 生成定时线程池
     * @param name 名称
     * @param corePollSize 核心线程数
     */
    public static ScheduledExecutorService getScheduledExecutorService(String name, int corePollSize) {
        BasicThreadFactory basicThreadFactory = new BasicThreadFactory.Builder().namingPattern(name + "-schedule-pool-%d").daemon(true).build();
        return new ScheduledThreadPoolExecutor(corePollSize, basicThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 生成缓存线程池
     * @param name 名称
     * @param corePollSize 核心线程数
     */
    public static ExecutorService getExecutorService(String name, int corePollSize) {
        return getExecutorService(name, corePollSize, corePollSize, 1024, 60);
    }

    /**
     * 生成缓存线程池
     * @param name 名称
     * @param corePollSize 核心线程数
     * @param maxmumPoolSize 最大线程数
     * @param queueSize 队列长度
     */
    public static ExecutorService getExecutorService(String name, int corePollSize, int maxmumPoolSize, int queueSize) {
        return getExecutorService(name, corePollSize, maxmumPoolSize, queueSize, 60);
    }

    /**
     * 生成缓存线程池
     * @param name 名称
     * @param corePollSize 核心线程数
     * @param maxmumPoolSize 最大线程数
     * @param queueSize 队列长度
     * @param keepAliveSeconds 空闲线程存活时间
     */
    public static ExecutorService getExecutorService(String name, int corePollSize, int maxmumPoolSize, int queueSize, int keepAliveSeconds) {
        BasicThreadFactory basicThreadFactory = new BasicThreadFactory.Builder().namingPattern(name + "-pool-%d").daemon(true).build();
        if (queueSize <= 0) {
            queueSize = corePollSize;
        }
        return new ThreadPoolExecutor(corePollSize, maxmumPoolSize, keepAliveSeconds, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(queueSize), basicThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }
}

