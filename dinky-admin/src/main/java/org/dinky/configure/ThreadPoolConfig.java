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

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import cn.hutool.core.thread.BlockPolicy;

@Configuration
public class ThreadPoolConfig {
    /**
     * Use sse to return to monitor the latest data(使用sse返回监控最新数据)
     *
     * @return
     */
    @Bean
    public Executor scheduleRefreshMonitorDataExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // 核心线程数
        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
        // 最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(200);
        // 配置队列大小
        threadPoolTaskExecutor.setQueueCapacity(1000);
        // 配置线程池前缀
        threadPoolTaskExecutor.setThreadNamePrefix("SCH-refresh-mt-");
        // 阻塞策略
        threadPoolTaskExecutor.setRejectedExecutionHandler(new BlockPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
