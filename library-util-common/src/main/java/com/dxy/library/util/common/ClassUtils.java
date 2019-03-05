package com.dxy.library.util.common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class操作工具类
 * @author duanxinyuan
 * 2018/8/15 下午3:52
 */
public class ClassUtils extends org.apache.commons.lang3.ClassUtils {

    /**
     * 实例化一个对象
     * @param className 类名 通过类似Integer.class.getName(),或者obj.getClass().getName()获取
     */
    public static Object instantiateClass(String className, Object... initargs) {
        try {
            return instantiateClass(ClassUtils.getClass(className), initargs);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 实例化类
     * @param type 类
     */
    public static <T> T instantiateClass(Class<T> type, Object... initargs) {
        if (type.isInterface()) {
            throw new RuntimeException("the class is an interface");
        }
        try {
            //获取构造器
            Constructor<T> constructor = type.getDeclaredConstructor();
            return instantiateClass(constructor, initargs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 实例化类
     * 如果有参数，参数需要和构造方法参数的顺序对应
     * @param constructor 构造器
     * @param initargs 构造器参数
     */
    public static <T> T instantiateClass(Constructor<T> constructor, Object... initargs) {
        try {
            ReflectUtils.setAccessible(constructor);
            //Class.newInstance()只能反射无参的构造器；
            //Constructor.newInstance()可以反任何构造器；
            //
            //Class.newInstance()需要构造器可见(visible)；
            //Constructor.newInstance()可以反私有构造器；
            //
            //Class.newInstance()对于捕获或者未捕获的异常均由构造器抛出;
            //Constructor.newInstance()通常会把抛出的异常封装成InvocationTargetException抛出；
            return constructor.newInstance(initargs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用系统类加载器读取包名下的所有class
     * @param packageName 包名
     */
    public static Set<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return getClasses(loader, packageName);
    }

    /**
     * 读取包名下的所有class
     * @param loader 加载器（系统类加载器、扩展类加载器、启动类加载器）
     * @param packageName 包名
     */
    public static Set<Class<?>> getClasses(ClassLoader loader, String packageName) throws IOException, ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        String path = packageName.replace(".", "/");
        Enumeration<URL> resources = loader.getResources(path);
        if (resources != null) {
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocol = url.getProtocol();

                if (StringUtils.equals("file", protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8.name());
                    classes.addAll(getClassesFromDirectory(new File(filePath), packageName));
                } else if (StringUtils.equalsIgnoreCase("jar", protocol)) {
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    classes.addAll(getClassesFromJARFile(jar, path));
                }
            }
        }
        return classes;
    }

    /**
     * 读取文件夹下的所有class
     * @param directory 文件夹
     * @param packageName 包名
     */
    public static Set<Class<?>> getClassesFromDirectory(File directory, String packageName) throws ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    String subPackageName = packageName + "." + file.getName();
                    classes.addAll(getClassesFromDirectory(file, subPackageName));
                } else {
                    String fileName = file.getName();
                    if (fileName.endsWith(".class")) {
                        if (fileName.contains(".")) {
                            fileName = fileName.substring(0, fileName.lastIndexOf("."));
                        }
                        String name = packageName + "." + fileName;
                        Class<?> clazz = getClass(name);
                        classes.add(clazz);
                    }
                }
            }
        }
        return classes;
    }

    /**
     * 读取Jar文件下的所有class
     * @param jarFile jar文件
     * @param packageName 包名
     */
    public static Set<Class<?>> getClassesFromJARFile(JarFile jarFile, String packageName) throws ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (jarEntry != null) {
                String className = jarEntry.getName();
                if (className.endsWith(".class")) {
                    if (className.contains(".")) {
                        className = className.substring(0, className.lastIndexOf("."));
                    }
                    if (className.startsWith(packageName)) {
                        classes.add(getClass(className.replace("/", ".")));
                    }
                }
            }
        }
        return classes;
    }

}
