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
import {
  getDataByParams,
  handleAddOrUpdate, handleData,
  handlePutDataByParams,
  handleRemoveById,
  updateEnabled
} from "@/services/BusinessCrud";
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
  GIT_PROJECT_BUILD_STEP,
  GIT_PROJECT_BUILD_STEP_ENUM,
  GIT_PROJECT_CODE_TYPE,
  GIT_PROJECT_CODE_TYPE_ENUM, GIT_PROJECT_STATUS, GIT_PROJECT_STATUS_ENUM, GIT_PROJECT_TYPE, GIT_PROJECT_TYPE_ENUM,
  renderBranchesTagColor
} from "@/pages/RegCenter/GitProject/constans";
import {ShowCodeTreeIcon} from "@/components/Icons/CustomIcons";
import {getData, queryList} from "@/services/api";
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


  /**
   * execute and callback function
   * @param {() => void} callback
   * @returns {Promise<void>}
   */
  const executeAndCallback = async (callback: () => void) => {
    setLoading(true);
    await callback();
    setLoading(false);
    actionRef.current?.reload?.();
  };

  /**
   * update enabled
   * @param {Partial<GitProject>} value
   * @returns {Promise<void>}
   */
  const handleChangeEnable = async (value: Partial<GitProject>) => {
    await executeAndCallback(async () => await updateEnabled(API_CONSTANTS.GIT_PROJECT_ENABLE, {id: value.id}));
  };


  /**
   * show log modal visible
   * @param {Partial<GitProject>} value
   * @returns {Promise<void>}
   */
  const handleShowLog = async (value: Partial<GitProject>) => {
    setFormValues(value);
    handleLogVisible(true);
  };

  /**
   * show code tree modal visible
   * @param {Partial<GitProject>} value
   * @returns {Promise<void>}
   */
  const handleShowCodeTree = async (value: Partial<GitProject>) => {
    setFormValues(value);
    handleCodeTreeVisible(true);
  };


  /**
   *  build modal visible,when build success, set build modal visible to step modal visible
   * @param {Partial<GitProject>} value
   * @returns {Promise<void>}
   */
  const handleBuild = async (value: Partial<GitProject>) => {
    await executeAndCallback(async () => {
        const result = await handlePutDataByParams(API_CONSTANTS.GIT_PROJECT_BUILD, l("rc.gp.building"), {id: value.id});
        if (result) {
          handleBuildVisible(true);
            // todo : build success, set build modal visible and set build steps
        }
    });
  };

  /**
   * handle edit modal visible
   * @param {Partial<GitProject>} value
   * @returns {Promise<void>}
   */
  const handleEdit = async (value: Partial<GitProject>) => {
    setFormValues(value);
    handleUpdateModalVisible(true);
  };


  /**
   * delete git project by id
   * @param {number} id
   * @returns {Promise<void>}
   */
  const handleDeleteSubmit = async (id: number) => {
    await executeAndCallback(async () => await handleRemoveById(API_CONSTANTS.GIT_PROJECT_DELETE, id));
  };

  /**
   * columns
   * @type {({dataIndex: string, title: any} | {dataIndex: string, title: any, renderText: (text, record) => JSX.Element} | {copyable: boolean, dataIndex: string, title: any} | {hideInSearch: boolean, dataIndex: string, valueEnum: {"1": {text: string}, "2": {text: string}}, filters: ({text: string, value: number} | {text: string, value: number})[], title: any, filterMultiple: boolean} | {hideInSearch: boolean, dataIndex: string, valueEnum: {"1": {text: string}, "2": {text: string}}, filters: ({text: string, value: number} | {text: string, value: number})[], title: any, filterMultiple: boolean} | {hideInSearch: boolean, dataIndex: string, valueEnum: {"0": {text: any, title: any, status: string}, "1": {text: any, title: any, status: string}, "2": {text: any, title: any, status: string}, "3": {text: any, title: any, status: string}, "4": {text: any, title: any, status: string}, "5": {text: any, title: any, status: string}, "6": {text: any, title: any, status: string}}, filters: ({text: any, value: number, status: string} | {text: any, value: number, status: string} | {text: any, value: number, status: string} | {text: any, value: number, status: string} | {text: any, value: number, status: string} | {text: any, value: number, status: string} | {text: any, value: number, status: string})[], title: any} | {hideInSearch: boolean, dataIndex: string, valueEnum: {"0": {text: any, title: any, status: string}, "1": {text: any, title: any, status: string}, "2": {text: any, title: any, status: string}, "3": {text: any, title: any, status: string}}, filters: ({text: any, value: number, status: string} | {text: any, value: number, status: string} | {text: any, value: number, status: string} | {text: any, value: number, status: string})[], title: any, filterMultiple: boolean} | {hideInSearch: boolean, dataIndex: string, valueType: string, title: any} | {dataIndex: string, title: any} | {hideInSearch: boolean, dataIndex: string, valueEnum: {true: {text: any, status: string}, false: {text: any, status: string}}, filters: [{text: any, value: number}, {text: any, value: number}], title: any, filterMultiple: boolean, render: (_, record) => JSX.Element} | {valueType: string, width: string, title: any, render: (text, record) => JSX.Element[]})[]}
   */
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
      ellipsis: true,
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
      filters: GIT_PROJECT_BUILD_STEP,
      valueEnum: GIT_PROJECT_BUILD_STEP_ENUM,
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
        return <EnableSwitchBtn key={`${record.id}_enable`} record={record}
                                onChange={() => handleChangeEnable(record)}/>;
      },
    },
    {
      title: l("global.table.operate"),
      valueType: "option",
      width: "10vw",
      render: (text, record) => [
        <ShowLogBtn key={`${record.id}_showLog`} onClick={() => handleShowLog(record)}/>,
        <Button
          key={`${record.id}_code`}
          className={"options-button"}
          title={l("button.showCode")}
          icon={<ShowCodeTreeIcon/>}
          onClick={() => handleShowCodeTree(record)}
        />,
        <Popconfirm
          className={"options-button"}
          key={`${record.id}_build`}
          placement="topRight"
          title={l("button.build")}
          description={l("rc.gp.buildConfirm")}
          onConfirm={() => handleBuild(record)}
          okText={l("button.confirm")}
          cancelText={l("button.cancel")}
        >
          <Button title={l("button.build")} key={`${record.id}_buildbtn`} icon={<BuildTwoTone/>}/>
        </Popconfirm>,
        <EditBtn key={`${record.id}_edit`} onClick={() => handleEdit(record)}/>,
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
    actionRef.current?.reload?.();
  };


  /**
   * added or update submit
   * @param {Partial<GitProject>} value
   * @returns {Promise<void>}
   */
  const handleAddOrUpdateSubmit = async (value: Partial<GitProject>) => {
    await handleAddOrUpdate(API_CONSTANTS.GIT_SAVE_UPDATE, value);
    handleCancel();
    actionRef.current?.reload?.();
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
      toolBarRender={() => [<CreateBtn key={"gitcreate"} onClick={() => handleModalVisible(true)}/>]}
      request={(params, sorter, filter: any) => queryList(API_CONSTANTS.GIT_PROJECT, {...params, sorter, filter})}
      columns={columns}
    />
    {/* added modal form */}
    <ProjectModal onCancel={handleCancel} onSubmit={(value) => handleAddOrUpdateSubmit(value)}
                  modalVisible={modalVisible} values={{}}/>
    {/* modify modal form */}
    <ProjectModal onCancel={handleCancel} onSubmit={(value) => handleAddOrUpdateSubmit(value)}
                  modalVisible={updateModalVisible}
                  values={formValues}/>
    {/* build steps modal */}
    {buildModalVisible && <BuildSteps onCancel={handleCancel} modalVisible={buildModalVisible}
                               values={formValues}/>}
    {/* show build log modal */}
    {logModalVisible &&  <ShowLog modalVisible={logModalVisible} onCancel={handleCancel} values={formValues}/>}
    {/* show code tree modal */}
    <CodeTree modalVisible={codeTreeModalVisible} onCancel={handleCancel} values={formValues}/>
  </>;
};
export default ProjectProTable;
