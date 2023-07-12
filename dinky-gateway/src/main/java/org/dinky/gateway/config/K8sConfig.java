package org.dinky.gateway.config;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class K8sConfig {
    private Map<String, String> configuration = new HashMap<>();
    private Map<String, Object> dockerConfig = new HashMap<>();
    private String podTemplate;
    private String jmPodTemplate;
    private String tmPodTemplate;
}
