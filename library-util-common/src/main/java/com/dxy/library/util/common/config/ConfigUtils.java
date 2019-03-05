package com.dxy.library.util.common.config;


import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.dxy.library.json.gson.GsonUtil;
import com.dxy.library.json.jackson.JacksonUtil;
import com.dxy.library.util.common.ClassUtils;
import com.dxy.library.util.common.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读取配置文件的工具类
 * @author duanxinyuan
 * 2018/8/6 12:44
 */
public class ConfigUtils {

    private static HashMap<String, Object> properties = Maps.newHashMap();

    static {
        HashMap<String, Object> prop = JacksonUtil.fromPropRecource("application.properties", new TypeReference<HashMap<String, Object>>() {});
        if (prop != null && prop.size() > 0) {
            prop = recursion(prop);
            properties.putAll(prop);
        }
        HashMap<String, Object> yml = JacksonUtil.fromYamlRecource("application.yml", new TypeReference<HashMap<String, Object>>() {});
        if (yml != null && yml.size() > 0) {
            yml = recursion(yml);
            properties.putAll(yml);
        }
        HashMap<String, Object> yaml = JacksonUtil.fromYamlRecource("application.yaml", new TypeReference<HashMap<String, Object>>() {});
        if (yaml != null && yaml.size() > 0) {
            yaml = recursion(yaml);
            properties.putAll(yaml);
        }
    }

    /**
     * 递归将带有等级的Key转换成Map
     */
    private static HashMap<String, Object> recursion(HashMap<String, Object> prop) {
        HashMap<String, Object> result = new HashMap<>();
        List<String> removeKeys = Lists.newArrayList();
        prop.forEach((k, v) -> {
            if (ClassUtils.isAssignable(v.getClass(), Map.class)) {
                HashMap<String, Object> from = GsonUtil.from(GsonUtil.to(v), new TypeToken<HashMap<String, Object>>() {});
                result.putAll(recursion(k, result, from));
                if (!prop.containsKey(k)) {
                    removeKeys.add(k);
                }
            }
        });
        for (String removeKey : removeKeys) {
            prop.remove(removeKey);
        }
        return result;
    }

    private static HashMap<String, Object> recursion(String previousKey, HashMap<String, Object> result, HashMap<String, Object> prop) {
        for (Map.Entry<String, Object> entry : prop.entrySet()) {
            String key = entry.getKey().trim();
            Object value = entry.getValue();
            String newKey = StringUtils.isEmpty(key) ? previousKey : previousKey + "." + key;

            boolean assignableFrom = ClassUtils.isAssignable(value.getClass(), Map.class);
            if (assignableFrom) {
                prop = GsonUtil.from(GsonUtil.to(value), new TypeToken<HashMap<String, Object>>() {});
                result = recursion(newKey, result, prop);
            } else {
                result.put(newKey, value);
            }
        }
        return result;
    }

    /**
     * 获取配置
     * @param key 配置名称
     */
    public static <T> T getConfig(String key, Class<T> type) {
        return getConfig(key, type, null);
    }

    /**
     * 获取配置
     * @param key 配置名称
     */
    public static <T> T getConfig(String key, Class<T> type, T defaultValue) {
        String value = getConfig(key);
        if (StringUtils.isNotEmpty(value)) {
            return GsonUtil.fromLenient(value, type);
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取配置
     * @param key 配置名称
     */
    public static String getConfig(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Object object = properties.get(key);
        if (object == null) {
            return null;
        } else {
            return String.valueOf(object);
        }
    }

    /**
     * 获取配置
     * @param key 配置名称
     */
    public static String getConfig(String key, String defaultValue) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Object object = properties.get(key);
        if (object == null) {
            return defaultValue;
        } else {
            return String.valueOf(object);
        }
    }

    /**
     * 获取配置（以Key为前缀，获取所有符合规则的config）
     * @param key 配置名称
     */
    public static List<Config<String>> getConfigs(String key) {
        return getConfigs(key, String.class);
    }

    /**
     * 获取配置（以Key为前缀，获取所有符合规则的config）
     * @param key 配置名称
     */
    public static <T> List<Config<T>> getConfigs(String key, Class<T> type) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(key)) {
            return Lists.newArrayList();
        }
        List<Config<T>> configs = Lists.newArrayList();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String entryKey = entry.getKey();
            Object entryValue = entry.getValue();

            String name = null;
            if (entryKey.equals(key)) {
                name = Config.DEFAULT_NAME;
            } else if (entryKey.startsWith(key + ".")) {
                name = entry.getKey().replace(key + ".", "");
            }
            if (StringUtils.isNotEmpty(name)) {
                T value;
                if (type == String.class) {
                    value = (T) entryValue;
                } else {
                    value = GsonUtil.from(String.valueOf(entryValue), type);
                }
                configs.add(new Config<>(entryKey, name, value));
            }
        }
        return configs;
    }

}

