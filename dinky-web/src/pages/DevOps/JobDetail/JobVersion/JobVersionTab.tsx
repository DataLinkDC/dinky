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


import {Jobs} from "@/types/DevOps/data";
import {ProCard} from "@ant-design/pro-components";
import {Card, Col, List, message, Modal, Row, Skeleton, Space, Tabs, Tag, Tooltip, Typography} from "antd";
import moment from "moment";
import CodeShow from "@/components/CustomEditor/CodeShow";
import React, {useState} from "react";
import {useRequest} from "@@/exports";
import {API_CONSTANTS} from "@/services/constants";
import {
    DeleteOutlined,
    DeleteTwoTone,
    DeliveredProcedureOutlined,
    RocketOutlined,
    RollbackOutlined
} from "@ant-design/icons";
import {l} from "@/utils/intl";
import {JobProps} from "@/pages/DevOps/JobDetail/data";
import {JOB_LIFE_CYCLE} from "@/pages/DevOps/constants";
import {getData, removeById, removeData} from "@/services/api";
import {TaskHistoryListItem} from "@/components/VersionList/data";
import VersionList from "@/components/VersionList";


const JobVersionTab = (props: JobProps) => {

    const {jobDetail} = props;
    const latestVersion: TaskHistoryListItem = {
        type: jobDetail.history.type,
        statement: jobDetail.history.statement,
        createTime: jobDetail.history.startTime,
        versionId: "LATEST",
        isLatest: true
    };

    const [currentVersion, setCurrentVersion] = useState<TaskHistoryListItem>({statement: ""})

    const versionList = useRequest({
        url: API_CONSTANTS.GET_JOB_VERSION,
        params: {taskId: jobDetail.history.taskId},
    }, {
        onSuccess: (data: TaskHistoryListItem[], params) => {
            data.splice(0, 0, latestVersion)
        }
    });

    const deleteVersion = (item: TaskHistoryListItem) => {
        Modal.confirm({
            title: l('devops.jobinfo.version.delete'),
            content: l('devops.jobinfo.version.delete.sure', '', {version: item.versionId}),
            okText: l('button.confirm'),
            cancelText: l('button.cancel'),
            onOk: async () => {
                const result = await removeById(API_CONSTANTS.GET_JOB_VERSION, {versionId: item.id});
                if (result) {
                    message.success("Delete Success");
                } else {
                    message.error("Delete faile");
                }
                versionList.run()
            }
        });
    }

    const renderVersionList = () => {
        return (
            <Row>
                <Col span={3}>
                    <VersionList
                        data={versionList.data}
                        onDeleteListen={deleteVersion}
                        onSelectListen={(item) => setCurrentVersion(item)}
                        header={l('devops.jobinfo.version.versionList')}
                    />
                </Col>
                <Col span={21}>
                    <Card title={"V" + currentVersion?.versionId} bordered={false}
                          extra={<>
                              <Tag key={"v-type"} color="blue">{currentVersion?.type}</Tag>
                              <Tag key={"v-dialect"} color="yellow">{currentVersion?.dialect}</Tag>
                          </>}>
                        <CodeShow
                            code={currentVersion?.statement}
                            height={500}
                            language={"sql"}
                        />
                    </Card>
                </Col>
            </Row>
        );
    };


    return <>
        <Card>
            {renderVersionList()}
        </Card>
    </>
};

export default JobVersionTab;
