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
