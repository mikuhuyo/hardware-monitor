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
@TableName("tb_quota")
public class Quota implements Serializable {

    private static final long serialVersionUID = -8622699592160108953L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "unit")
    private String unit;

    @TableField(value = "subject")
    private String subject;

    @TableField(value = "value_key")
    private String valueKey;

    @TableField(value = "sn_key")
    private String snKey;

    @TableField(value = "webhook")
    private String webhook;

    @TableField(value = "value_type")
    private String valueType;

    @TableField(value = "reference_value")
    private String referenceValue;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
