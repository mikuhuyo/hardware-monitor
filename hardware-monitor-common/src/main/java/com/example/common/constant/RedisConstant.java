package com.example.common.constant;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
public class RedisConstant {
    /**
     * redis-告警信息沉默周期前缀
     * cold:storage:device:cycle:${deviceId}:${quotaKey}:${level}
     * deviceId: 设备id
     * quotaKey: 指标key(例如风扇转速异常指标-fans)
     * level: 告警等级
     */
    public static final String CYCLE_KEY = "cold:storage:device:cycle:%s:%s:%s";
}
