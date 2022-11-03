package com.example.chain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class QuotaVO implements Serializable {

    private static final long serialVersionUID = -1118377612058354421L;

    private Long id;

    private String name;

    private String unit;

    private String subject;

    private String valueKey;

    private String snKey;

    private String webhook;

    private String valueType;

    private String referenceValue;
}
