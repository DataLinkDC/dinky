package com.dlink.model;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dlink.db.model.SuperEntity;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
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

    private String alias;

    private String type;

    private Integer checkPoint;

    private String savePointPath;

    private Integer parallelism;

    private boolean fragment;

    private Integer clusterId;

    private String config;

    private String note;

    @TableField(exist = false)
    private String statement;

    @TableField(exist = false)
    private String clusterName;

    public ExecutorSetting getExecutorSetting(){
        HashMap configMap = new HashMap();
        if(config!=null&&!"".equals(clusterName)) {
            configMap = JSONUtil.toBean(config, HashMap.class);
        }
        return new ExecutorSetting(checkPoint,parallelism,fragment,savePointPath,alias,configMap);
    }


}
