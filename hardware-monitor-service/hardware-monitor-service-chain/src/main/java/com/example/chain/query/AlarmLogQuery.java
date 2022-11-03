package com.example.chain.query;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class AlarmLogQuery implements Serializable {

    private static final long serialVersionUID = -5529631204092689107L;

    private Long page;

    private Long pageSize;

    private String start;

    private String end;

    private String alarmName;

    private String deviceId;

    /**
     * 0正常日志 1一般警告日志 2严重警告日志
     */
    private Integer alarm;
}
