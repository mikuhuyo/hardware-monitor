package com.example.chain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
public class BoardDTO implements Serializable {

    private static final long serialVersionUID = -3032234483449186550L;

    private Long id;

    private Long userId;

    private String boardName;

    private String quota;

    private String device;

    private String isSystem;

    private String isDisable;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
