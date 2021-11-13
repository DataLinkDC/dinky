package com.dlink.model;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dlink.db.model.SuperEntity;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.job.JobConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;

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

    @TableField(fill = FieldFill.INSERT)
    private String alias;

    private String type;

    private Integer checkPoint;

    private String savePointPath;

    private Integer parallelism;

    private boolean fragment;

    private Integer clusterId;

    private Integer clusterConfigurationId;

    private String config;

    private String note;

    @TableField(exist = false)
    private String statement;

    @TableField(exist = false)
    private String clusterName;

    public ExecutorSetting buildExecutorSetting(){
        HashMap configMap = new HashMap();
        if(config!=null&&!"".equals(clusterName)) {
            configMap = JSONUtil.toBean(config, HashMap.class);
        }
        return new ExecutorSetting(checkPoint,parallelism,fragment,savePointPath,alias,configMap);
    }

    public JobConfig buildSubmitConfig(){
        boolean useRemote = true;
        if(clusterId==null||clusterId==0){
            useRemote = false;
        }
        return new JobConfig(false,false,useRemote,clusterId,getId(),alias,fragment,checkPoint,parallelism,savePointPath);
    }

}
