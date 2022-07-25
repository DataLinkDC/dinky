package com.dlink.model;

import com.baomidou.mybatisplus.annotation.*;
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

    private Integer tenantId;

    private Integer alertGroupId;

    private Integer jobInstanceId;

    private String title;

    private String content;

    private Integer status;

    private String log;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
