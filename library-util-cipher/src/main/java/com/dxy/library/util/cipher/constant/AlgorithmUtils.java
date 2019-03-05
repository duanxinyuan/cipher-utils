package com.dxy.library.util.cipher.constant;

/**
 * 组装加密算法的工具类
 * @author duanxinyuan
 * 2019/2/19 21:11
 */
public interface AlgorithmUtils {

    /**
     * 获取加密算法
     * @param algorithm 加密算法
     * @param mode 密码块工作模式
     * @param padding 填充方式
     * @return 加密算法全称
     */
    static String getAlgorithm(Algorithm algorithm, Mode mode, Padding padding) {
        return algorithm.getAlgorithm() + "/" + mode.getMode() + "/" + padding.getPadding();
    }

}
