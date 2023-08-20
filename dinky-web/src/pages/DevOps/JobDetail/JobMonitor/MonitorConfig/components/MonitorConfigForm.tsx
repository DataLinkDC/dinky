import React, {useState} from "react";
import {Button, Transfer} from "antd";
import {l} from "@/utils/intl";
import {ModalForm, ProFormSelect} from "@ant-design/pro-components";
import {JobProps, JobVertice} from "@/pages/DevOps/JobDetail/data";
import {API_CONSTANTS} from "@/services/constants";
import {useRequest} from "@@/exports";


const MonitorConfigForm = (props: JobProps) => {

    const {jobDetail} = props
    const jobManagerUrl = jobDetail?.cluster?.jobManagerHost
    const jobId = jobDetail?.jobHistory?.job?.jid

    const [targetKeys, setTargetKeys] = useState<string[]>([]);

    const jobMetrics = useRequest((subTask) => (
        {
            url: API_CONSTANTS.GET_JOB_MERTICE_ITEMS,
            params: {address: jobManagerUrl, jobId: jobId, verticeId: subTask}
        }
    ), {manual: true});

    const saveJobMetrics = useRequest((subTask) => (
        {
            url: API_CONSTANTS.SAVE_FLINK_METRICS,
            params: (()=>{return {}})
        }
    ), {manual: true});

    const getSubTask = (vets: JobVertice[]) => {
        return vets.map((item) => {
            return {value: item.id, label: item.name,}
        })
    }

    const saveMerticesList = () =>{
        // SAVE_FLINK_METRICS

    }

    return <ModalForm
        layout={"horizontal"}
        title="任务监控配置"
        trigger={<Button type="primary">监控项配置</Button>}
        onFinish={async (values) => {
        }}
    >
        <ProFormSelect
            name="vertices"
            label={l('metrics.flink.subTask')}
            placeholder={l('metrics.flink.subTask.placeholder')}
            options={getSubTask(jobDetail?.jobHistory?.job?.vertices)}
            fieldProps={{onChange: (subTask) => jobMetrics.run(subTask)}}
        />
        <Transfer
            style={{width: "100vh"}}
            showSearch={true}
            dataSource={jobMetrics.data}
            titles={['监控项', '已选择']}
            targetKeys={targetKeys}
            onChange={(tgk)=>setTargetKeys(tgk)}
            rowKey={(item) => item.id}
            render={(item) => item.id}
            listStyle={{width: "42vh", height: "50vh"}}
            oneWay
        />
    </ModalForm>;
}

export default MonitorConfigForm;
