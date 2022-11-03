package com.example.chain.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 报表-设备监控实体类
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
@Accessors(chain = true)
public class DeviceMonitorDTO implements Serializable {

    private static final long serialVersionUID = -9081298476645505353L;

    private Long deviceCount;

    private Long alarmCount;
}
