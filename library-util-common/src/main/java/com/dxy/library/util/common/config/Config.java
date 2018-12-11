package com.dxy.library.util.common.config;

import lombok.Data;

/**
 * @author duanxinyuan
 * 2018/9/14 18:14
 */
@Data
public class Config<T> {

    public static final String DEFAULT_NAME = "config_default_name";

    private String key;

    private String name;

    private T value;

    public Config(String key, String name, T value) {
        this.key = key;
        this.name = name;
        this.value = value;
    }
}
