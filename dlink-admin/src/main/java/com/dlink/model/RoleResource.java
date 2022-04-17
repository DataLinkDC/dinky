package com.dlink.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * role resource
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_role_resource")
public class RoleResource implements Serializable {
    private static final long serialVersionUID = -8653861289552550894L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * role id
     */
    private Integer roleId;

    /**
     * resource id
     */
    private Integer resourceId;
}
