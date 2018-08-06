package com.dxy.common.util;

import com.dxy.library.json.GsonUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author duanxinyuan
 * 2018/8/6 12:44
 */
public class ConfigUtil {

    private static Properties rootProp = new Properties();

    static {
        try (InputStream inputStream = ConfigUtil.class.getClassLoader().getResourceAsStream("application.properties");
             InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8")) {
            rootProp.load(reader);
        } catch (Exception e) {
            throw new Error("init config error.", e);
        }
    }

    /**
     * 获取配置
     * @param key 配置名称
     */
    public static <T> T getConfig(String key, Class<T> cls) {
        String value = getConfig(key);
        T t = GsonUtil.from(value, cls);
        return t;
    }

    /**
     * 获取配置
     * @param key 配置名称
     */
    public static String getConfig(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return rootProp.getProperty(key);
    }

}

