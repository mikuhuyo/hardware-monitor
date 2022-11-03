package com.example.chain.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Data
@TableName("tb_gps")
public class Gps implements Serializable {

    private static final long serialVersionUID = 2042406758348642439L;

    @TableId(value = "id")
    private Long id;

    @TableField(value = "subject")
    private String subject;

    @TableField(value = "sn_key")
    private String snKey;

    @TableField(value = "single_field")
    private String singleField;

    @TableField(value = "value_key")
    private String valueKey;

    @TableField(value = "separation")
    private String separation;

    @TableField(value = "longitude")
    private String longitude;

    @TableField(value = "latitude")
    private String latitude;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
