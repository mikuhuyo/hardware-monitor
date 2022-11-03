package com.example.chain.query;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class DevicesQuery implements Serializable {

    private static final long serialVersionUID = 1504265723139457473L;

    private Long page;

    private Long pageSize;

    private String deviceId;

    private String tag;

    private Integer state;
}
