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

package org.dinky.data.constant;

/**
 * FlinkRestAPIConstant
 *
 * @since 2021/6/24 14:04
 */
public final class FlinkRestAPIConstant {

    public static final String OVERVIEW = "overview";

    public static final String FLINK_CONFIG = "config";

    public static final String CONFIG = "/config";

    public static final String JOBS = "jobs/";

    public static final String JOBSLIST = "jobs/overview";

    public static final String CANCEL = "/yarn-cancel";

    public static final String CHECKPOINTS = "/checkpoints";

    public static final String CHECKPOINTS_CONFIG = "/checkpoints/config";

    public static final String SAVEPOINTS = "/savepoints";

    public static final String STOP = "/stop";

    public static final String EXCEPTIONS = "/exceptions?maxExceptions=10";

    public static final String JOB_MANAGER = "/jobmanager";

    public static final String TASK_MANAGER = "/taskmanagers/";

    public static final String METRICS = "/metrics";

    public static final String LOG = "/log";

    public static final String LOGS = "/logs/";

    public static final String STDOUT = "/stdout";

    public static final String THREAD_DUMP = "/thread-dump";

    public static final String GET = "?get=";
}
