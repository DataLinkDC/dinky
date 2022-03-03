package com.dlink.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AlertHistory
 *
 * @author wenmo
 * @since 2022/2/24 20:12
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_alert_history")
public class AlertHistory implements Serializable {

    private static final long serialVersionUID = -7904869940473678282L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String alertGroupId;

    private String jobInstanceId;

    private String title;

    private String content;

    private Integer status;

    private String log;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
