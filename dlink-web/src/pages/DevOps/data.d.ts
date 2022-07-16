import {ClusterTableListItem} from "@/pages/Cluster/data";
import {ClusterConfigurationTableListItem} from "@/pages/ClusterConfiguration/data";
import {HistoryItem} from "@/components/Studio/StudioConsole/StudioHistory/data";
import {JarTableListItem} from "@/pages/Jar/data";
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
  end_to_end_duration: number,
  external_path : string,
  latest_ack_timestamp: number,
  state_size: number,
  trigger_timestamp: number,
}


export  type SavePointInfo = {
  id: number,
  taskId: number,
  name: string,
  type: string,
  path: string,
  createTime: Date,
}
