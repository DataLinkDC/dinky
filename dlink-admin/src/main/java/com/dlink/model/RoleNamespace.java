package com.dlink.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * role namespace relation
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_role_namespace")
public class RoleNamespace implements Serializable {
    private static final long serialVersionUID = 304808291890721691L;
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
     * namespace id
     */
    private Integer namespaceId;

}
