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

package com.zdpx.coder.configure;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.google.common.collect.ImmutableMap;

/** */
// @Configuration
public class ProxyServletConfiguration {
    /** 读取配置文件中路由设置 */
    @Value("${proxy.grafana.servlet_url}")
    private String servlet_url;

    /** 读取配置中代理目标地址 */
    @Value("${proxy.grafana.target_url}")
    private String target_url;

    @Bean
    public ServletRegistrationBean proxyServletRegistration() {
        ServletRegistrationBean registrationBean =
                new ServletRegistrationBean(new GrafanaProxyServlet(), servlet_url);
        // 设置网址以及参数
        Map<String, String> params = ImmutableMap.of("targetUri", target_url, "log", "true");
        registrationBean.setInitParameters(params);
        return registrationBean;
    }
}
