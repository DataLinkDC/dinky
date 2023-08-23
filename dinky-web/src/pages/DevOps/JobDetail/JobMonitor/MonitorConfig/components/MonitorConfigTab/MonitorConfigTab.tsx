import React, {useState} from "react";
import {Button, Transfer} from "antd";
import {JobProps, JobVertice} from "@/pages/DevOps/JobDetail/data";
import { API_CONSTANTS } from '@/services/endpoints';
import {useRequest} from "@@/exports";
import {Jobs} from "@/types/DevOps/data";

type Props = {
  vertice: JobVertice,
  jobDetail: Jobs.JobInfoDetail
  onValueChange:(verticeId:string,keys:string[])=>any
}

const MonitorConfigTab = (props: Props) => {

  const {vertice, jobDetail,onValueChange} = props
  const jobManagerUrl = jobDetail?.cluster?.jobManagerHost
  const jobId = jobDetail?.jobHistory?.job?.jid

  const [targetKeys, setTargetKeys] = useState<string[]>([]);

  const jobMetricItems = useRequest({
    url: API_CONSTANTS.GET_JOB_MERTICE_ITEMS,
    params: {address: jobManagerUrl, jobId: jobId, verticeId: vertice.id}
  });

  const onChange = (tgk:string[]) => {
    setTargetKeys(tgk)
    onValueChange(vertice.id,tgk)
  }

  return <>
    <Transfer
      // style={{width: "99vh"}}
      showSearch={true}
      dataSource={jobMetricItems.data??[]}
      titles={['监控项', '已选择']}
      targetKeys={targetKeys}
      onChange={(tgk) => onChange(tgk)}
      rowKey={(item) => item.id}
      render={(item) => item.id}
      listStyle={{width: "42vh", height: "50vh"}}
      oneWay
    />
  </>;
}

export default MonitorConfigTab;
