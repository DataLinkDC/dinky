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
import {Document, GitProject} from "@/types/RegCenter/data";
import {handleAddOrUpdate, handleRemoveById, updateEnabled} from "@/services/BusinessCrud";
import {
    API_CONSTANTS,
    PROTABLE_OPTIONS_PUBLIC,
    STATUS_ENUM,
    STATUS_MAPPING,
} from "@/services/constants";
import {l} from "@/utils/intl";
import {Button, Popconfirm, Tag} from "antd";
import {BranchesOutlined, BuildTwoTone} from "@ant-design/icons";
import {
    GIT_PROJECT_CODE_TYPE,
    GIT_PROJECT_CODE_TYPE_ENUM, GIT_PROJECT_STATUS, GIT_PROJECT_STATUS_ENUM, GIT_PROJECT_TYPE, GIT_PROJECT_TYPE_ENUM,
    renderBranchesTagColor
} from "@/pages/RegCenter/GitProject/constans";
import {ShowCodeTreeIcon} from "@/components/Icons/CustomIcons";
import {queryList} from "@/services/api";
import {BuildSteps} from "@/pages/RegCenter/GitProject/components/BuildSteps";
import {ShowLog} from "@/pages/RegCenter/GitProject/components/ShowLog";
import {CodeTree} from "@/pages/RegCenter/GitProject/components/CodeTree";
import {CreateBtn} from "@/components/CallBackButton/CreateBtn";
import ProjectModal from "@/pages/RegCenter/GitProject/components/ProjectModal";
import {EditBtn} from "@/components/CallBackButton/EditBtn";
import {PopconfirmDeleteBtn} from "@/components/CallBackButton/PopconfirmDeleteBtn";
import {EnableSwitchBtn} from "@/components/CallBackButton/EnableSwitchBtn";
import {ShowLogBtn} from "@/components/CallBackButton/ShowLogBtn";

const ProjectProTable: React.FC = () => {

    const actionRef = useRef<ActionType>();
    const [loading, setLoading] = useState<boolean>(false);
    const [modalVisible, handleModalVisible] = useState<boolean>(false);
    const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
    const [buildModalVisible, handleBuildVisible] = useState<boolean>(false);
    const [logModalVisible, handleLogVisible] = useState<boolean>(false);
    const [codeTreeModalVisible, handleCodeTreeVisible] = useState<boolean>(false);
    const [formValues, setFormValues] = useState<Partial<GitProject>>({});


    const executeAndCallback = async (callback: () => void) => {
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
            title: l("rc.gp.branch"),
            dataIndex: "branch",
            renderText: (text, record) => {
                return <Tag icon={<BranchesOutlined/>}
                            color={renderBranchesTagColor(record.branch)}>{record.branch}</Tag>;
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
                return <EnableSwitchBtn key={`${record.id}_enable`} record={record} onChange={() => handleChangeEnable(record)}/>;
            },
        },
        {
            title: l("global.table.operate"),
            valueType: "option",
            width: 200,
            render: (text, record) => [
                <ShowLogBtn key={`${record.id}_showLog`} onClick={() => {
                    setFormValues(record);
                    handleLogVisible(true);
                }}/>,

                <Button
                    key={`${record.id}_code`}
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
                    key={`${record.id}_build`}
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
                    <Button key={`${record.id}_buildbtn`} icon={<BuildTwoTone/>}/>
                </Popconfirm>,

                <EditBtn key={`${record.id}_edit`} onClick={() => {
                    setFormValues(record);
                    handleUpdateModalVisible(true);
                }}/>,

                <PopconfirmDeleteBtn key={`${record.id}_delete`} onClick={() => handleDeleteSubmit(record.id)}
                                     description={l("rc.gp.deleteConfirm")}/>,
            ],
        },
    ];

    /**
     * cancel all status
     */
    const handleCancel = () => {
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

    const handleAddOrUpdateSubmit = async (value: Partial<GitProject>) => {
        await handleAddOrUpdate(API_CONSTANTS.GIT_SAVE_UPDATE, value);
        handleCancel();
        actionRef.current?.reload?.();
    }


    /**
     * render
     */
    return <>
        <ProTable<GitProject>
            {...PROTABLE_OPTIONS_PUBLIC}
            loading={loading}
            actionRef={actionRef}
            headerTitle={l("rc.gp.management")}
            toolBarRender={() => [<CreateBtn key={"gitcreate"} onClick={() => handleModalVisible(true)}/>]}
            request={(params, sorter, filter: any) => queryList(API_CONSTANTS.GIT_PROJECT, {...params, sorter, filter})}
            columns={columns}
        />
        {/* added modal form */}
        {modalVisible &&
            <ProjectModal onCancel={handleCancel} onSubmit={(value) => handleAddOrUpdateSubmit(value)} modalVisible={modalVisible} values={{}}/>}
        {/* modify modal form */}
        {updateModalVisible &&
            <ProjectModal onCancel={handleCancel} onSubmit={(value) => handleAddOrUpdateSubmit(value)} modalVisible={updateModalVisible}
                          values={formValues}/>}
        {/* build steps modal */}
        {formValues && <BuildSteps onCancel={handleCancel} onSubmit={(value) => handleAddOrUpdateSubmit(value)} modalVisible={buildModalVisible}
                                   values={formValues}/>}
        {/* show build log modal */}
        {logModalVisible && <ShowLog modalVisible={logModalVisible} onCancel={handleCancel} values={formValues}/>}
        {/* show code tree modal */}
        {codeTreeModalVisible &&
            <CodeTree modalVisible={codeTreeModalVisible} onCancel={handleCancel} values={formValues}/>}
    </>
}
export default ProjectProTable;