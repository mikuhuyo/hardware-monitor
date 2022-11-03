package com.example.common.utils;

import java.util.Random;

/**
 * @author YueLiMin
 * @version 1.0.0
 * @since 11
 */
public class SaltUtil {
    /**
     * 获取指定位数的随机盐
     *
     * @param x 字符数
     * @return 随机盐值
     */
    public static String getRandomSalt(int x) {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234567890!@#$%^&*()".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < x; i ++) {
            char c = chars[new Random().nextInt(chars.length)];
            stringBuilder.append(c);
        }

        return stringBuilder.toString();
    }
}

