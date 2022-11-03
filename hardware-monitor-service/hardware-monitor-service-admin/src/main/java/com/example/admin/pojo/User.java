package com.example.admin.pojo;

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
@TableName(value = "tb_user")
public class User implements Serializable {

    private static final long serialVersionUID = 7910360494957237761L;

    @TableId(value = "id",type = IdType.ID_WORKER)
    private Long id;

    @TableField(value = "login_name")
    private String loginName;

    @TableField(value = "password")
    private String password;

    @TableField(value = "type")
    private String type;

    @TableField(value = "board")
    private String board;

    @TableField(value = "salt")
    private String salt;

    @TableField(value = "status")
    private String status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
