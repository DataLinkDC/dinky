import React, {useState} from "react";
import {Button, Transfer} from "antd";
import {l} from "@/utils/intl";
import {ModalForm, ProFormSelect} from "@ant-design/pro-components";
import {JobProps, JobVertice} from "@/pages/DevOps/JobDetail/data";
import {API_CONSTANTS} from "@/services/constants";
import {useRequest} from "@@/exports";


const MonitorConfigTab = (props: JobProps) => {

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


    return <>
        <Transfer
            style={{width: "100vh"}}
            showSearch={true}
            dataSource={jobMetrics.data}
            titles={['监控项', '已选择']}
            targetKeys={targetKeys}
            onChange={(tgk) => setTargetKeys(tgk)}
            rowKey={(item) => item.id}
            render={(item) => item.id}
            listStyle={{width: "42vh", height: "50vh"}}
            oneWay
        />
    </>;
}

export default MonitorConfigTab;
