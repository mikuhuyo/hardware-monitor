package com.example.chain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class BoardVO implements Serializable {

    private static final long serialVersionUID = 7786935748188420868L;

    private Long id;

    private Long userId;

    private String boardName;

    private String quota;

    private String device;

    private String isSystem;

    private String isDisable;
}
