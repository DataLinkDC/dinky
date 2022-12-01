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


import {ClusterTableListItem, JarTableListItem} from "@/pages/RegistrationCenter/data";
import {ClusterConfigurationTableListItem} from "@/pages/RegistrationCenter/ClusterManage/ClusterConfiguration/data";
import {HistoryItem} from "@/components/Studio/StudioConsole/StudioHistory/data";
import {List} from "antd";

export type JobInstanceTableListItem = {
  id: number,
  name: string,
  taskId: number,
  step: number,
  clusterId: number,
  clusterAlias: string,
  type: string,
  jobManagerAddress: string,
  jid: string,
  status: string,
  historyId: number,
  error: string,
  failedRestartCount: number,
  duration: number,
  createTime: Date,
  updateTime: Date,
  finishTime: Date,
};

export type StatusCount = {
  all: number,
  initializing: number,
  running: number,
  finished: number,
  failed: number,
  canceled: number,
  restarting: number,
  created: number,
  failing: number,
  cancelling: number,
  suspended: number,
  reconciling: number,
  unknown: number,
}

export type JobInfoDetail = {
  id: number,
  instance: JobInstanceTableListItem,
  cluster: ClusterTableListItem,
  clusterConfiguration: ClusterConfigurationTableListItem,
  history: HistoryItem,
  jobHistory: JobHistoryItem,
  jobManagerConfiguration: JobManagerConfiguration
  taskManagerConfiguration: List<TaskManagerConfiguration>
  jar: JarTableListItem
}

export type VerticesTableListItem = {
  name: string,
  status: string,
  metrics: any,
  parallelism: number,
  startTime: string,
  duration: number,
  endTime: string,
  tasks: any,
}

export type JobHistoryItem = {
  id: number,
  job: string,
  exceptions: string,
  checkpoints: string,
  checkpointsConfig: string,
  config: string,
  jar: string,
  cluster: string,
  clusterConfiguration: string,
  updateTime: string,
}

export type JobManagerConfiguration = {
  metrics: any,
  jobManagerConfig: any,
  jobManagerLog: string,
  jobManagerStdout: string,
}

export type TaskManagerConfiguration = {
  containerId: string,
  containerPath: string,
  dataPort: number,
  jmxPort: number,
  timeSinceLastHeartbeat: number,
  slotsNumber: number,
  freeSlots: number,
  totalResource: string,
  freeResource: string,
  hardware: string,
  memoryConfiguration: string,
  taskContainerConfigInfo: TaskContainerConfigInfo,
}

export type TaskContainerConfigInfo = {
  metrics: any,
  taskManagerLog: string,
  taskManagerStdout: string,
  taskManagerThreadDump: string,
}



export type TaskVersion = {
  id: number,
  taskId: number,
  name: string,
  alias: string,
  dialect: string,
  type: string,
  versionId: number,
  statement: string,
  createTime: string,
}


export  type CheckPointsDetailInfo = {
  jobID: number,
  historyID: number,
  id: number,
  status: string,
  checkpoint_type: string,
  end_to_end_duration: number,
  external_path : string,
  latest_ack_timestamp: number,
  state_size: number,
  trigger_timestamp: number,
}

