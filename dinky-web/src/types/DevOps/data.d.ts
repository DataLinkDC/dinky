import {BaseBeanColumns} from "@/types/Public/data";

/**
 * about flink job
 */
declare namespace Jobs {

  export type JobInstanceTableListItem =  BaseBeanColumns &  {
    id: number,
    name: string,
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
    createTime: Date,
    updateTime: Date,
    finishTime: Date,
  };


}
