package com.dlink.gateway.config;

import lombok.Getter;
import lombok.Setter;

/**
 * AppConfig
 *
 * @author wenmo
 * @since 2021/11/3 21:55
 */
@Setter
@Getter
public class AppConfig {
    private String userJarPath;
    private String[] userJarParas;
    private String userJarMainAppClass;

    public AppConfig() {
    }
}
