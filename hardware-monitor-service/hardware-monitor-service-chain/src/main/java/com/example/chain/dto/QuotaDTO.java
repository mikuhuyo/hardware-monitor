package com.example.chain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 指标信息DTO
 * </p>
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuotaDTO implements Serializable {

    private static final long serialVersionUID = -6387513644132639728L;

    /**
     * 指标ID
     */
    private Long id;

    /**
     * 指标名称
     */
    private String name;

    /**
     * 单位
     */
    private String unit;

    /**
     * 报文主题
     */
    private String subject;

    /**
     * 指标值字段名称
     */
    private String valueKey;

    /**
     * 指标值数据类型
     */
    private String valueType;

    /**
     * 指标值(数值)
     */
    private String value;

    /**
     * 设备识别码字段(设备Id)
     */
    private String snKey;

    /**
     * web钩子地址
     */
    private String webhook;

    /**
     * 参考值
     */
    private String referenceValue;

    /**
     * 设备Id
     */
    private String deviceId;

    // 告警部分 //

    /**
     * 是否告警
     */
    private String alarm;

    /**
     * 告警名称
     */
    private String alarmName;

    /**
     * 告警级别
     */
    private Integer level;

    /**
     * 告警web钩子
     */
    private String alarmWebhook;

    /**
     * 沉默周期
     */
    private Integer cycle;

    /**
     * 设备标签
     */
    private String tag;
}
