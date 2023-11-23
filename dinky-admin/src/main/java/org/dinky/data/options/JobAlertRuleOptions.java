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

package org.dinky.data.options;

public class JobAlertRuleOptions {
    public static final String OPTIONS_JOB_ALERT_RULE = "jobAlertRule";

    /**
     * Job Alert Data Filed record
     */
    public static final String FIELD_NAME_TIME = "alertTime";

    public static final String FIELD_NAME_START_TIME = "jobStartTime";
    public static final String FIELD_NAME_END_TIME = "jobEndTime";
    public static final String FIELD_NAME_DURATION = "duration";
    public static final String FIELD_NAME_JOB_NAME = "jobName";
    public static final String FIELD_NAME_JOB_ID = "jobId";
    public static final String FIELD_NAME_JOB_STATUS = "jobStatus";
    public static final String FIELD_TASK_ID = "taskId";
    public static final String FIELD_JOB_INSTANCE_ID = "jobInstanceId";
    public static final String FIELD_JOB_TASK_URL = "taskUrl";
    public static final String FIELD_JOB_BATCH_MODEL = "batchModel";
    public static final String FIELD_NAME_CLUSTER_NAME = "clusterName";
    public static final String FIELD_NAME_CLUSTER_TYPE = "clusterType";
    public static final String FIELD_NAME_CLUSTER_HOSTS = "clusterHost";
    public static final String FIELD_NAME_EXCEPTIONS_MSG = "errorMsg";
    public static final String FIELD_NAME_CHECKPOINT_COST_TIME = "checkpointCostTime";
    public static final String FIELD_NAME_CHECKPOINT_FAILED_COUNT = "checkpointFailedCount";
    public static final String FIELD_NAME_CHECKPOINT_COMPLETE_COUNT = "checkpointCompleteCount";
    public static final String FIELD_NAME_CHECKPOINT_FAILED = "isCheckpointFailed";
    public static final String FIELD_NAME_IS_EXCEPTION = "isException";
}
