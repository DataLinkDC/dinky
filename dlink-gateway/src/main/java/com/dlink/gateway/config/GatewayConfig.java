package com.dlink.gateway.config;

import com.dlink.gateway.GatewayType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * SubmitConfig
 *
 * @author wenmo
 * @since 2021/10/29
 **/
@Getter
@Setter
public class GatewayConfig {

    private Integer taskId;
    private GatewayType type;
    private ClusterConfig clusterConfig;
    private FlinkConfig flinkConfig;
    private AppConfig appConfig;

    private static final ObjectMapper mapper = new ObjectMapper();

    public GatewayConfig() {
        clusterConfig = new ClusterConfig();
        flinkConfig = new FlinkConfig();
        appConfig = new AppConfig();
    }

    public static GatewayConfig build(JsonNode para){
        GatewayConfig config = new GatewayConfig();
        if(para.has("taskId")) {
            config.setTaskId(para.get("taskId").asInt());
        }
        config.setType(GatewayType.get(para.get("type").asText()));
        if(para.has("flinkConfigPath")) {
            config.getClusterConfig().setFlinkConfigPath(para.get("flinkConfigPath").asText());
        }
        if(para.has("flinkLibPath")) {
            config.getClusterConfig().setFlinkLibPath(para.get("flinkLibPath").asText());
        }
        if(para.has("yarnConfigPath")) {
            config.getClusterConfig().setYarnConfigPath(para.get("yarnConfigPath").asText());
        }
        if(para.has("jobName")) {
            config.getFlinkConfig().setJobName(para.get("jobName").asText());
        }
        if(para.has("userJarPath")) {
            config.getAppConfig().setUserJarPath(para.get("userJarPath").asText());
        }
        if(para.has("userJarParas")) {
            config.getAppConfig().setUserJarParas(para.get("userJarParas").asText().split("\\s+"));
        }
        if(para.has("userJarMainAppClass")) {
            config.getAppConfig().setUserJarMainAppClass(para.get("userJarMainAppClass").asText());
        }
        if(para.has("savePoint")) {
            config.getFlinkConfig().setSavePoint(para.get("savePoint").asText());
        }
        if(para.has("configParas")) {
            try {
                List<ConfigPara> configParas = new ArrayList<>();
                JsonNode paras = mapper.readTree(para.get("configParas").asText());
                paras.forEach((JsonNode node)-> {
                    configParas.add(new ConfigPara(node.get("key").asText(),node.get("value").asText()));
                    }
                );
                config.getFlinkConfig().setConfigParas(configParas);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return config;
    }

}
