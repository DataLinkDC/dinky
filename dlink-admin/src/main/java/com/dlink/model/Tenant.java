package com.dlink.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * tenant
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_tenant")
public class Tenant implements Serializable {

    private static final long serialVersionUID = -7782313413034278131L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * code
     */
    private String tenantCode;

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
