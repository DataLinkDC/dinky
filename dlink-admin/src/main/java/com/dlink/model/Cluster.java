package com.dlink.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dlink.db.model.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Cluster
 *
 * @author wenmo
 * @since 2021/5/28 13:53
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_cluster")
public class Cluster extends SuperEntity {

    private static final long serialVersionUID = 3104721227014487321L;

    @TableField(fill = FieldFill.INSERT)
    private String alias;

    private String type;

    private String hosts;

    private String jobManagerHost;

    private String version;

    private Integer status;

    private String note;
}
