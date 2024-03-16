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

package org.dinky.job.builder;

import org.dinky.executor.Executor;
import org.dinky.job.ExecuteSqlException;
import org.dinky.job.JobBuilder;
import org.dinky.job.JobManager;
import org.dinky.job.JobParam;
import org.dinky.job.StatementParam;

import lombok.extern.slf4j.Slf4j;

/**
 * JobDDLBuilder
 *
 */
@Slf4j
public class JobDDLBuilder implements JobBuilder {

    private final JobParam jobParam;
    private final Executor executor;

    public JobDDLBuilder(JobParam jobParam, Executor executor) {
        this.jobParam = jobParam;
        this.executor = executor;
    }

    public static JobDDLBuilder build(JobManager jobManager) {
        return new JobDDLBuilder(jobManager.getJobParam(), jobManager.getExecutor());
    }

    @Override
    public void run() throws Exception {
        for (StatementParam item : jobParam.getDdl()) {
            try {
                executor.executeSql(item.getValue());
            } catch (Exception ex) {
                throw new ExecuteSqlException(item.getValue(), ex);
            }
        }
    }
}
