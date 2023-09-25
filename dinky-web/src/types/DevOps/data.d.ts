/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import { BaseBeanColumns } from '@/types/Public/data';

/**
 * about flink job
 */
declare namespace Jobs {
  export type JobInstance = BaseBeanColumns & {
    taskId: number;
    step: number;
    clusterId: number;
    clusterName: string;
    type: string;
    jobManagerAddress: string;
    jid: string;
    status: string;
    historyId: number;
    error: string;
    failedRestartCount: number;
    duration: number;
    finishTime: Date;
  };

  export type JobConfig = {
    config: {};
    savePointStrategy: string;
    savePointPath: string;
    useSqlFragment: string;
    isJarTask: string;
    useBatchModel: string;
  };

  export type History = {
    id: number;
    tenantId: number;
    clusterId: number;
    clusterConfigurationId: number;
    session: string;
    jobId: string;
    jobName: string;
    jobManagerAddress: string;
    status: number;
    statement: string;
    type: string;
    error: string;
    result: string;
    config: JobConfig;
    configJson: string;
    startTime: string;
    endTime: string;
    taskId: number;
    statusText: string;
    clusterName: string;
  };

  export type VetricsMetrics = {
    'read-bytes': number;
    'read-bytes-complete': boolean;
    'write-bytes': number;
    'write-bytes-complete': boolean;
    'read-records': number;
    'read-records-complete': boolean;
    'write-records': number;
    'write-records-complete': boolean;
  };

  export type JobVertices = {
    id: string;
    name: string;
    maxParallelism: number;
    parallelism: number;
    status: string;
    duration: number;
    tasks: any;
    metrics: VetricsMetrics;
  };

  export type JobNodeInput = {
    num: number;
    id: string;
    ship_strategy: string;
    exchange: string;
  };

  export type JobNode = {
    id: string;
    parallelism: number;
    operator: string;
    operator_strategy: string;
    description: string;
    inputs: JobNodeInput[];
    optimizer_properties: any;
  };

  export type JobPlan = {
    jid: string;
    name: string;
    type: string;
    nodes: JobNode[];
  };

  export type Job = {
    jid: string;
    name: string;
    isStoppable: false;
    state: string;
    'start-time': number;
    'end-time': number;
    duration: number;
    maxParallelism: number;
    now: number;
    timestamps: any;
    vertices: JobVertices[];
    'status-counts': any;
    plan: JobPlan;
  };

  export type JobDataDtoItem = {
    id: number;
    job: Job;
    exceptions: any;
    checkpoints: any;
    checkpointsConfig: any;
    config: any;
    jar: string;
    cluster: string;
    clusterConfiguration: string;
    updateTime: string;
  };

  export type JobInfoDetail = {
    id: number;
    instance: JobInstance;
    cluster: any;
    clusterConfiguration: any;
    history: History;
    jobDataDto: JobDataDtoItem;
    jobManagerConfiguration: any;
    taskManagerConfiguration: any;
  };
}
