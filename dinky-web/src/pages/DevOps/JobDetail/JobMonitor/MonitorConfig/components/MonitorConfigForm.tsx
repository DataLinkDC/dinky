import React, {useState} from "react";
import {Button, message, Tabs, Transfer} from "antd";
import {l} from "@/utils/intl";
import {ModalForm, ProFormSelect} from "@ant-design/pro-components";
import {JobProps, JobVertice} from "@/pages/DevOps/JobDetail/data";
import {API_CONSTANTS} from "@/services/constants";
import {useRequest} from "@@/exports";
import MonitorConfigTab
  from "@/pages/DevOps/JobDetail/JobMonitor/MonitorConfig/components/MonitorConfigTab/MonitorConfigTab";
import {putData, putDataAsArray} from "@/services/api";


const MonitorConfigForm = (props: JobProps) => {

  const {jobDetail} = props

  const [targetKeys, setTargetKeys] = useState<{[verticeId: string]: string[]}>({})


  const saveJobMetrics = () => {
    let params:any = [];
    const layout = `${jobDetail.instance.name}-${jobDetail.instance.taskId}`

    Object.entries(targetKeys).forEach(([verid, value]) => {
      params = [...params,...value.map((item)=>({
        taskId: jobDetail.history.taskId,
        vertices: verid,
        metrics: item,
        title: item,
        layoutName: layout,
        showType: "Chart",
        showSize: "25%"
      }))]
    })

    return putDataAsArray(API_CONSTANTS.SAVE_FLINK_METRICS + layout, params)
  }

  const onValueChange = (verticeId: string, keys: string[]) => {
    targetKeys[verticeId] = keys
    setTargetKeys(targetKeys)
  };


  const itemTabs = jobDetail?.jobHistory?.job?.vertices?.map((item: JobVertice) => {
    return {
      key: item.id,
      label: item.name,
      children: <MonitorConfigTab
        jobDetail={jobDetail}
        vertice={item}
        onValueChange={onValueChange}/>
    }
  })

  return <ModalForm
    width={1000}
    layout={"horizontal"}
    title="任务监控配置"
    trigger={<Button type="primary">监控项配置</Button>}
    onFinish={async (values) => await saveJobMetrics()}
  >
    {/*<ProFormSelect*/}
    {/*  name="vertices"*/}
    {/*  label={l('metrics.flink.subTask')}*/}
    {/*  placeholder={l('metrics.flink.subTask.placeholder')}*/}
    {/*  options={getSubTask(jobDetail?.jobHistory?.job?.vertices)}*/}
    {/*  fieldProps={{onChange: (subTask) => jobMetrics.run(subTask)}}*/}
    {/*/>*/}

    <Tabs items={itemTabs}/>

    {/*<Transfer*/}
    {/*    style={{width: "100vh"}}*/}
    {/*    showSearch={true}*/}
    {/*    dataSource={jobMetrics.data}*/}
    {/*    titles={['监控项', '已选择']}*/}
    {/*    targetKeys={targetKeys}*/}
    {/*    onChange={(tgk)=>setTargetKeys(tgk)}*/}
    {/*    rowKey={(item) => item.id}*/}
    {/*    render={(item) => item.id}*/}
    {/*    listStyle={{width: "42vh", height: "50vh"}}*/}
    {/*    oneWay*/}
    {/*/>*/}
  </ModalForm>;
}

export default MonitorConfigForm;
