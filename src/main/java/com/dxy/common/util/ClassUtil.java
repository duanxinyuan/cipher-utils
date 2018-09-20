package com.dxy.common.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author duanxinyuan
 * 2018/8/15 下午3:52
 */
public interface ClassUtil {

    /**
     * 是否是基础数据类型或者封装类型
     */
    static boolean isPrimitiveType(Class<?> cls) {
        return cls == Object.class || cls == String.class
                || cls == Integer.class || cls == int.class
                || cls == Short.class || cls == short.class
                || cls == Float.class || cls == float.class
                || cls == Double.class || cls == double.class
                || cls == Boolean.class || cls == boolean.class
                || cls == Long.class || cls == long.class
                || cls == Character.class || cls == char.class
                || cls == Byte.class || cls == byte.class
                || cls == BigDecimal.class
                || cls == BigInteger.class
                || cls == Date.class
                || cls == Enum.class;
    }

    /**
     * 判断是否继承了父类或者实现了接口
     * @param child 子类对象
     * @param parent 父类对象
     */
    static <C, P> boolean isAssignableFrom(C child, P parent) {
        return isAssignableFrom(child.getClass(), parent.getClass());
    }

    /**
     * 判断是否继承了父类或者实现了接口
     * @param child 子类class
     * @param parent 父类class
     */
    static boolean isAssignableFrom(Class<?> child, Class<?> parent) {
        return parent.isAssignableFrom(child);
    }
}
