package com.dlink.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dlink.db.model.SuperEntity;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务
 *
 * @author wenmo
 * @since 2021-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dlink_task")
public class Task extends SuperEntity{

    private static final long serialVersionUID = 5988972129893667154L;

    private String alias;

    private String type;

    private Integer checkPoint;

    private String savePointPath;

    private Integer parallelism;

    private boolean fragment;

    private Integer clusterId;

    private String note;

    @TableField(exist = false)
    private String statement;

    @TableField(exist = false)
    private String clusterName;

    public ExecutorSetting getLocalExecutorSetting(){
        return new ExecutorSetting(Executor.LOCAL,checkPoint,parallelism,fragment,savePointPath,alias);
    }

    public ExecutorSetting getRemoteExecutorSetting(){
        return new ExecutorSetting(Executor.REMOTE,checkPoint,parallelism,fragment,savePointPath,alias);
    }


}
