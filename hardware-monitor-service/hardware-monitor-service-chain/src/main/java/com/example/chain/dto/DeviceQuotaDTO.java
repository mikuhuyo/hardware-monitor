package com.example.chain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceQuotaDTO implements Serializable {

    private static final long serialVersionUID = -6387513644132639728L;

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

    private List<QuotaDTO> quotaList;
}
