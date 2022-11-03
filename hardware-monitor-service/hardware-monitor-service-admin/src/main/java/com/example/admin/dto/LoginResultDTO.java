package com.example.admin.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class LoginResultDTO implements Serializable {

    private static final long serialVersionUID = 832098862168595179L;

    /**
     * 登录结果
     */
    private Boolean loginSuccess;

    /**
     * 管理员Id
     */
    private Long userId;

    /**
     * jwt token
     */
    private String token;
}
