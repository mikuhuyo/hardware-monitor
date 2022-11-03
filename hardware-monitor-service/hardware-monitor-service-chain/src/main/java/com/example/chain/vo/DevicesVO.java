package com.example.chain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class DevicesVO implements Serializable {

    private static final long serialVersionUID = -7087423248172756901L;

    /**
     * 设备编号
     */
    private String deviceId;

    /**
     * 告警名称
     */
    private String alarmName;

    /**
     * 标签
     */
    private String tag;

    /**
     * 告警级别
     */
    private Integer level;

    /**
     * 是否告警
     */
    private Boolean alarm;

    /**
     * 是否在线
     */
    private Boolean online;

    /**
     * 开关状态
     */
    private Boolean status;
}
