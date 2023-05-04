/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import React, {useRef, useState} from "react";
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {GitProject} from "@/types/RegCenter/data";
import {handleRemoveById, updateEnabled} from "@/services/BusinessCrud";
import {
    API_CONSTANTS,
    PROTABLE_OPTIONS_PUBLIC,
    STATUS_ENUM,
    STATUS_MAPPING,
    SWITCH_OPTIONS
} from "@/services/constants";
import {l} from "@/utils/intl";
import {Button, Popconfirm, Space, Switch, Tag} from "antd";
import {BranchesOutlined, BuildTwoTone, EditTwoTone} from "@ant-design/icons";
import {
    GIT_PROJECT_CODE_TYPE,
    GIT_PROJECT_CODE_TYPE_ENUM, GIT_PROJECT_STATUS, GIT_PROJECT_STATUS_ENUM, GIT_PROJECT_TYPE, GIT_PROJECT_TYPE_ENUM,
    renderBranchesTagColor
} from "@/pages/RegCenter/GitProject/constans";
import {DangerDeleteIcon, ShowCodeTreeIcon, ShowLogIcon} from "@/components/Icons/CustomIcons";
import {queryList} from "@/services/api";
import {BuildSteps} from "@/pages/RegCenter/GitProject/components/BuildSteps";
import {ShowLog} from "@/pages/RegCenter/GitProject/components/ShowLog";
import {CodeTree} from "@/pages/RegCenter/GitProject/components/CodeTree";
import {CreateBtn} from "@/components/CallBackButton/CreateBtn";
import ProjectModal from "@/pages/RegCenter/GitProject/components/ProjectModal";

