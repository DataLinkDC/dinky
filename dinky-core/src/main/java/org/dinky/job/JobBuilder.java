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

package org.dinky.job;

import org.dinky.data.enums.GatewayType;
import org.dinky.executor.Executor;

public abstract class JobBuilder {

    protected JobManager jobManager;
    protected JobConfig config;
    protected JobParam jobParam;
    protected GatewayType runMode;
    protected Executor executor;
    protected boolean useStatementSet;
    protected boolean useGateway;
    protected Job job;

    public JobBuilder(JobManager jobManager) {
        this.jobManager = jobManager;
        this.config = jobManager.getConfig();
        this.jobParam = jobManager.getJobParam();
        this.runMode = jobManager.getRunMode();
        this.executor = jobManager.getExecutor();
        this.useStatementSet = jobManager.isUseStatementSet();
        this.useGateway = jobManager.isUseGateway();
        this.job = jobManager.getJob();
    }

    public abstract void run() throws Exception;
}
