package com.dxy.common.util;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

/**
 * 线程池工具类
 * @author duanxinyuan
 * 2017/8/14 18:30
 */
public class ExecutorUtil {

    private volatile static ExecutorUtil instance;

    //缓存线程池
    private ExecutorService executorService;

    // 定时线程池
    private ScheduledExecutorService scheduledExecutorService;

    private ExecutorUtil() {
        executorService = getExecutorService("ExecutorUtil", 5, 20, 1024);
        scheduledExecutorService = getScheduledExecutorService("ExecutorUtil", 5);
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "ExecutorUtil-shutdownhook"));
    }

    public static ExecutorUtil getInstance() {
        if (null == instance) {
            synchronized (ExecutorUtil.class) {
                if (null == instance) {
                    instance = new ExecutorUtil();
                }
            }
        }
        return instance;
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
     * @param maxmumPoolSize 最大线程数
     * @param queueSize 队列长度
     */
    public static ExecutorService getExecutorService(String name, int corePollSize, int maxmumPoolSize, int queueSize) {
        BasicThreadFactory basicThreadFactory = new BasicThreadFactory.Builder().namingPattern(name + "-pool-%d").daemon(true).build();
        return new ThreadPoolExecutor(corePollSize, maxmumPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(queueSize), basicThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void schedule(long delay, TimeUnit unit, Runnable command) {
        scheduledExecutorService.schedule(command, delay, unit);
    }

    public void execute(Runnable command) {
        executorService.execute(command);
    }

    public void shutdown() {
        executorService.shutdown();
        scheduledExecutorService.shutdown();
    }
}

