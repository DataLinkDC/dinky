package com.dlink.executor;

import com.dlink.assertion.Asserts;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExecutorSetting
 *
 * @author wenmo
 * @since 2021/5/25 13:43
 **/
@Setter
@Getter
public class ExecutorSetting {
    private Integer checkpoint;
    private Integer parallelism;
    private boolean useSqlFragment;
    private boolean useStatementSet;
    private String savePointPath;
    private String jobName;
    private Map<String,String> config;
    public static final ExecutorSetting DEFAULT = new ExecutorSetting(0,1,true);
    private static final ObjectMapper mapper = new ObjectMapper();

    public ExecutorSetting(boolean useSqlFragment) {
        this.useSqlFragment = useSqlFragment;
    }

    public ExecutorSetting(Integer checkpoint) {
        this.checkpoint = checkpoint;
    }

    public ExecutorSetting(Integer checkpoint, boolean useSqlFragment) {
        this.checkpoint = checkpoint;
        this.useSqlFragment = useSqlFragment;
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment) {
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment, String savePointPath, String jobName) {
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
        this.savePointPath = savePointPath;
        this.jobName = jobName;
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment, String savePointPath) {
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
        this.savePointPath = savePointPath;
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment, String savePointPath, String jobName, Map<String, String> config) {
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
        this.savePointPath = savePointPath;
        this.jobName = jobName;
        this.config = config;
    }

    public ExecutorSetting(Integer checkpoint, Integer parallelism, boolean useSqlFragment,boolean useStatementSet, String savePointPath, String jobName, Map<String, String> config) {
        this.checkpoint = checkpoint;
        this.parallelism = parallelism;
        this.useSqlFragment = useSqlFragment;
        this.useStatementSet = useStatementSet;
        this.savePointPath = savePointPath;
        this.jobName = jobName;
        this.config = config;
    }

    public static ExecutorSetting build(Integer checkpoint, Integer parallelism, boolean useSqlFragment,boolean useStatementSet, String savePointPath, String jobName, String configJson){
        JsonNode paras = null;
        Map<String,String> config = new HashMap<>();
        if(Asserts.isNotNullString(configJson)) {
            try {
                paras = mapper.readTree(configJson);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            config = mapper.convertValue(paras, new TypeReference<Map<String, String>>(){});
        }
        return new ExecutorSetting(checkpoint,parallelism,useSqlFragment,useStatementSet,savePointPath,jobName,config);
    }

    public static ExecutorSetting build(Map<String,String> settingMap){
        Integer checkpoint = null;
        Integer parallelism = null;
        if(settingMap.containsKey("checkpoint")&&!"".equals(settingMap.get("checkpoint"))){
            checkpoint = Integer.valueOf(settingMap.get("checkpoint"));
        }
        if(settingMap.containsKey("parallelism")&&!"".equals(settingMap.get("parallelism"))){
            parallelism = Integer.valueOf(settingMap.get("parallelism"));
        }
        return build(checkpoint,
                parallelism,
                "1".equals(settingMap.get("useSqlFragment")),
                "1".equals(settingMap.get("useStatementSet")),
                settingMap.get("savePointPath"),
                settingMap.get("jobName"),
                settingMap.get("config"));
    }
}
