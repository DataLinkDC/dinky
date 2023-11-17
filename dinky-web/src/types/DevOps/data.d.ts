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

import { BaseBeanColumns } from '@/types/Public/data';
import { Alert } from '@/types/RegCenter/data.d';

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

  export type Subtasks = {
    subtask: number;
    backpressureLevel: string;
    ratio: number;
    idleRatio: number;
    busyRatio: number;
  };

  export type JobNodeBackPressure = {
    status: string;
    backpressureLevel: string;
    endTimestamp: number;
    subtasks: Subtasks[];
  };
  export type JobNodeWaterMark = {
    id: string;
    value: string;
  };

  export type JobPlanNode = {
    id: string;
    parallelism: number;
    operator: string;
    operator_strategy: string;
    description: string;
    inputs: JobNodeInput[];
    optimizer_properties: any;
    backpressure: JobNodeBackPressure;
    watermark: JobNodeWaterMark[];
  };

  export type JobPlan = {
    jid: string;
    name: string;
    type: string;
    nodes: JobPlanNode[];
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

  export type JobConfigInfo = {
    jid: string;
    name: string;
    executionConfig: ExecutionConfig;
  };
  export type ExecutionConfig = {
    executionMode: string;
    restartStrategy: string;
    jobParallelism: number;
    objectReuse: boolean;
    userConfig: any;
  };

  export type JobDataDtoItem = {
    id: number;
    job: Job;
    exceptions: any;
    checkpoints: any;
    checkpointsConfig: any;
    config: JobConfigInfo;
    jar: string;
    cluster: string;
    clusterConfiguration: string;
    updateTime: string;
  };

  export type JobInfoDetail = {
    id: number;
    instance: JobInstance;
    clusterInstance: any;
    clusterConfiguration: any;
    history: History;
    jobDataDto: JobDataDtoItem;
    jobManagerConfiguration: any;
    taskManagerConfiguration: any;
  };
}

export interface AlertHistory {
  id: number;
  tenantId: number;
  alertGroupId: number;
  alertGroup: Alert.AlertGroup;
  jobInstanceId: number;
  title: string;
  content: string;
  status: number;
  log: string;
  createTime: Date;
  updateTime: Date;
}

export interface LineageTableColumn {
  name: string;
  title: string;
}

export interface LineageTable {
  id: string;
  name: string;
  isCollapse: boolean;
  columns: LineageTableColumn[];
}

export interface LineageRelations {
  id: string;
  srcTableId: string;
  tgtTableId: string;
  srcTableColName: string;
  tgtTableColName: string;
}

export interface LineageDetailInfo {
  tables: LineageTable[];
  relations: LineageRelations[];
}
