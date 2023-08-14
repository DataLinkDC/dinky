import {BaseBeanColumns} from "@/types/Public/data";

/**
 * about flink job
 */
declare namespace Jobs {

  export type JobInstance =  BaseBeanColumns &  {
    taskId: number,
    step: number,
    clusterId: number,
    clusterName: string,
    type: string,
    jobManagerAddress: string,
    jid: string,
    status: string,
    historyId: number,
    error: string,
    failedRestartCount: number,
    duration: number,
    finishTime: Date,
  };

  export type JobConfig = {
    config:{},
    savePointStrategy:string,
    savePointPath:string,
    useSqlFragment:string,
    isJarTask:string,
    useBatchModel:string,
  };

  export type  History = {
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

  export type JobInfoDetail = {
    id: number,
    instance: JobInstance,
    cluster: any,
    clusterConfiguration: any,
    history: History,
    jobHistory: any,
    jobManagerConfiguration: any,
    taskManagerConfiguration: any,
  };


}
