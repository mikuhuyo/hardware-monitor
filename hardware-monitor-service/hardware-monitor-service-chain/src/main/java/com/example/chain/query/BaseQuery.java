package com.example.chain.query;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class BaseQuery implements Serializable {

    private static final long serialVersionUID = -1008096487395117184L;

    private Long id;

    private List<String> stringIds;

    private String startTime;

    private String endTime;

    private String type;

    private Long page;

    private Long pageSize;
}
