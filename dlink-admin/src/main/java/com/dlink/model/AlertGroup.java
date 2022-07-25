package com.dlink.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dlink.db.model.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * AlertGroup
 *
 * @author wenmo
 * @since 2022/2/24 19:58
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_alert_group")
public class AlertGroup extends SuperEntity {

    private static final long serialVersionUID = 7027411164191682344L;

    private Integer tenantId;

    private String alertInstanceIds;

    private String note;

    @TableField(exist = false)
    private List<AlertInstance> instances;
}
