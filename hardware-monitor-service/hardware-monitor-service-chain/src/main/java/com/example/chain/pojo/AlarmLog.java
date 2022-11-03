package com.example.chain.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>报警日志elasticsearch pojo类</p>
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class AlarmLog implements Serializable {
    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 指标id
     */
    private Long quotaId;

    /**
     * 指标名称
     */
    private String quotaName;

    /**
     * 是否告警 0-不告警 1-告警
     */
    private Integer alarm;

    /**
     * 告警级别 0-正常 1-一般警报 2-严重警报
     */
    private Integer level;

    /**
     * 告警名称
     */
    private String alarmName;

    /**
     * 单位
     */
    private String unit;

    /**
     * 参考值
     */
    private String referenceValue;

    /**
     * 数值指标
     */
    private String value;

    /**
     * 告警时间(秒)
     */
    private Long alarmLogTime;
}
