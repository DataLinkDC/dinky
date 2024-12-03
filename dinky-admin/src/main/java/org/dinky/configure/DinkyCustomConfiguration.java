package org.dinky.configure;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dinky")
@Slf4j
@Data
public class DinkyCustomConfiguration {


    private String dbType;
}
