package com.example.chain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class AlarmLogDTO implements Serializable {

    private static final long serialVersionUID = 290191026653721239L;

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
     * 是否告警  0：不告警  1：告警
     */
    private Integer alarm;

    /**
     * 告警级别
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
     * 告警时间
     */
    private String time;

    /**
     * 是否在线
     */
    private Boolean online;
}
