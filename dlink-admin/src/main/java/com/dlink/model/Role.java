package com.dlink.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * role
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_role")
public class Role implements Serializable {
    private static final long serialVersionUID = 6877230738922824958L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * code
     */
    private Integer tenantId;

    /**
     * role code
     */
    private String roleCode;

    /**
     * role name
     */
    private String roleName;

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
