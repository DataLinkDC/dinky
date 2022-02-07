package com.dlink.model;

import com.baomidou.mybatisplus.annotation.TableName;
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

    private Integer id;
    private String name;
    private Integer taskId;
    private Integer clusterId;
    private String jid;
    private Integer status;
    private Integer historyId;
    private String error;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime finishTime;
    private Integer failed_restart_count;

}
