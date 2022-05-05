package com.dlink.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_resource")
public class Resource {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * tenant id
     */
    private Integer tenantId;

    /**
     * resource code
     */
    private String resourceCode;

    /**
     * is enabled
     */
    private Boolean enabled;

    /**
     * note
     */
    private String note;

    /**
     * create time
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * update time
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
