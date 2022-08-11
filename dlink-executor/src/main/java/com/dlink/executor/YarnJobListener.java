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
package com.dlink.executor;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.core.execution.JobListener;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * 监听任务yarn-par启动和结束,并在结束是kill
 *
 * @author duhanmin
 * @since 2022/4/25 23:02
 */
public class YarnJobListener implements JobListener {
    protected static final Logger logger = LoggerFactory.getLogger(YarnJobListener.class);
    private StreamExecutionEnvironment environment;
    private String appId;

    public YarnJobListener(StreamExecutionEnvironment environment) {
        this.environment = environment;
    }

    /**
     * 监听flink应用提交成功事件
     */
    @Override
    public void onJobSubmitted(JobClient jobClient, Throwable throwable) {
        // applicationId 配置项
        ConfigOption<String> applicationId = ConfigOptions.key("yarn.application.id")
                .stringType()
                .noDefaultValue();
        try {
            // 获取flink应用的配置
            Field configurationField = StreamExecutionEnvironment.class.getDeclaredField("configuration");
            if (!configurationField.isAccessible()) {
                configurationField.setAccessible(true);
            }
            org.apache.flink.configuration.Configuration configuration = (org.apache.flink.configuration.Configuration)configurationField.get(environment);

            // 从配置中获取applicationId
            String appId = configuration.get(applicationId);
            setAppId(appId);
        } catch (Exception e) {
            logger.info("get yarn appId error" + e);
        }
    }

    @Override
    public void onJobExecuted(JobExecutionResult jobExecutionResult, Throwable throwable) {
        Process process = null;
        try {
            if (StrUtil.isNotBlank(getAppId())) {
                process = RuntimeUtil.exec("yarn application -kill " + getAppId());
                assert (process.waitFor() == 0);
            }
        } catch (Exception e) {
            logger.info("kill yarn appId error" + e);
        } finally {
            RuntimeUtil.destroy(process);
        }
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
