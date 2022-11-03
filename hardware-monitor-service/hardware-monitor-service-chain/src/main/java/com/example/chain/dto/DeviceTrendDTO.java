package com.example.chain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 报表-设备趋势实体类
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceTrendDTO  implements Serializable {

    private static final long serialVersionUID = -1868771922171806517L;

    /**
     * y轴(数据)
     */
    private String series;

    /**
     * x轴(标题)1小时内的分钟 2每日的小时 3每周的天
     */
    private String xdata;
}
