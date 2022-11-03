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

package com.dlink.service;

import com.dlink.executor.Executor;
import com.dlink.gateway.GatewayType;
import com.dlink.job.JobConfig;

/**
 * @author ZackYoung
 * @since 0.6.8
 */
public interface UDFService {

    /**
     *
     * @param statement sql语句
     * @param gatewayType flink 网关提交类型
     * @param missionId 任务id
     * @param executor flink执行器
     * @param config job配置
     */
    void initUDF(String statement, GatewayType gatewayType, Integer missionId,Executor executor, JobConfig config);
}
