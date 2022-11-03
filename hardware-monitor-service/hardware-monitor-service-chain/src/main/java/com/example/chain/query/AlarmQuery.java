package com.example.chain.query;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class AlarmQuery implements Serializable {

    private static final long serialVersionUID = 5172487689208831486L;

    private Long page;

    private Long pageSize;

    /**
     * 报警名称
     */
    private String name;

    /**
     * 指标id
     */
    private Long quotaId;
}
