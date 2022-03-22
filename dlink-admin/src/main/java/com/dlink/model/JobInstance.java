package com.dlink.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * JobInstance
 *
 * @author wenmo
 * @since 2022/2/1 16:46
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_job_instance")
public class JobInstance implements Serializable {

    private static final long serialVersionUID = -3410230507904303730L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private Integer taskId;

    private Integer step;

    private Integer clusterId;

    private String jid;

    private String status;

    private Integer historyId;

    private String error;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private LocalDateTime finishTime;

    private Long duration;

    private Integer failedRestartCount;

    @TableField(exist = false)
    private String type;

    @TableField(exist = false)
    private String clusterAlias;

    @TableField(exist = false)
    private String jobManagerAddress;

}
