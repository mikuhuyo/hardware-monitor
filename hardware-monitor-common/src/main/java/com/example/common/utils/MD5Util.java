package com.example.common.utils;

import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;

/**
 * <p>
 * MD5加密
 * 单向加密算法（不可逆）
 * 特点: 加密速度快, 不需要秘钥, 但是安全性不高, 需要搭配随机盐值使用
 * </p>
 *
 * @author YueLiMin
 * @version 1.0.0
 * @since 11
 */
public class MD5Util {

    /**
     * 加密(加密内容, 随机盐, 编码)
     *
     * @param content
     * @param salt
     * @param charset
     * @return
     */
    public static String sign(String content, String salt, String charset) {
        content = content + salt;
        return DigestUtils.md5DigestAsHex(getContentBytes(content, charset));
    }

    /**
     * 对比
     *
     * @param content
     * @param sign
     * @param salt
     * @param charset
     * @return
     */
    public static boolean verify(String content, String sign, String salt, String charset) {
        content = content + salt;
        String mysign = DigestUtils.md5DigestAsHex(getContentBytes(content, charset));

        return mysign.equals(sign);
    }

    private static byte[] getContentBytes(String content, String charset) {
        if (!"".equals(charset)) {
            try {
                return content.getBytes(charset);
            } catch (UnsupportedEncodingException var3) {
                throw new RuntimeException("MD5签名过程中出现错误, 指定的编码集错误");
            }
        } else {
            return content.getBytes();
        }
    }
}