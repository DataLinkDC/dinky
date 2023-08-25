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

  export type JobVertices = {
    id: string;
    name: string;
    maxParallelism: number;
    parallelism: number;
    status: string;
    duration: number;
    tasks: any;
    metrics: any;
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
    'status-counts': {};
    plan: {};
  };
  export type JobHistoryItem = {
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
    jobHistory: JobHistoryItem;
    jobManagerConfiguration: any;
    taskManagerConfiguration: any;
  };
}
