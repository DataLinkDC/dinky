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

package org.dinky.app.util;

import org.dinky.context.CustomTableEnvironmentContext;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.utils.JsonUtils;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.core.execution.JobListener;
import org.apache.flink.runtime.client.JobCancellationException;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FlinkAppUtil {

    /**
     * Utility class for monitoring Flink tasks.
     * This method creates a Flink REST client and continuously checks the status of the task until it is completed.
     * If the task is completed, it sends a hook notification and stops monitoring.
     */
    public static void monitorFlinkTask(int taskId) {
        StreamExecutionEnvironment streamExecutionEnvironment =
                CustomTableEnvironmentContext.get().getStreamExecutionEnvironment();
        streamExecutionEnvironment.registerJobListener(new JobListener() {
            @Override
            public void onJobSubmitted(JobClient jobClient, Throwable throwable) {
                jobClient.getJobExecutionResult().thenAccept(jobExecutionResult -> finshedHook(jobClient, taskId));
                jobClient.getJobStatus().thenAccept(job -> {
                    if (job == JobStatus.FINISHED) {
                        finshedHook(jobClient, taskId);
                    }
                });
            }

            @Override
            public void onJobExecuted(JobExecutionResult jobExecutionResult, Throwable throwable) {
                if (throwable instanceof JobCancellationException) {
                    // todo cancel task
                } else {
                    // other exception
                }
            }
        });
    }

    private static void finshedHook(JobClient jobClient, int taskId) {
        try {
            sendHook(taskId, jobClient.getJobID().toHexString(), 0);
            log.info("hook finished.");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The sendHook method is used to send a Hook request.
     * This method sends an HTTP request to notify a specific address about the completion status of a task.
     * If the request is successful, the returned code in the result is 0; otherwise, an exception is thrown.
     * If sending the request fails, it will be retried up to 30 times with a 1-second interval between each retry.
     * If the retry limit is exceeded, an exception is thrown.
     */
    private static void sendHook(int taskId, String jobId, int reTryCount) throws InterruptedException {
        try {
            String dinkyAddr = SystemConfiguration.getInstances().getDinkyAddr().getValue();
            String url =
                    StrFormatter.format("{}/api/jobInstance/hookJobDone?taskId={}&jobId={}", dinkyAddr, taskId, jobId);
            String resultStr = HttpUtil.get(url);
            // TODO 这里应该使用Result实体类，但是Result.class不在comm里，迁移改动太大，暂时不搞
            String code = JsonUtils.parseObject(resultStr).get("code").toString();
            if (!"0".equals(code)) {
                throw new RuntimeException("Hook Job Done result failed: " + resultStr);
            }
        } catch (Exception e) {
            if (reTryCount < 30) {
                log.error("send hook failed,retry later taskId:{},jobId:{},{}", taskId, jobId, e.getMessage());
                Thread.sleep(1000);
                sendHook(taskId, jobId, reTryCount + 1);
            } else {
                throw new RuntimeException("Hook Job Done failed, The retry limit is exceeded: " + e.getMessage());
            }
        }
    }

    /**
     * Create a REST cluster client for Flink.
     *
     * @return
     * @throws Exception
     */
    private static RestClusterClient<StandaloneClusterId> createClient() throws Exception {
        ReadableConfig config = CustomTableEnvironmentContext.get()
                .getStreamExecutionEnvironment()
                .getConfiguration();
        Configuration configuration = new Configuration((Configuration) config);

        return new RestClusterClient<>(configuration, StandaloneClusterId.getInstance());
    }
}
