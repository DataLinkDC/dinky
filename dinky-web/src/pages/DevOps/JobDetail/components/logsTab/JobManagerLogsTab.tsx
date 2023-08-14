/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


import {Descriptions, Tabs, Tag, Typography} from 'antd';
import {ProCard} from "@ant-design/pro-components";
import {Jobs} from "@/types/DevOps/data";
import CodeShow from "@/components/CustomEditor/CodeShow";
import React, {useState} from "react";
import {useRequest} from "@@/exports";
import {API_CONSTANTS} from "@/services/constants";
import {getData} from "@/services/api";

const {Text, Paragraph} = Typography;

type JobProps = {
    jobDetail: Jobs.JobInfoDetail;
};

const JobManagerLogsTab = (props: JobProps) => {

    const {jobDetail} = props;
    const jmaddr = jobDetail?.history?.jobManagerAddress;

    const log = useRequest({
        url: API_CONSTANTS.GET_JOBMANAGER_LOG,
        params: {address: jmaddr},
    });

    const stdout = useRequest({
        url: API_CONSTANTS.GET_JOBMANAGER_STDOUT,
        params: {address: jmaddr},
    });

    const dump = useRequest({
        url: API_CONSTANTS.GET_JOBMANAGER_THREAD_DUMP,
        params: {address: jmaddr},
    });

    const getLog = (ur: any) => {
        return <CodeShow
            code={ur.data}
            height={600}
        />
    }

    return <>
        <ProCard>
            <Tabs
                size={"small"}
                items={[
                    {label: 'Log', key: "LOG", children: getLog(log)},
                    {label: 'Std Out', key: "STDOUT", children: getLog(stdout)},
                    {label: 'Thread Dump', key: "DUMP", children: getLog(dump)},
                ]}
                // onTabClick={(key) => log.run()}
            />

        </ProCard>
    </>
};

export default JobManagerLogsTab;
