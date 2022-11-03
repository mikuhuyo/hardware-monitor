package com.example.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 响应体
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResponse<T> {

    private Integer status;
    private String msg;
    private T result;

    public RestResponse() {
        this(0, "success");
    }

    public RestResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public static <T> RestResponse<T> success() {
        return new RestResponse<T>();
    }

    public static <T> RestResponse<T> success(T result) {
        RestResponse<T> response = new RestResponse<T>();
        response.setResult(result);
        return response;
    }

    public static <T> RestResponse<T> fail(String msg) {
        RestResponse<T> response = new RestResponse<T>();
        response.setStatus(400);
        response.setMsg(msg);
        return response;
    }

    public static <T> RestResponse<T> fail(Integer status, String msg) {
        RestResponse<T> response = new RestResponse<T>();
        response.setStatus(status);
        response.setMsg(msg);
        return response;
    }

}