const ProjectProTable: React.FC = () => {

    const actionRef = useRef<ActionType>();
    const [loading, setLoading] = useState<boolean>(false);
    const [modalVisible, handleModalVisible] = useState<boolean>(false);
    const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
    const [buildModalVisible, handleBuildVisible] = useState<boolean>(false);
    const [logModalVisible, handleLogVisible] = useState<boolean>(false);
    const [codeTreeModalVisible, handleCodeTreeVisible] = useState<boolean>(false);
    const [formValues, setFormValues] = useState<Partial<GitProject>>({});


    const executeAndCallback= async (callback: () =>  void) => {
        setLoading(true);
        await callback();
        setLoading(false);
        actionRef.current?.reload?.();
    }
    /**
     * change enable
     * @param value
     */
    const handleChangeEnable = async (value: Partial<GitProject>) => {
        await executeAndCallback(() => updateEnabled(API_CONSTANTS.GIT_PROJECT_ENABLE, {id: value.id}));
    };

    /**
     * handle delete
     * @param id
     */
    const handleDeleteSubmit = async (id: number) => {
        await executeAndCallback(() => handleRemoveById(API_CONSTANTS.GIT_PROJECT_DELETE, id));
    }
    const columns: ProColumns<GitProject>[] = [
        {
            title: l("rc.gp.name"),
            dataIndex: "name",
        },
        {
            title: l("rc.gp.branches"),
            dataIndex: "branch",
            renderText: (text, record) => {
                return <Tag icon={<BranchesOutlined/>} color={renderBranchesTagColor(record.branch)}>{record.branch}</Tag>;
            }
        },
        {
            title: l("rc.gp.url"),
            dataIndex: "url",
            copyable: true,
        },
        {
            title: l("rc.gp.codeType"),
            dataIndex: "codeType",
            hideInSearch: true,
            filterMultiple: false,
            valueEnum: GIT_PROJECT_CODE_TYPE_ENUM,
            filters: GIT_PROJECT_CODE_TYPE,
        },
        {
            title: l("rc.gp.type"),
            dataIndex: "type",
            hideInSearch: true,
            filterMultiple: false,
            valueEnum: GIT_PROJECT_TYPE_ENUM,
            filters: GIT_PROJECT_TYPE,
        },
        {
            title: l("rc.gp.buildStep"),
            dataIndex: "buildStep",
            hideInSearch: true,
        },
        {
            title: l("rc.gp.buildState"),
            dataIndex: "buildState",
            hideInSearch: true,
            filterMultiple: false,
            filters: GIT_PROJECT_STATUS,
            valueEnum: GIT_PROJECT_STATUS_ENUM,
        },
        {
            title: l("rc.gp.lastBuild"),
            dataIndex: "lastBuild",
            hideInSearch: true,
            valueType: "dateTime",
        },
        {
            title: l("global.table.note"),
            dataIndex: "description",
        },
        {
            title: l("global.table.isEnable"),
            dataIndex: "enable",
            hideInSearch: true,
            filters: STATUS_MAPPING(),
            filterMultiple: false,
            valueEnum: STATUS_ENUM(),
            render: (_, record) => {
                return <>
                    <Space>
                        <Switch
                            key={record.id}
                            {...SWITCH_OPTIONS()}
                            checked={record.enabled}
                            onChange={() => handleChangeEnable(record)}/>
                    </Space>
                </>;
            },
        },
        {
            title: l("global.table.operate"),
            valueType: "option",
            width: 200,
            render: (text, record) => [
                <Button
                    key={record.id}
                    className={"options-button"}
                    title={l("button.showLog")}
                    icon={<ShowLogIcon/>}
                    onClick={() => {
                        setFormValues(record);
                        handleLogVisible(true);
                    }}
                />,
                <Button
                    key={record.id}
                    className={"options-button"}
                    title={l("button.showCode")}
                    icon={<ShowCodeTreeIcon/>}
                    onClick={() => {
                        setFormValues(record);
                        handleCodeTreeVisible(true);
                    }}
                />,
                <Popconfirm
                    className={"options-button"}
                    key={record.id}
                    placement="topRight"
                    title={l("button.build")}
                    description={l("rc.gp.buildConfirm")}
                    onConfirm={() => {
                        setFormValues(record);
                        handleBuildVisible(true);
                    }}
                    okText={l("button.confirm")}
                    cancelText={l("button.cancel")}
                >
                    <Button key={"buildGitProjectIcon"} icon={<BuildTwoTone/>}/>
                </Popconfirm>,

                <Button
                    key={record.id}
                    className={"options-button"}
                    title={l("button.edit")}
                    icon={<EditTwoTone/>}
                    onClick={() => {
                        setFormValues(record);
                        handleUpdateModalVisible(true);
                    }}
                />,
                <Popconfirm
                    className={"options-button"}
                    key={"GitProjectDelete"}
                    placement="topRight"
                    title={l("button.delete")}
                    description={l("rc.gp.deleteConfirm")}
                    onConfirm={() => {
                        handleDeleteSubmit(record.id);
                    }}
                    okText={l("button.confirm")}
                    cancelText={l("button.cancel")}
                >
                    <Button key={"deleteGitProjectIcon"} icon={<DangerDeleteIcon/>}/>
                </Popconfirm>
            ],
        },
    ];

    /**
     * cancel all status
     */
    const handleAllCancel = () => {
        setFormValues({});
        handleModalVisible(false);
        handleUpdateModalVisible(false);
        handleBuildVisible(false);
        handleLogVisible(false);
        handleCodeTreeVisible(false);
    };


    /**
     * submit
     */
    const handleSubmit = () => {
        handleAllCancel();
    };
    /**
     * cancel
     */
    const handleCancel = () => {
        handleAllCancel();
    };

    /**
     * render
     */
    return <>
        <ProTable<GitProject>
            {...PROTABLE_OPTIONS_PUBLIC}
            loading={loading}
            actionRef={actionRef}
            headerTitle={l("rc.gp.management")}
            toolBarRender={() => [<CreateBtn onClick={() => handleModalVisible(true)}/>]}
            request={(params, sorter, filter:any) => queryList(API_CONSTANTS.GIT_PROJECT, {...params, sorter, filter})}
            columns={columns}
        />
        {/* added modal form */}
        { modalVisible && <ProjectModal onCancel={handleCancel} onSubmit={handleSubmit} modalVisible={modalVisible} values={{}}/>}
        {/* modify modal form */}
        { updateModalVisible && <ProjectModal onCancel={handleCancel} onSubmit={handleSubmit} modalVisible={updateModalVisible} values={formValues}/>}
        {/* build steps modal */}
        { formValues && <BuildSteps onCancel={handleCancel} onSubmit={handleSubmit} modalVisible={buildModalVisible} values={formValues}/>}
        {/* show build log modal */}
        {logModalVisible && <ShowLog modalVisible={logModalVisible} onCancel={handleCancel} values={formValues}/>}
        {/* show code tree modal */}
        {codeTreeModalVisible && <CodeTree modalVisible={codeTreeModalVisible} onCancel={handleCancel} values={formValues}/>}
    </>
}
export default ProjectProTable;