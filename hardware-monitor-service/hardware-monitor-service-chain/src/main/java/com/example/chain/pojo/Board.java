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
@TableName("tb_board")
public class Board implements Serializable {

    private static final long serialVersionUID = 6823556155875683405L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "board_name")
    private String boardName;

    @TableField(value = "quota")
    private String quota;

    @TableField(value = "device")
    private String device;

    @TableField(value = "is_system")
    private String isSystem;

    @TableField(value = "is_disable")
    private String isDisable;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
