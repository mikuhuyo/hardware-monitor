package com.example.admin.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 7566317139854185030L;

    private String loginName;

    private String password;

    private String type;

    private String board;

    private String status;
}
