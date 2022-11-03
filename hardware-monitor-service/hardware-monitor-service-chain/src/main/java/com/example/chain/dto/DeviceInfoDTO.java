package com.example.chain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 设备信息DTO
 * </p>
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceInfoDTO implements Serializable {

    private static final long serialVersionUID = -5449662632648954039L;

    private DevicesDTO devicesDTO;

    private List<QuotaDTO> deviceQuotaDTOList;
}
