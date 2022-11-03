package com.example.chain.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
@TableName("tb_alarm")
public class Alarm implements Serializable {

    private static final long serialVersionUID = -7831447148841590870L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "quota_id")
    private Long quotaId;

    @TableField(value = "operator")
    private String operator;

    @TableField(value = "threshold")
    private String threshold;

    @TableField(value = "level")
    private Integer level;

    @TableField(value = "cycle")
    private Integer cycle;

    @TableField(value = "webhook")
    private String webhook;

    @TableField(value = "subject")
    private String subject;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
