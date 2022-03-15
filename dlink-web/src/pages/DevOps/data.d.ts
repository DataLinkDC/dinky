import {ClusterTableListItem} from "@/pages/Cluster/data";
import {ClusterConfigurationTableListItem} from "@/pages/ClusterConfiguration/data";
import {HistoryItem} from "@/components/Studio/StudioConsole/StudioHistory/data";
import {JarTableListItem} from "@/pages/Jar/data";

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
