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

import { CreateBtn } from '@/components/CallBackButton/CreateBtn';
import { EditBtn } from '@/components/CallBackButton/EditBtn';
import { EnableSwitchBtn } from '@/components/CallBackButton/EnableSwitchBtn';
import { PopconfirmDeleteBtn } from '@/components/CallBackButton/PopconfirmDeleteBtn';
import { ShowLogBtn } from '@/components/CallBackButton/ShowLogBtn';
import { ShowCodeTreeIcon } from '@/components/Icons/CustomIcons';
import { Authorized, HasAuthority } from '@/hooks/useAccess';
import { BuildSteps } from '@/pages/RegCenter/GitProject/components/BuildSteps';
import ClassList from '@/pages/RegCenter/GitProject/components/BuildSteps/JarShow/JarList';
import { CodeTree } from '@/pages/RegCenter/GitProject/components/CodeTree';
import ProjectModal from '@/pages/RegCenter/GitProject/components/ProjectModal';
import {
  GIT_PROJECT_BUILD_STEP_JAVA_ENUM,
  GIT_PROJECT_BUILD_STEP_PYTHON_ENUM,
  GIT_PROJECT_CODE_TYPE,
  GIT_PROJECT_CODE_TYPE_ENUM,
  GIT_PROJECT_STATUS,
  GIT_PROJECT_STATUS_ENUM,
  GIT_PROJECT_TYPE,
  GIT_PROJECT_TYPE_ENUM,
  renderBranchesTagColor
} from '@/pages/RegCenter/GitProject/constans';
import { queryList } from '@/services/api';
import {
  handleAddOrUpdate,
  handleOption,
  handlePutDataByParams,
  handleRemoveById,
  updateDataByParam
} from '@/services/BusinessCrud';
import { PROTABLE_OPTIONS_PUBLIC, STATUS_ENUM, STATUS_MAPPING } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { PermissionConstants } from '@/types/Public/constants';
import { GitProject } from '@/types/RegCenter/data.d';
import { InitGitProjectState } from '@/types/RegCenter/init.d';
import { GitProjectState } from '@/types/RegCenter/state.d';
import { l } from '@/utils/intl';
import { BranchesOutlined, BuildTwoTone } from '@ant-design/icons';
import { ActionType, DragSortTable, ProColumns } from '@ant-design/pro-table';
import { Button, Empty, Popconfirm, Tag } from 'antd';
import React, { useRef, useState } from 'react';

