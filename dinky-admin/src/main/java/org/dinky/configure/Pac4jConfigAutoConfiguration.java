package org.dinky.configure;

import org.dinky.configure.propertie.Pac4jConfigurationProperties;
import org.pac4j.config.client.PropertiesConfigFactory;
import org.pac4j.core.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * The Pac4jConfigAutoConfiguration class for Spring.
 *
 * @author yangzehan
 *
 */
@Configuration(value = "Pac4jConfigAutoConfiguration", proxyBeanMethods = false)
@EnableConfigurationProperties(org.dinky.configure.propertie.Pac4jConfigurationProperties.class)
public class Pac4jConfigAutoConfiguration {

    @Autowired
    private Pac4jConfigurationProperties pac4j;

    @Bean
    @ConditionalOnMissingBean
    public Config config() {
        final PropertiesConfigFactory factory =
            new PropertiesConfigFactory(pac4j.getCallbackUrl(), pac4j.getProperties());
        return factory.build();
    }
}
