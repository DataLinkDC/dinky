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

package org.dinky;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import cn.hutool.system.SystemUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Dinky 启动器
 *
 * @since 2021/5/28
 */
@EnableTransactionManagement
@SpringBootApplication(exclude = FreeMarkerAutoConfiguration.class)
@EnableCaching
@Slf4j
public class Dinky {
    @SneakyThrows
    public static void main(String[] args) {
        String ipAddress = SystemUtil.getHostInfo().getAddress();
        System.setProperty("ipAddr", ipAddress);
        SpringApplication app = new SpringApplication(Dinky.class);
        ConfigurableApplicationContext application = app.run(args);
        Environment env = application.getEnvironment();
        String port = env.getProperty("server.port");
        System.setProperty("dinkyAddr", ipAddress + ":" + port);
        log.info(
                "\n----------------------------------------------------------\n\t"
                        + "Application 'Dinky' is running! Access URLs:\n\t"
                        + "Local: \t\thttp://localhost:{}\n\t"
                        + "External: \thttp://{}:{}\n\t"
                        + "Doc: \thttp://{}:{}/doc.html\n"
                        + "----------------------------------------------------------",
                port,
                ipAddress,
                port,
                ipAddress,
                port);
    }
}
