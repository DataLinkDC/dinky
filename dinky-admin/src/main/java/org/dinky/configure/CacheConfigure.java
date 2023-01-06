/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
