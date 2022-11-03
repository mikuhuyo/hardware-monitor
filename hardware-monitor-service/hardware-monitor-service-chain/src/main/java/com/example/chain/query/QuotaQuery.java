package com.example.chain.query;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class QuotaQuery implements Serializable {

    private static final long serialVersionUID = 443266758153168453L;

    private Long page;

    private Long pageSize;

    private String quotaName;
}