const ProjectProTable: React.FC = () => {
  const [gitProjectStatus, setGitProjectStatus] = useState<GitProjectState>(InitGitProjectState);

  const actionRef = useRef<ActionType>();

  /**
   * execute and callback function
   * @param {() => void} callback
   * @returns {Promise<void>}
   */
  const executeAndCallback = async (callback: () => void) => {
    setGitProjectStatus((prevState) => ({ ...prevState, loading: true }));
    await callback();
    setGitProjectStatus((prevState) => ({ ...prevState, loading: false }));
    actionRef.current?.reload();
  };

  /**
   * update enabled
   * @param {Partial<GitProject>} value
   * @returns {Promise<void>}
   */
  const handleChangeEnable = async (value: Partial<GitProject>) => {
    await executeAndCallback(async () =>
      updateDataByParam(API_CONSTANTS.GIT_PROJECT_ENABLE, { id: value.id })
    );
  };

  /**
   * show log modal visible
   * @param {Partial<GitProject>} value
   * @returns {Promise<void>}
   */
  const handleShowLog = async (value: Partial<GitProject>) => {
    setGitProjectStatus((prevState) => ({ ...prevState, value: value, logOpen: true }));
  };

  /**
   * show code tree modal visible
   * @param {Partial<GitProject>} value
   * @returns {Promise<void>}
   */
  const handleShowCodeTree = async (value: Partial<GitProject>) => {
    setGitProjectStatus((prevState) => ({ ...prevState, value: value, codeTreeOpen: true }));
  };

  /**
   *  build modal visible,when build success, set build modal visible to step modal visible
   * @param {Partial<GitProject>} value
   * @returns {Promise<void>}
   */
  const handleBuild = async (value: Partial<GitProject>) => {
    await executeAndCallback(async () => {
      const result = await handlePutDataByParams(
        API_CONSTANTS.GIT_PROJECT_BUILD,
        l('rc.gp.building'),
        {
          id: value.id
        }
      );
      if (result)
        setGitProjectStatus((prevState) => ({ ...prevState, value: value, buildOpen: true }));
    });
  };

  /**
   * handle edit modal visible
   * @param {Partial<GitProject>} value
   * @returns {Promise<void>}
   */
  const handleEdit = async (value: Partial<GitProject>): Promise<void> =>
    setGitProjectStatus((prevState) => ({ ...prevState, value: value, editOpen: true }));

  /**
   * drag sort call
   * @param {GitProject[]} newDataSource
   * @returns {Promise<void>}
   */
  const handleDragSortEnd = async (newDataSource: GitProject[]): Promise<void> => {
    const updatedItems = newDataSource.map((item: GitProject, index: number) => ({
      ...item,
      orderLine: index + 1
    }));

    await executeAndCallback(async () =>
      handleOption(API_CONSTANTS.GIT_DRAGEND_SORT_PROJECT, l('rc.gp.ucl.projectOrder'), {
        sortList: updatedItems
      })
    );
  };

  /**
   * delete git project by id
   * @param {number} id
   * @returns {Promise<void>}
   */
  const handleDeleteSubmit = async (id: number) => {
    await executeAndCallback(
      async () => await handleRemoveById(API_CONSTANTS.GIT_PROJECT_DELETE, id)
    );
    await queryList(API_CONSTANTS.GIT_PROJECT).then((res) => handleDragSortEnd(res.data));
    actionRef.current?.reload();
  };

  /**
   * columns
   * @type {({dataIndex: string, title: any} | {dataIndex: string, title: any, renderText: (text, record) => JSX.Element} | {copyable: boolean, dataIndex: string, title: any} | {hideInSearch: boolean, dataIndex: string, valueEnum: {"1": {text: string}, "2": {text: string}}, filters: ({text: string, value: number} | {text: string, value: number})[], title: any, filterMultiple: boolean} | {hideInSearch: boolean, dataIndex: string, valueEnum: {"1": {text: string}, "2": {text: string}}, filters: ({text: string, value: number} | {text: string, value: number})[], title: any, filterMultiple: boolean} | {hideInSearch: boolean, dataIndex: string, valueEnum: {"0": {text: any, title: any, status: string}, "1": {text: any, title: any, status: string}, "2": {text: any, title: any, status: string}, "3": {text: any, title: any, status: string}, "4": {text: any, title: any, status: string}, "5": {text: any, title: any, status: string}, "6": {text: any, title: any, status: string}}, filters: ({text: any, value: number, status: string} | {text: any, value: number, status: string} | {text: any, value: number, status: string} | {text: any, value: number, status: string} | {text: any, value: number, status: string} | {text: any, value: number, status: string} | {text: any, value: number, status: string})[], title: any} | {hideInSearch: boolean, dataIndex: string, valueEnum: {"0": {text: any, title: any, status: string}, "1": {text: any, title: any, status: string}, "2": {text: any, title: any, status: string}, "3": {text: any, title: any, status: string}}, filters: ({text: any, value: number, status: string} | {text: any, value: number, status: string} | {text: any, value: number, status: string} | {text: any, value: number, status: string})[], title: any, filterMultiple: boolean} | {hideInSearch: boolean, dataIndex: string, valueType: string, title: any} | {dataIndex: string, title: any} | {hideInSearch: boolean, dataIndex: string, valueEnum: {true: {text: any, status: string}, false: {text: any, status: string}}, filters: [{text: any, value: number}, {text: any, value: number}], title: any, filterMultiple: boolean, render: (_, record) => JSX.Element} | {valueType: string, width: string, title: any, render: (text, record) => JSX.Element[]})[]}
   */
  const columns: ProColumns<GitProject>[] = [
    {
      title: l('rc.gp.level'),
      hideInSearch: true,
      dataIndex: 'id',
      tooltip: l('rc.gp.level.tooltip'),
      render: (dom: any, record: GitProject) => {
        return (
          <Tag
            style={{ marginLeft: 10 }}
            color={record.orderLine > 3 ? 'default' : 'success'}
          >{`No.${record.orderLine}`}</Tag>
        );
      }
    },
    {
      title: l('rc.gp.name'),
      dataIndex: 'name',
      ellipsis: true,
      width: '10%'
    },
    {
      title: l('rc.gp.branch'),
      dataIndex: 'branch',
      renderText: (text: string, record: GitProject) => {
        return (
          <Tag icon={<BranchesOutlined />} color={renderBranchesTagColor(record.branch)}>
            {record.branch}
          </Tag>
        );
      }
    },
    {
      title: l('rc.gp.url'),
      dataIndex: 'url',
      copyable: true,
      ellipsis: true,
      width: '10%'
    },
    {
      title: l('rc.gp.codeType'),
      dataIndex: 'codeType',
      hideInSearch: true,
      filterMultiple: false,
      width: '6%',
      valueEnum: GIT_PROJECT_CODE_TYPE_ENUM,
      filters: GIT_PROJECT_CODE_TYPE
    },
    {
      title: l('rc.gp.type'),
      dataIndex: 'type',
      hideInSearch: true,
      filterMultiple: false,
      width: '8%',
      valueEnum: GIT_PROJECT_TYPE_ENUM,
      filters: GIT_PROJECT_TYPE
    },
    {
      title: l('rc.gp.buildStep'),
      dataIndex: 'buildStep',
      hideInSearch: true,
      // filters: GIT_PROJECT_BUILD_STEP,
      valueEnum: (row) =>
        row.codeType === 1 ? GIT_PROJECT_BUILD_STEP_JAVA_ENUM : GIT_PROJECT_BUILD_STEP_PYTHON_ENUM
    },
    {
      title: l('rc.gp.buildState'),
      dataIndex: 'buildState',
      hideInSearch: true,
      filterMultiple: false,
      filters: GIT_PROJECT_STATUS,
      valueEnum: GIT_PROJECT_STATUS_ENUM
    },
    {
      title: l('rc.gp.lastBuild'),
      dataIndex: 'lastBuild',
      hideInSearch: true,
      valueType: 'dateTime',
      width: '12%'
    },
    {
      title: l('global.table.note'),
      dataIndex: 'description',
      ellipsis: true
    },
    {
      title: l('global.table.isEnable'),
      dataIndex: 'enable',
      width: '6%',
      hideInSearch: true,
      filters: STATUS_MAPPING(),
      filterMultiple: false,
      valueEnum: STATUS_ENUM(),
      render: (_: any, record: GitProject) => {
        return (
          <EnableSwitchBtn
            key={`${record.id}_enable`}
            record={record}
            disabled={!HasAuthority(PermissionConstants.REGISTRATION_GIT_PROJECT_EDIT)}
            onChange={() => handleChangeEnable(record)}
          />
        );
      }
    },
    {
      title: l('global.table.operate'),
      valueType: 'option',
      width: '10%',
      fixed: 'right',
      render: (text: any, record: GitProject) => [
        <Authorized
          key={`${record.id}_showLog`}
          path={PermissionConstants.REGISTRATION_GIT_PROJECT_SHOW_LOG}
        >
          <ShowLogBtn
            disabled={record.buildStep === 0}
            key={`${record.id}_showLog`}
            onClick={() => handleShowLog(record)}
          />
        </Authorized>,
        <Button
          key={`${record.id}_code`}
          className={'options-button'}
          title={l('button.showCode')}
          icon={<ShowCodeTreeIcon />}
          onClick={() => handleShowCodeTree(record)}
        />,
        <Authorized
          key={`${record.id}_build`}
          path={PermissionConstants.REGISTRATION_GIT_PROJECT_BUILD}
        >
          <Popconfirm
            className={'options-button'}
            key={`${record.id}_build`}
            placement='topRight'
            title={l('button.build')}
            description={l('rc.gp.buildConfirm')}
            onConfirm={() => handleBuild(record)}
            okText={l('button.confirm')}
            cancelText={l('button.cancel')}
          >
            <Button
              title={l('button.build')}
              key={`${record.id}_buildbtn`}
              htmlType={'submit'}
              autoFocus
              icon={<BuildTwoTone />}
            />
          </Popconfirm>
        </Authorized>,
        <Authorized
          key={`${record.id}_edit`}
          path={PermissionConstants.REGISTRATION_GIT_PROJECT_EDIT}
        >
          <EditBtn key={`${record.id}_edit`} onClick={() => handleEdit(record)} />
        </Authorized>,
        <Authorized
          key={`${record.id}_delete`}
          path={PermissionConstants.REGISTRATION_GIT_PROJECT_DELETE}
        >
          <PopconfirmDeleteBtn
            key={`${record.id}_delete`}
            onClick={() => handleDeleteSubmit(record.id)}
            description={l('rc.gp.deleteConfirm')}
          />
        </Authorized>
      ]
    }
  ];

  /**
   * cancel all status
   */
  const handleCancel = () => {
    setGitProjectStatus(InitGitProjectState);
    actionRef.current?.reload();
  };

  /**
   * added or update submit
   * @param {Partial<GitProject>} value
   * @returns {Promise<void>}
   */
  const handleAddOrUpdateSubmit = async (value: Partial<GitProject>) => {
    await handleAddOrUpdate(API_CONSTANTS.GIT_SAVE_UPDATE, value);
    handleCancel();
  };

  /**
   * render jar
   * @param {GitProject} record
   * @returns {JSX.Element}
   */
  const renderClassList = (record: GitProject) => {
    const { udfClassMapList, id } = record;
    if (udfClassMapList) {
      return <ClassList projectId={id} jarAndClassesList={JSON.parse(udfClassMapList)} />;
    }
    return <Empty image={Empty.PRESENTED_IMAGE_DEFAULT} />;
  };

  /**
   * re try build
   * @param value
   */
  const handleReTryBuild = async (value: Partial<GitProject>) => {
    handleCancel();
    await handleBuild(value);
  };

  /**
   * render
   */
  return (
    <>
      <DragSortTable<GitProject>
        {...PROTABLE_OPTIONS_PUBLIC}
        headerTitle={l('rc.gp.management')}
        columns={columns}
        loading={gitProjectStatus.loading}
        actionRef={actionRef}
        dragSortKey={'id'}
        toolBarRender={() => [
          <Authorized key='create' path={PermissionConstants.REGISTRATION_GIT_PROJECT_ADD}>
            <CreateBtn
              key={'gittable'}
              onClick={() =>
                setGitProjectStatus((prevState) => ({ ...prevState, addedOpen: true }))
              }
            />
          </Authorized>
        ]}
        expandable={{
          expandRowByClick: false,
          expandedRowRender: (record: GitProject) => renderClassList(record)
        }}
        request={(params, sorter, filter: any) =>
          queryList(API_CONSTANTS.GIT_PROJECT, { ...params, sorter, filter })
        }
        onDragSortEnd={(beforeIndex, afterIndex, newDataSource) => handleDragSortEnd(newDataSource)}
      />
      {/* added modal form */}
      <ProjectModal
        onCancel={handleCancel}
        onSubmit={(value) => handleAddOrUpdateSubmit(value)}
        modalVisible={gitProjectStatus.addedOpen}
        values={{}}
      />
      {/* modify modal form */}
      <ProjectModal
        onCancel={handleCancel}
        onSubmit={(value) => handleAddOrUpdateSubmit(value)}
        modalVisible={gitProjectStatus.editOpen}
        values={gitProjectStatus.value}
      />
      {/* build steps modal */}
      {gitProjectStatus.buildOpen && (
        <BuildSteps
          title={l('rc.gp.build')}
          onOk={handleCancel}
          onRebuild={() => handleReTryBuild(gitProjectStatus.value)}
          onReTry={() => handleReTryBuild(gitProjectStatus.value)}
          values={gitProjectStatus.value}
        />
      )}
      {/* show build log modal */}
      {/*{logModalVisible &&  <ShowLog modalVisible={logModalVisible} onCancel={handleCancel} values={formValues}/>}*/}
      {gitProjectStatus.logOpen && (
        <BuildSteps
          title={l('rc.gp.log')}
          onOk={handleCancel}
          showLog={gitProjectStatus.logOpen}
          onRebuild={() => handleReTryBuild(gitProjectStatus.value)}
          values={gitProjectStatus.value}
        />
      )}
      {/* show code tree modal */}
      <CodeTree
        modalVisible={gitProjectStatus.codeTreeOpen}
        onCancel={handleCancel}
        values={gitProjectStatus.value}
      />
    </>
  );
};
export default ProjectProTable;
