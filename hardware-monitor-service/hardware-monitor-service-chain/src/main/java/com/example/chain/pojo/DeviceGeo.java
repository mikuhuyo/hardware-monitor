package com.example.chain.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class DeviceGeo implements Serializable {

    private static final long serialVersionUID = -4091269338563002044L;

    /**
     * 地理信息
     */
    private String location;

    /**
     * 设备id
     */
    private String deviceId;
}
