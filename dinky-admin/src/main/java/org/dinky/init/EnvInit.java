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

package org.dinky.init;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;

@Order(-1)
@Component
@Slf4j
public class EnvInit implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        String ipAddress = SystemUtil.getHostInfo().getAddress();
        System.setProperty("ipAddr", ipAddress);
        ApplicationContext application = SpringUtil.getApplicationContext();
        Environment env = application.getEnvironment();
        String port = env.getProperty("server.port");
        System.setProperty("dinkyAddr", "http://" + ipAddress + ":" + port);
        log.info(
                "\n----------------------------------------------------------\n\t"
                        + "Application 'Dinky' is running! Access URLs:\n\t"
                        + "Local: \t\thttp://localhost:{}\n\t"
                        + "External: \thttp://{}:{}\n\t"
                        + "Doc: \thttp://{}:{}/doc.html\n\t"
                        + "Druid Monitor: \thttp://{}:{}/druid/index.html\n\t"
                        + "Actuator: \thttp://{}:{}/actuator\n"
                        + "----------------------------------------------------------",
                port,
                ipAddress,
                port,
                ipAddress,
                port,
                ipAddress,
                port,
                ipAddress,
                port);
    }
}
