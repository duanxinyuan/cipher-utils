package com.dxy.library.util.common;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Method;
import java.util.*;

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
        HashMap<Integer, String> map = Maps.newHashMap();
        List<V> listNew = new ArrayList<V>();
        if (list.size() <= n) {
            return list;
        } else {
            while (map.size() < n) {
                int random = (int) (Math.random() * list.size());
                if (!map.containsKey(random)) {
                    map.put(random, "");
                    listNew.add(list.get(random));
                }
            }
            return listNew;
        }
    }

    /***
     *从List中根据权重随机获取count个数据
     */
    public static <T> List<T> getRandom(String[] keys, int[] weights, Integer count, List<T> ts, List<Long> excepts, String verifyKy) {
        if (keys == null || weights == null || keys.length != weights.length) {
            return ts;
        }
        List<WeightElement> weightElements = new ArrayList<>();

        for (int i = 0; i < keys.length; i++) {
            weightElements.add(new WeightElement(keys[i], weights[i]));
        }

        if (weightElements.size() == 0) {
            return ts;
        }

        WeightElement ele0 = weightElements.get(0);
        ele0.setThresholdLow(0);
        ele0.setThresholdHigh(ele0.getWeight());

        for (int i = 1; i < weightElements.size(); i++) {
            WeightElement curElement = weightElements.get(i);
            WeightElement preElement = weightElements.get(i - 1);

            curElement.setThresholdLow(preElement.getThresholdHigh());
            curElement.setThresholdHigh(curElement.getThresholdLow() + curElement.getWeight());
        }

        Random r = new Random();
        Map<String, List<Integer>> map = new TreeMap<>();
        //根据权重配比 得到随机数
        for (int i = 0; i < count; i++) {

            Integer thresholdHigh = weightElements.get(weightElements.size() - 1).getThresholdHigh();
            if (thresholdHigh == 0) {
                continue;
            }
            Integer rv = r.nextInt(thresholdHigh);

            if (rv < 0 || rv > weightElements.get(weightElements.size() - 1).getThresholdHigh() - 1) {
                return null;
            }
            WeightElement randomValue;
            //此时rv必然在0 - getMaxRandomValue()-1范围内，
            //也就是必然能够命中某一个值
            int start = 0, end = weightElements.size() - 1;
            int index = weightElements.size() / 2;
            while (true) {
                if (rv < weightElements.get(index).getThresholdLow()) {
                    end = index - 1;
                } else if (rv >= weightElements.get(index).getThresholdHigh()) {
                    start = index + 1;
                } else {
                    randomValue = weightElements.get(index);
                    break;
                }
                index = (start + end) / 2;
            }
            String key = randomValue == null ? "" : randomValue.getKey();
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            T t = ts.get(rv);
            Object fieldValue = ReflectUtils.getFieldValue(t, verifyKy);
            if (map.get(key).contains(rv) || (t != null && excepts.contains(NumberUtils.toLong(String.valueOf(fieldValue))))) {
                count++;
                //防止死循环
                if (count >= 200) {
                    break;
                }
            } else {
                map.get(key).add(rv);
            }
        }
        List<Integer> indexs = new ArrayList<>();
        List<T> result = new ArrayList<>();
        map.forEach((k, v) -> indexs.addAll(v));
        indexs.forEach(i -> result.add(ts.get(i)));
        return result;
    }

    public static class WeightElement {
        /**
         * 元素标记
         */
        private String key;
        /**
         * 元素权重
         */
        private Integer weight;
        /**
         * 权重对应随机数范围低线
         */
        private Integer thresholdLow;
        /**
         * 权重对应随机数范围高线
         */
        private Integer thresholdHigh;

        public WeightElement() {
        }

        public WeightElement(Integer weight) {
            this.key = weight.toString();
            this.weight = weight;
        }

        public WeightElement(String key, Integer weight) {
            this.key = key;
            this.weight = weight;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Integer getWeight() {
            return weight;
        }

        public void setWeight(Integer weight) {
            this.weight = weight;
        }

        public Integer getThresholdLow() {
            return thresholdLow;
        }

        public void setThresholdLow(Integer thresholdLow) {
            this.thresholdLow = thresholdLow;
        }

        public Integer getThresholdHigh() {
            return thresholdHigh;
        }

        public void setThresholdHigh(Integer thresholdHigh) {
            this.thresholdHigh = thresholdHigh;
        }

        @Override
        public String toString() {
            return "key:" + this.key + " weight:" + this.weight + " low:" + this.thresholdLow + " heigh:" + this.thresholdHigh;
        }
    }


}
