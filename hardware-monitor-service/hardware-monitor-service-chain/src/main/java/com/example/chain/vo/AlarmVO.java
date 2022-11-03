package com.example.chain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class AlarmVO implements Serializable {

    private static final long serialVersionUID = 126653584931390426L;

    private Long id;

    /**
     * 告警名称
     */
    private String name;

    /**
     * 指标id
     */
    private Long quotaId;

    /**
     * 指标名称
     */
    private String quotaName;

    /**
     * 运算符 > < =
     */
    private String operator;

    /**
     * 预警值
     */
    private String threshold;

    /**
     * 级别 0-正常 1-一般 2-严重
     */
    private Integer level;

    /**
     * 沉默周期-分钟
     */
    private Integer cycle;

    /**
     * webhook-业务服务地址
     */
    private String webhook;

    /**
     * 冗余-配合前端(傻狗前端)
     */
    private Integer time;
}
