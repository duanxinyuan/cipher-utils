package com.dxy.library.util.common;

import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * List工具类
 * @author duanxinyuan
 * 2017/4/5 19:08
 */
public class ListUtils {

    /**
     * 判断数组是否为空
     * @return false-为空
     */
    public static <T> boolean isEmpty(T[] list) {
        return null == list || list.length == 0;
    }

    /**
     * 判断数组是否为空
     * @return false-为空
     */
    public static <T> boolean isNotEmpty(T[] list) {
        return null != list && list.length > 0;
    }

    /**
     * 判断List是否为空
     * @return false-为空
     */
    public static <T> boolean isEmpty(List<T> list) {
        return null == list || list.size() == 0;
    }

    /**
     * 判断List是否为空
     * @return false-为空
     */
    public static <T> boolean isNotEmpty(List<T> list) {
        return null != list && list.size() > 0;
    }

    /**
     * 升序排序
     * @param list 要排序的list
     * @param sortField 排序的字段
     */
    public static <T> void sortAsc(List<T> list, final String sortField) {
        sort(list, sortField, "asc");
    }

    /**
     * 降序排序
     * @param list 要排序的list
     * @param sortField 排序的字段
     */
    public static <T> void sortDesc(List<T> list, final String sortField) {
        sort(list, sortField, "desc");
    }

    /**
     * list排序
     * @param list 要排序的list
     * @param sortField 排序的字段
     * @param sortMode 排序的模式（asc或者desc）
     */
    private static <T> void sort(List<T> list, final String sortField, final String sortMode) {
        Collections.sort(list, (obj1, obj2) -> {
            int retVal;
            try {
                //首字母转大写
                String newStr = sortField.substring(0, 1).toUpperCase() + sortField.replaceFirst("\\w", "");
                String methodStr = "get" + newStr;

                Method method1 = ((T) obj1).getClass().getMethod(methodStr, (Class<?>) null);
                Method method2 = ((T) obj2).getClass().getMethod(methodStr, (Class<?>) null);
                if (sortMode != null && "desc".equals(sortMode)) {
                    // 倒序
                    retVal = method2.invoke(obj2, (Object) null).toString().compareTo(method1.invoke(obj1, (Object) null).toString());
                } else {
                    // 正序
                    retVal = method1.invoke(obj1, (Object) null).toString().compareTo(method2.invoke(obj2, (Object) null).toString());
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
            return retVal;
        });
    }

    /**
     * 截取前几个数据
     * @param blockSize 指定大小
     */
    public static <T> List<T> sub(List<T> list, int blockSize) {
        List<T> result = new ArrayList<>(blockSize);
        for (int i = 0; i < list.size(); i++) {
            if (i >= blockSize) {
                break;
            }
            result.add(list.get(i));
        }
        return result;
    }

    /**
     * 按照指定大小切割 list
     * @param blockSize 指定大小
     */
    public static <T> List<List<T>> split(List<T> list, int blockSize) {
        List<List<T>> lists = new ArrayList<>();
        if (list != null && blockSize > 0) {
            int listSize = list.size();
            if (listSize <= blockSize) {
                lists.add(list);
                return lists;
            }
            int batchSize = listSize / blockSize;
            int remain = listSize % blockSize;
            for (int i = 0; i < batchSize; i++) {
                int fromIndex = i * blockSize;
                int toIndex = fromIndex + blockSize;
                lists.add(list.subList(fromIndex, toIndex));
            }
            if (remain > 0) {
                lists.add(list.subList(listSize - remain, listSize));
            }
        }
        return lists;
    }

    /**
     * 从list中随机抽取1条数据
     */
    private static <V> List<V> getRandom(List<V> list) {
        return getRandom(list, 1);
    }

    /**
     * 从list中随机抽取n个元素
     */
    private static <V> List<V> getRandom(List<V> list, int n) {
        if (list.size() <= n) {
            return list;
        } else {
            List<V> listNew = new ArrayList<>();
            Set<Integer> set = Sets.newHashSet();
            while (set.size() < n) {
                int random = (int) (Math.random() * list.size());
                if (!set.contains(random)) {
                    set.add(random);
                    listNew.add(list.get(random));
                }
            }
            return listNew;
        }
    }

}
