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


}
