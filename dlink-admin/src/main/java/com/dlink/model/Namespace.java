package com.dlink.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_namespace")
public class Namespace implements Serializable {
    private static final long serialVersionUID = -5960332046748903443L;
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
     * namespace code
     */
    private String namespaceCode;

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
