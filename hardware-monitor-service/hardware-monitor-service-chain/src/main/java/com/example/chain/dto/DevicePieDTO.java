package com.example.chain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 报表-设备分布实体类
 * <code>@Accessors(chain = true)开启链式编程</code>
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DevicePieDTO implements Serializable {

    private static final long serialVersionUID = 2194033771833803248L;

    private String name;

    private Long value;
}
