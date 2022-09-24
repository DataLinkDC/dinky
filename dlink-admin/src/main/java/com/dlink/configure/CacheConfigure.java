package com.dlink.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * CacheCoonfigure
 *
 * @author ikiler
 * @since 2022/09/24 11:23
 */
@Configuration
public class CacheConfigure {

    /**
     * 配置Redis缓存注解的value序列化方式
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                //序列化为json
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json())
                )
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
    }


    //    /**
    //     * 配置RedisTemplate的序列化方式
    //     */
    //    @Bean
    //    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
    //        RedisTemplate redisTemplate = new RedisTemplate();
    //        redisTemplate.setConnectionFactory(factory);
    //        // 指定key的序列化方式：string
    //        redisTemplate.setKeySerializer(RedisSerializer.string());
    //        // 指定value的序列化方式：json
    //        redisTemplate.setValueSerializer(RedisSerializer.json());
    //        return redisTemplate;
    //    }

}
