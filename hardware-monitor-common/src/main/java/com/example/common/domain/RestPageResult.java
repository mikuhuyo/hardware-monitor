package com.example.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页响应体
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestPageResult<T> implements Serializable {

    private static final long serialVersionUID = 8894547939662899733L;

    /**
     * 总条数
     */
    private Long counts;
    /**
     * 每页条数
     */
    private Long pageSize;
    /**
     * 总页数
     */
    private Long pages;
    /**
     * 当前页数
     */
    private Long page;

    private T items;

    public RestPageResult() {
    }

    public RestPageResult(Long counts, Long pageSize, Long pages, Long page, T data) {
        this.counts = counts;
        this.pageSize = pageSize;
        this.pages = pages;
        this.page = page;
        this.items = data;
    }
}
