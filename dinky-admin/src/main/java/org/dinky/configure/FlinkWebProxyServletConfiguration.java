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

import org.dinky.utils.FlinkWebURITemplateProxyServlet;

import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class FlinkWebProxyServletConfiguration implements EnvironmentAware {
    private static final String TARGET_URL = "http://{_authority}/#/job/running/{_jid}/overview";

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean servletRegistrationBean =
                new ServletRegistrationBean(new FlinkWebURITemplateProxyServlet());
        servletRegistrationBean.addUrlMappings("/api/flink_web/proxy/*", "/api/flink_web/*");
        servletRegistrationBean.addInitParameter(ProxyServlet.P_TARGET_URI, TARGET_URL);
        servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG, "false");
        return servletRegistrationBean;
    }

    @Override
    public void setEnvironment(Environment environment) {}
}
