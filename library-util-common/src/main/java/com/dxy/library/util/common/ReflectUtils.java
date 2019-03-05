package com.dxy.library.util.common;

import org.apache.commons.lang3.BooleanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 反射工具类
 * @author duanxinyuan
 * 2015-01-16 20:43
 */
public class ReflectUtils {

    /**
     * 设置属性可见
     */
    public static void setAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers())
                || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
                || Modifier.isFinal(field.getModifiers()))
                && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * 设置方法可见
     */
    public static void setAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) ||
                !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * 设置构造方法可见
     */
    public static void setAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers())
                || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers()))
                && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }

    /**
     * 将某个数组里面的对象转换为另一个对象的list返回
     */
    public static <T> List<T> transform(Object[] ts, Class<T> cls) {
        if (ts == null || ts.length <= 0) {
            return null;
        }
        List<T> newList = new ArrayList<>();
        for (Object obj : ts) {
            newList.add(ReflectUtils.transform(obj, cls));
        }
        return newList;
    }

    /**
     * 将某个List里面的对象转换为另一个对象的list返回
     */
    public static <T> List<T> transform(List list, Class<T> cls) {
        if (list == null || list.size() <= 0) {
            return null;
        }
        List<T> newList = new ArrayList<>();
        for (Object obj : list) {
            newList.add(ReflectUtils.transform(obj, cls));
        }
        return newList;
    }

    /**
     * 将某个对象转换为另外一个类型的对象
     * @param obj 要转换的对象
     * @param cls 转换成为的类型
     */
    public static <T> T transform(Object obj, Class<T> cls) {
        //创建一个对象
        T t = ClassUtils.instantiateClass(cls);

        //获取目标class的属性
        List<Field> fieldList = new ArrayList<>();
        setFields(cls, fieldList);

        Field[] fields = fieldList.toArray(new Field[0]);
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            Object value;
            try {
                value = getFieldValue(obj, name);
                if (value != null) {
                    field.set(t, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return t;
    }

    /**
     * 获取元素值
     */
    public static Object getFieldValue(Object obj, String name) {
        try {
            if (obj == null) {
                return null;
            }
            Field field = getField(obj.getClass(), name);
            if (field == null) {
                return invokeGet(obj, name);
            } else {
                return field.get(obj);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置某个对象的某个值
     * @param obj 被设置的对象
     * @param name 对象对应的属性名称
     * @param value 设置对应的值
     */
    public static void setFieldValue(Object obj, String name, Object value) {
        Field field = getField(obj.getClass(), name);
        if (null != field) {
            try {
                Class<?> type = field.getType();
                if (type == Short.class && !(value instanceof Short)) {
                    value = Short.parseShort(String.valueOf(value));
                } else if (type == Integer.class && !(value instanceof Integer)) {
                    value = Integer.parseInt(String.valueOf(value));
                } else if (type == Long.class && !(value instanceof Long)) {
                    value = Long.parseLong(String.valueOf(value));
                } else if (type == Double.class && !(value instanceof Double)) {
                    value = Double.parseDouble(String.valueOf(value));
                } else if (type == BigDecimal.class && !(value instanceof BigDecimal)) {
                    value = new BigDecimal(String.valueOf(value));
                } else if (type == Float.class && !(value instanceof Float)) {
                    value = Float.parseFloat(String.valueOf(value));
                } else if (type == Boolean.class && !(value instanceof Boolean)) {
                    value = BooleanUtils.toBoolean(String.valueOf(value));
                } else if (type == String.class && !(value instanceof String)) {
                    value = String.valueOf(value);
                }
                field.set(obj, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            invokeSet(obj, name, value);
        }
    }

    /**
     * 获取元素值
     */
    private static Field getField(Class cls, String name) {
        if (cls == null) {
            return null;
        }
        try {
            Field field = cls.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return getField(cls.getSuperclass(), name);
        }
    }

    /**
     * 获取类的属性数组
     * @param className 类名
     */
    public static Field[] getFields(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return clazz.getDeclaredFields();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置类的属性数组
     * @param cls 类名
     * @param result 数组值
     */
    private static void setFields(Class cls, List<Field> result) {
        if (cls == null) {
            return;
        }
        Field[] fields = cls.getDeclaredFields();
        result.addAll(Arrays.asList(fields));
        setFields(cls.getSuperclass(), result);
    }

    /**
     * 合并两个对象
     */
    public static <T> T combine(T source, T target) {
        Class sourceClass = source.getClass();
        Class targetClass = target.getClass();

        Field[] sourceFields = sourceClass.getDeclaredFields();
        Field[] targetFields = targetClass.getDeclaredFields();
        for (int i = 0; i < sourceFields.length; i++) {
            Field sourceField = sourceFields[i];
            Field targetField = targetFields[i];
            sourceField.setAccessible(true);
            targetField.setAccessible(true);
            try {
                if (sourceField.get(source) != null && !"serialVersionUID".equals(sourceField.getName())) {
                    targetField.set(target, sourceField.get(source));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return target;
    }

    /**
     * 执行set方法
     * @param obj 执行对象
     * @param fieldName 属性
     * @param value 值
     */
    public static void invokeSet(Object obj, String fieldName, Object value) {
        try {
            Class<?>[] parameterTypes = new Class<?>[1];
            parameterTypes[0] = obj.getClass().getDeclaredField(fieldName).getType();
            String sb = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = obj.getClass().getMethod(sb, parameterTypes);
            method.invoke(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行get方法
     * @param obj 执行对象
     * @param fieldName 属性
     */
    public static Object invokeGet(Object obj, String fieldName) {
        try {
            String sb = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = obj.getClass().getMethod(sb);
            return method.invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
