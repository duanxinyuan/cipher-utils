package com.dxy.util.common;

import com.dxy.library.json.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 反射工具类
 * @author duanxinyuan
 * 2015-01-16 20:43
 */
@Slf4j
public class ReflectUtils {

    /**
     * 实例化一个对象, 执行它的set方法, 为其属性赋值, propertyNames为该对象的属性名称数组,
     * propertyValues为属性的值, propertyValues必须和propertyNames的顺序对应.
     * @param className 类名
     * @param propertyNames 属性名称数组
     * @param propertyValues 属性值数组
     */
    public static Object newClass(String className, String[] propertyNames, Object[] propertyValues) throws Exception {
        Class<?> clazz = Class.forName(className);
        Object obj = clazz.newInstance();
        for (int i = 0; i < propertyNames.length; i++) {
            executeSet(obj, propertyNames[i], propertyValues[i]);
        }
        return obj;
    }

    /**
     * 实例化一个对象, map为该对象的属性数据, map的key是对象的属性名称,
     * map的value通过set方法为属性赋值.
     * 该对象必须有和map的key对应的属性, 属性必须有set方法
     * @param className 类名
     * @param map 数据
     */
    public static Object newClass(String className, Map<String, Object> map) throws Exception {
        Class<?> clazz = Class.forName(className);
        Object obj = clazz.newInstance();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String propertyName = entry.getKey();
            Object propertyValue = entry.getValue();
            executeSet(obj, propertyName, propertyValue);
        }
        return obj;
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
        T distObj = null;
        try {
            distObj = cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("transform newInstance error, obj: {}, cls: {}", obj.toString(), cls.getName(), e);
        }

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
                    field.set(distObj, value);
                }
            } catch (IllegalAccessException e) {
                log.error("setField error, obj: {}, field: {}", obj.toString(), name, e);
            }
        }
        return distObj;
    }

    /**
     * 获取元素值
     */
    public static Object getFieldValue(Object obj, String name) {
        Field sourceField;
        try {
            if (obj == null) {
                return null;
            }
            sourceField = getField(obj.getClass(), name);
            if (sourceField == null) {
                return null;
            }
            return sourceField.get(obj);
        } catch (IllegalAccessException e) {
            log.error("getField error, obj: {}, field: {}", obj.toString(), name, e);
        }
        return null;
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
                if (type == Integer.class) {
                    value = NumberUtils.toInt(String.valueOf(value));
                } else if (type == Long.class) {
                    value = NumberUtils.toLong(String.valueOf(value));
                } else if (type == Double.class) {
                    value = NumberUtils.toDouble(String.valueOf(value));
                } else if (type == BigDecimal.class) {
                    value = NumberUtils.toDouble(String.valueOf(value));
                } else if (type == Float.class) {
                    value = NumberUtils.toFloat(String.valueOf(value));
                } else if (type == Boolean.class) {
                    value = BooleanUtils.toBoolean(String.valueOf(value));
                }
                field.set(obj, value);
            } catch (IllegalAccessException e) {
                log.error("setFieldValue error, obj: {}, field: {}", obj.toString(), name, e);
            }
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
            log.error("getFields error, className: {}", className, e);
            return null;
        }
    }

    /**
     * 设置类的属性数组
     * @param cls 类名
     * @param result 数组值
     */
    public static void setFields(Class cls, List<Field> result) {
        if (cls == null) {
            return;
        }
        Field[] fields = cls.getDeclaredFields();
        result.addAll(Arrays.asList(fields));
        setFields(cls.getSuperclass(), result);
    }

    /**
     * 合并 dto
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
                log.error("combine error, sourceField: {}, targetField: {}", sourceField, targetField, e);
            }
        }
        return target;
    }


    /**
     * 实例化一个对象
     * @param className 类名 通过类似Integer.class.getName(),或者obj.getClass().getName()获取
     */
    public static Object newInstance(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return clazz.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            log.error("newInstance error, className: {}", className, e);
            return null;
        }
    }

    /**
     * 实例化带有参数的对象, 该类有带有参数的构造器, 且构造器的参数顺序和该方法参数values对应
     * @param className 类名
     * @param values 构造器参数值
     */
    public static Object newInstance(String className, Object... values) {
        try {
            Class<?> clazz = Class.forName(className);
            //获取参数clazz
            Class<?>[] clazzs = new Class<?>[values.length];
            for (int i = 0; i < values.length; i++) {
                clazzs[i] = values[i].getClass();
            }
            //获取有参数构造器
            Constructor<?> c = clazz.getConstructor(clazzs);
            return c.newInstance(values);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            log.error("newInstance error, className: {}, values: {}", className, GsonUtil.to(values), e);
            return null;
        }
    }

    /**
     * 转换类型, 该方法通过将obj转为String数据, 再将String数据转为className类型
     */
    public static <T> T parseClassName(Object obj, Class<T> c) {
        if (obj == null) {
            return null;
        }
        try {
            //将obj转换为String
            String value;
            if (obj instanceof Exception) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ((Exception) obj).printStackTrace(pw);
                sw.flush();
                pw.flush();
                value = sw.toString();
                sw.close();
                pw.close();
            } else if (obj instanceof String) {
                value = (String) obj;
            } else {
                value = String.valueOf(obj);
            }
            //将String转换为className类型
            if (c == String.class) {
                return (T) value;
            } else {
                return GsonUtil.fromLenient(value, c);
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 执行set方法
     * @param propertyName 变量名
     * @param propertyValue 变量值
     */
    public static void executeSet(Object obj, String propertyName, Object propertyValue) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(propertyName, obj.getClass());
            Method method = pd.getWriteMethod();
            method.invoke(obj, propertyValue);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("setValueByProperty error, obj: {}. propertyName: {}, propertyValue: {}", GsonUtil.to(obj), propertyName, propertyValue, e);
        }
    }

    /**
     * 执行get方法
     * @param propertyName 变量名
     * @param obj 变量值
     */
    public static Object executeGet(Object obj, String propertyName) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(propertyName, obj.getClass());
            Method method = pd.getReadMethod();
            return method.invoke(obj);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            log.error("getValueByProperty error, obj: {}. propertyName: {}", GsonUtil.to(obj), propertyName, e);
            return null;
        }
    }

}
