package com.dxy.library.util.common;

import org.apache.commons.exec.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 系统工具类
 * @author duanxinyuan
 * 2018/8/1 15:53
 */
public class SystemUtils {
    private static String WINDOWS = "Windows";
    private static String MAC = "Mac";
    private static String LINUX = "Linux";

    /**
     * 判断是否是Windows系统
     */
    public static boolean isWindows() {
        return getOsName().contains(WINDOWS);
    }

    /**
     * 判断是否是Linux系统
     */
    public static boolean isLinux() {
        return getOsName().contains(LINUX);
    }

    /**
     * 判断是否是Linux系统
     */
    public static boolean isMac() {
        return getOsName().contains(MAC);
    }

    /**
     * 获取操作系统名称
     */
    public static String getOsName() {
        return System.getProperty("os.name");
    }

    /**
     * 同步执行系统命令
     */
    public static String executeCommand(String command) {
        CommandLine cmdLine = CommandLine.parse(command);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValues(null);
        //十分钟超时
        ExecuteWatchdog watchdog = new ExecuteWatchdog(600 * 1000);
        executor.setWatchdog(watchdog);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

        executor.setStreamHandler(streamHandler);
        try {
            executor.execute(cmdLine);
            //获取程序外部程序执行结果
            return outputStream.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 异步执行系统命令
     */
    public static void executeCommandAsync() {
        //开启windows telnet: net start telnet
        //注意：第一个空格之后的所有参数都为参数
        CommandLine cmdLine = new CommandLine("net");
        cmdLine.addArgument("start");
        cmdLine.addArgument("telnet");
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(1);
        //设置60秒超时，执行超过60秒后会直接终止
        ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
        executor.setWatchdog(watchdog);
        DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();
        try {
            executor.execute(cmdLine, handler);
            //命令执行返回前一直阻塞
            handler.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
