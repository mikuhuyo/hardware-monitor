package com.example.chain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DevicesDTO implements Serializable {

    private static final long serialVersionUID = -5785884079354833250L;

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
