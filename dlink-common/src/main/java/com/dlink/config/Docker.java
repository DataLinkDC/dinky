package com.dlink.config;

import com.dlink.assertion.Asserts;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author ZackYoung
 * @since 0.7.1
 */
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
public class Docker {
    private String instance;
    private String registryUrl;
    private String registryUsername;
    private String registryPassword;
    private String imageNamespace;
    private String imageStorehouse;
    private String imageDinkyVersion;

    public static Docker build(Map configMap) {
        if (Asserts.isNullMap(configMap)) {
            return null;
        }
        String instance1 = configMap.getOrDefault("docker.instance", "").toString();
        if ("".equals(instance1)) {
            return null;
        }
        return Docker.builder()
            .instance(instance1)
            .registryUrl(configMap.getOrDefault("docker.registry.url", "").toString())
            .registryUsername(configMap.getOrDefault("docker.registry.username", "").toString())
            .registryPassword(configMap.getOrDefault("docker.registry.password", "").toString())
            .imageNamespace(configMap.getOrDefault("docker.image.namespace", "").toString())
            .imageStorehouse(configMap.getOrDefault("docker.image.storehouse", "").toString())
            .imageDinkyVersion(configMap.getOrDefault("docker.image.dinkyVersion", "").toString())
            .build();
    }
}
