package com.example.chain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceHeapDTO implements Serializable {

    private static final long serialVersionUID = -4973414434354106271L;

    private String deviceId;

    private String heapValue;

    private String quotaId;

    private String quotaName;
}
