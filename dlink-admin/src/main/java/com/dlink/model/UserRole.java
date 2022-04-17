package com.dlink.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_role")
public class UserRole implements Serializable {
    private static final long serialVersionUID = 7695611379848688269L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * user id
     */
    private Integer userId;

    /**
     * role id
     */
    private Integer roleId;

    /**
     * role id
     */
    private Integer enabled;
}
