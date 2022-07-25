package com.dlink.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dlink.db.model.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AlertInstance
 *
 * @author wenmo
 * @since 2022/2/24 19:46
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_alert_instance")
public class AlertInstance extends SuperEntity {
    private static final long serialVersionUID = -3435401513220527001L;

    private Integer tenantId;

    private String type;

    private String params;
}
