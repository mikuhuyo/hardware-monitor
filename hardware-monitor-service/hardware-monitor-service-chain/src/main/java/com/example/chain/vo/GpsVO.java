package com.example.chain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class GpsVO implements Serializable {

    private static final long serialVersionUID = 7141753441678923532L;

    private Long id;

    private String subject;

    private String snKey;

    private String singleField;

    private String valueKey;

    private String separation;

    private String longitude;

    private String latitude;
}
