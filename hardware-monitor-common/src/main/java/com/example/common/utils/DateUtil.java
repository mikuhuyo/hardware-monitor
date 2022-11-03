package com.example.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
public class DateUtil {
    /**
     * 秒格式化为具体的日期时间
     *
     * @param second 秒
     * @return 日期时间字符串 2019-01-01 00-00-00
     */
    public static String secondFormat(Long second) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(second * 1000);

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(instance.getTime());
    }

    /**
     * 毫秒值格式化为具体的日期时间
     *
     * @param millis 毫秒
     * @return 日期时间字符串 2019-01-01 00-00-00
     */
    public static String millisFormat(Long millis) {
        Date date = new Date(millis);

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    /**
     * 将2019-01-01+00:00:00格式化为秒值
     *
     * @param datetimeFormat 日期时间格式化字符串 2019-01-01+00:00:00
     * @return 秒值
     */
    public static Long datetimeFormat2second(String datetimeFormat) throws ParseException {
        String format = null;
        if (datetimeFormat.contains("+")) {
            format = datetimeFormat.replace("+", " ");
        } else {
            format = datetimeFormat;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time = simpleDateFormat.parse(format).getTime();

        return time / 1000;
    }

    /**
     * 将2019-01-01+00:00:00格式化为毫秒值
     *
     * @param datetimeFormat 日期时间格式化字符串 2019-01-01+00:00:00
     * @return 毫秒值
     */
    public static Long datetimeFormat2Millis(String datetimeFormat) throws ParseException {
        String format = null;
        if (datetimeFormat.contains("+")) {
            format = datetimeFormat.replace("+", " ");
        } else {
            format = datetimeFormat;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return simpleDateFormat.parse(format).getTime();
    }
}
