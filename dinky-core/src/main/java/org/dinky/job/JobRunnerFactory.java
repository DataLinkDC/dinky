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

import org.dinky.data.job.JobStatementType;
import org.dinky.job.runner.JobDDLRunner;
import org.dinky.job.runner.JobPipelineRunner;
import org.dinky.job.runner.JobSetRunner;
import org.dinky.job.runner.JobSqlRunner;

public class JobRunnerFactory {

    private JobSetRunner jobSetRunner;
    private JobSqlRunner jobSqlRunner;
    private JobPipelineRunner jobPipelineRunner;
    private JobDDLRunner jobDDLRunner;

    public JobRunnerFactory(JobManager jobManager) {
        this.jobSetRunner = new JobSetRunner(jobManager);
        this.jobSqlRunner = new JobSqlRunner(jobManager);
        this.jobPipelineRunner = new JobPipelineRunner(jobManager);
        this.jobDDLRunner = new JobDDLRunner(jobManager);
    }

    public JobRunner getJobRunner(JobStatementType jobStatementType) {
        switch (jobStatementType) {
            case SET:
                return jobSetRunner;
            case SQL:
                return jobSqlRunner;
            case PIPELINE:
                return jobPipelineRunner;
            case DDL:
            default:
                return jobDDLRunner;
        }
    }

    public static JobRunnerFactory create(JobManager jobManager) {
        return new JobRunnerFactory(jobManager);
    }
}
