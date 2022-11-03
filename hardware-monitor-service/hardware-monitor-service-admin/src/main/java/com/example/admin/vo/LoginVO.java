package com.example.admin.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = -4717431641998171940L;

    private String loginName;

    private String password;
}
