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
public class DeviceGeoDetails implements Serializable {

    private static final long serialVersionUID = 7013925172323976837L;

    private String deviceId;

    private Integer level;

    private String latitude;

    private String longitude;

    private Boolean alarm;

    private Boolean online;

    private List<QuotaDTO> quotaList;
}
