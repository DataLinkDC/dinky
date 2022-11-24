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

package com.dlink.gateway;

import com.dlink.assertion.Asserts;
import com.dlink.gateway.config.GatewayConfig;
import com.dlink.gateway.exception.GatewayException;
import com.dlink.gateway.result.GatewayResult;
import com.dlink.gateway.result.SavePointResult;
import com.dlink.gateway.result.TestResult;
import com.dlink.model.JobStatus;

import org.apache.flink.runtime.jobgraph.JobGraph;

import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * Submiter
 *
 * @author wenmo
 * @since 2021/10/29
 **/
public interface Gateway {

    static Optional<Gateway> get(GatewayConfig config) {
        Asserts.checkNotNull(config, "配置不能为空");
        Asserts.checkNotNull(config.getType(), "配置类型不能为空");
        ServiceLoader<Gateway> loader = ServiceLoader.load(Gateway.class);
        Iterator<Gateway> iterator = loader.iterator();
        while (iterator.hasNext()) {
            Gateway gateway = iterator.next();
            if (gateway.canHandle(config.getType())) {
                gateway.setGatewayConfig(config);
                return Optional.of(gateway);
            }
        }
        return Optional.empty();
    }

    static Gateway build(GatewayConfig config) {
        Optional<Gateway> optionalGateway = Gateway.get(config);
        if (!optionalGateway.isPresent()) {
            throw new GatewayException("不支持 Flink Gateway 类型【" + config.getType().getLongValue() + "】,请添加扩展包");
        }
        return optionalGateway.get();
    }

    boolean canHandle(GatewayType type);

    GatewayType getType();

    void setGatewayConfig(GatewayConfig config);

    GatewayResult submitJobGraph(JobGraph jobGraph);

    GatewayResult submitJar();

    SavePointResult savepointCluster();

    SavePointResult savepointCluster(String savePoint);

    SavePointResult savepointJob();

    SavePointResult savepointJob(String savePoint);

    TestResult test();

    JobStatus getJobStatusById(String id);
}
