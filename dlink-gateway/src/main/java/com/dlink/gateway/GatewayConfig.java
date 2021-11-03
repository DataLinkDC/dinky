package com.dlink.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * SubmitConfig
 *
 * @author wenmo
 * @since 2021/10/29
 **/
@Getter
@Setter
public class GatewayConfig {

    private GatewayType type;
    private String jobName;
    private String flinkConfigPath;
    private String userJarPath;
    private String[] userJarParas;
    private String userJarMainAppClass;
    private String savePoint;
    private String flinkLibs;
    private String yarnConfigPath;
    private List<ConfigPara> configParas;

    private static final ObjectMapper mapper = new ObjectMapper();

    public GatewayConfig() {
    }

    public static GatewayConfig build(JsonNode para){
        GatewayConfig config = new GatewayConfig();
        config.setType(GatewayType.get(para.get("type").asText()));
        if(para.has("jobName")) {
            config.setJobName(para.get("jobName").asText());
        }
        if(para.has("flinkConfigPath")) {
            config.setFlinkConfigPath(para.get("flinkConfigPath").asText());
        }
        if(para.has("userJarPath")) {
            config.setUserJarPath(para.get("userJarPath").asText());
        }
        if(para.has("userJarParas")) {
            config.setUserJarParas(para.get("userJarParas").asText().split("\\s+"));
        }
        if(para.has("userJarMainAppClass")) {
            config.setUserJarMainAppClass(para.get("userJarMainAppClass").asText());
        }
        if(para.has("savePoint")) {
            config.setSavePoint(para.get("savePoint").asText());
        }
        if(para.has("flinkLibs")) {
            config.setFlinkLibs(para.get("flinkLibs").asText());
        }
        if(para.has("yarnConfigPath")) {
            config.setYarnConfigPath(para.get("yarnConfigPath").asText());
        }
        if(para.has("configParas")) {
            try {
                List<ConfigPara> configParas = new ArrayList<>();
                JsonNode paras = mapper.readTree(para.get("configParas").asText());
                paras.forEach((JsonNode node)-> {
                    configParas.add(new ConfigPara(node.get("key").asText(),node.get("value").asText()));
                    }
                );
                config.setConfigParas(configParas);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    @Override
    public String toString() {
        return "GatewayConfig{" +
                "type=" + type +
                ", jobName='" + jobName + '\'' +
                ", flinkConfigPath='" + flinkConfigPath + '\'' +
                ", userJarPath='" + userJarPath + '\'' +
                ", userJarParas=" + Arrays.toString(userJarParas) +
                ", userJarMainAppClass='" + userJarMainAppClass + '\'' +
                ", savePoint='" + savePoint + '\'' +
                ", flinkLibs='" + flinkLibs + '\'' +
                ", yarnConfigPath='" + yarnConfigPath + '\'' +
                ", configParas='" + configParas.toString() + '\'' +
                '}';
    }
}
