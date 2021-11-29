package com.dlink.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dlink.assertion.Asserts;
import com.dlink.db.model.SuperEntity;
import com.dlink.job.JobConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Integer savePointStrategy;

    private String savePointPath;

    private Integer parallelism;

    private boolean fragment;

    private boolean statementSet;

    private Integer clusterId;

    private Integer clusterConfigurationId;

    private Integer jarId;

    private String configJson;

    private String note;

    @TableField(exist = false)
    private String statement;

    @TableField(exist = false)
    private String clusterName;

    @TableField(exist = false)
    private List<Savepoints> savepoints;

    @TableField(exist = false)
    private List<Map<String,String>> config = new ArrayList<>();


    public List<Map<String,String>> parseConfig(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if(Asserts.isNotNullString(configJson)) {
                config = objectMapper.readValue(configJson, ArrayList.class);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return config;
    }
    /*public ExecutorSetting buildExecutorSetting(){
        HashMap configMap = new HashMap();
        if(config!=null&&!"".equals(clusterName)) {
            configMap = JSONUtil.toBean(config, HashMap.class);
        }
        return new ExecutorSetting(checkPoint,parallelism,fragment,savePointPath,alias,configMap);
    }*/

    public JobConfig buildSubmitConfig(){
        boolean useRemote = true;
        if(clusterId==null||clusterId==0){
            useRemote = false;
        }
        Map<String,String> map = new HashMap<>();
        for(Map<String,String> item : config){
            map.put(item.get("key"),item.get("value"));
        }
        return new JobConfig(type,false,false,useRemote,clusterId,clusterConfigurationId,jarId,getId(),alias,fragment,statementSet,checkPoint,parallelism,savePointStrategy,savePointPath,map);
    }

}
