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

import com.dlink.gateway.config.GatewayConfig;
import com.dlink.model.JobStatus;

import org.apache.flink.configuration.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractGateway
 *
 * @author wenmo
 * @since 2021/10/29
 **/
public abstract class AbstractGateway implements Gateway {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractGateway.class);
    protected GatewayConfig config;
    protected Configuration configuration;

    public AbstractGateway() {
    }

    public AbstractGateway(GatewayConfig config) {
        this.config = config;
    }

    @Override
    public boolean canHandle(GatewayType type) {
        return type == getType();
    }

    @Override
    public void setGatewayConfig(GatewayConfig config) {
        this.config = config;
    }

    protected abstract void init();

    @Override
    public JobStatus getJobStatusById(String id) {
        return JobStatus.UNKNOWN;
    }
}
