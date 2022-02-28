export type JobInstanceTableListItem = {
  id: number,
  name: string,
  taskId: number,
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
}
