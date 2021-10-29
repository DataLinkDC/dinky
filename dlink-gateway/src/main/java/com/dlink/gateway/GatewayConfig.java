package com.dlink.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

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
    private String configDir;
    private String userJarPath;
    private String[] userJarParas;
    private String userJarMainAppClass;
    private String savePoint;

    public GatewayConfig() {
    }

    public GatewayConfig(GatewayType type, String jobName, String configDir, String userJarPath, String[] userJarParas, String userJarMainAppClass, String savePoint) {
        this.type = type;
        this.jobName = jobName;
        this.configDir = configDir;
        this.userJarPath = userJarPath;
        this.userJarParas = userJarParas;
        this.userJarMainAppClass = userJarMainAppClass;
        this.savePoint = savePoint;
    }

    public static GatewayConfig build(JsonNode para){
        GatewayConfig config = new GatewayConfig();
        config.setType(GatewayType.get(para.get("type").asText()));
        if(para.has("jobName")) {
            config.setJobName(para.get("jobName").asText());
        }
        if(para.has("configDir")) {
            config.setConfigDir(para.get("configDir").asText());
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
        return config;
    }

    @Override
    public String toString() {
        return "GatewayConfig{" +
                "type=" + type +
                ", jobName='" + jobName + '\'' +
                ", configDir='" + configDir + '\'' +
                ", userJarPath='" + userJarPath + '\'' +
                ", userJarParas=" + Arrays.toString(userJarParas) +
                ", userJarMainAppClass='" + userJarMainAppClass + '\'' +
                ", savePoint='" + savePoint + '\'' +
                '}';
    }
}
