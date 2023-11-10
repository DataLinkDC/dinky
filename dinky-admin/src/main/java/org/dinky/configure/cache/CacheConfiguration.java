package org.dinky.configure.cache;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {
    @Bean
    public CacheManager cacheManager() {
        return new PaimonCacheManager();
    }
}
