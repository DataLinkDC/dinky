package com.dlink.gateway.config;

import com.dlink.assertion.Asserts;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FlinkConfig
 *
 * @author wenmo
 * @since 2021/11/3 21:56
 */
@Getter
@Setter
public class FlinkConfig {
    private String jobName;
    private String jobId;
    private ActionType action;
    private SavePointType savePointType;
    private String savePoint;
//    private List<ConfigPara> configParas;
    private Map<String, String> configuration;

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final String DEFAULT_SAVEPOINT_PREFIX = "hdfs:///flink/savepoints/";
    public FlinkConfig() {
    }

    public FlinkConfig(Map<String, String> configuration) {
        this.configuration = configuration;
    }

    public FlinkConfig(String jobName, String jobId, ActionType action, SavePointType savePointType, String savePoint, Map<String, String> configuration) {
        this.jobName = jobName;
        this.jobId = jobId;
        this.action = action;
        this.savePointType = savePointType;
        this.savePoint = savePoint;
        this.configuration = configuration;
    }

    public static FlinkConfig build(Map<String, String> paras){
        /*List<ConfigPara> configParasList = new ArrayList<>();
        for (Map.Entry<String, String> entry : paras.entrySet()) {
            configParasList.add(new ConfigPara(entry.getKey(),entry.getValue()));
        }*/
        return new FlinkConfig(paras);
    }

    public static FlinkConfig build(String jobName, String jobId, String actionStr, String savePointTypeStr, String savePoint, String configParasStr){
//        List<ConfigPara> configParasList = new ArrayList<>();
        Map<String, String> configMap = new HashMap<>();
        JsonNode paras = null;
        if(Asserts.isNotNullString(configParasStr)) {
            try {
                paras = mapper.readTree(configParasStr);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            paras.forEach((JsonNode node) -> {
                        configMap.put(node.get("key").asText(),node.get("value").asText());
//                        configParasList.add(new ConfigPara(node.get("key").asText(), node.get("value").asText()));
                    }
            );
        }
        return new FlinkConfig(jobName,jobId,ActionType.get(actionStr),SavePointType.get(savePointTypeStr),savePoint,configMap);
    }

    public static FlinkConfig build(String jobId, String actionStr, String savePointTypeStr, String savePoint){
        return new FlinkConfig(null,jobId,ActionType.get(actionStr),SavePointType.get(savePointTypeStr),savePoint,null);
    }
}

