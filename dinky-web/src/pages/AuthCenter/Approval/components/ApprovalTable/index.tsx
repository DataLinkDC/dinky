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

import { ApprovalBasicInfo, OperationStatus, OperationType } from '@/types/AuthCenter/data.d';
import React, { useRef, useState } from 'react';
import { InitApprovalList } from '@/types/AuthCenter/init.d';
import { ActionType, ProColumns } from '@ant-design/pro-table';
import { Button, Flex, Tag } from 'antd';
import { l } from '@/utils/intl';
import { ProTable } from '@ant-design/pro-components';
import { queryList } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { handleOption, queryDataByParams } from '@/services/BusinessCrud';
import { getTaskDetails } from '@/pages/DataStudio/service';
import { TaskState } from '@/pages/DataStudio/type';
import TaskInfoModal from '@/pages/AuthCenter/Approval/components/TaskInfoModal';
import ApprovalModal from '@/pages/AuthCenter/Approval/components/ApprovalModal';
import { useAsyncEffect } from 'ahooks';
import { getTenantByLocalStorage } from '@/utils/function';
import { ApprovalListState } from '@/types/AuthCenter/state.d';

type UserFormProps = {
  tableType: 'review' | 'submit';
};

const ApprovalTable: React.FC<UserFormProps> = (props) => {
  // approval list
  const [approvalListState, setApprovalListState] = useState<ApprovalListState>(InitApprovalList);

  // users
  const [userMap, setUserMap] = useState<Map<number, string>>();

  // active approval state
  const [activeApprovalState, setActiveApprovalState] = useState<{
    activeId: number;
    taskInfoOpen: boolean;
    taskBasicInfo: TaskState | undefined;
    preVersionStatement: string;
    curVersionStatement: string;
  }>({
    activeId: -1,
    taskInfoOpen: false,
    taskBasicInfo: undefined,
    preVersionStatement: '',
    curVersionStatement: ''
  });

  // operation modal states
  const [operationState, setOperationState] = useState<{
    operationType: OperationType;
    operationModalOpen: boolean;
    operationDesc: string;
  }>({ operationType: OperationType.SUBMIT, operationModalOpen: false, operationDesc: '' });

  const actionRef = useRef<ActionType>(); // table action

  // get user list
  useAsyncEffect(async () => {
    const tempUserMap: Map<number, string> = new Map();
    const usersRes = await queryDataByParams(API_CONSTANTS.GET_USER_LIST_BY_TENANTID, {
      id: getTenantByLocalStorage()
    });
    usersRes.users.forEach((user) => {
      tempUserMap.set(user.id, user.username);
    });
    setUserMap(tempUserMap);
    actionRef.current?.reload();
  }, []);

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setApprovalListState((prevState) => ({ ...prevState, loading: true }));
    await callback();
    actionRef.current?.reload();
    setApprovalListState((prevState) => ({ ...prevState, loading: false }));
  };

  const handleOperationButtonClick = (operation: OperationType, entity: ApprovalBasicInfo) => {
    setActiveApprovalState((prevState) => ({ ...prevState, activeId: entity.id }));
    switch (operation) {
      case OperationType.SUBMIT:
        setOperationState((prevState) => ({
          ...prevState,
          operationDesc: l('approval.operation.submit'),
          operationType: operation,
          operationModalOpen: true
        }));
        break;
      case OperationType.REJECT:
        setOperationState((prevState) => ({
          ...prevState,
          operationDesc: l('approval.operation.reject'),
          operationType: operation,
          operationModalOpen: true
        }));
        break;
      case OperationType.APPROVE:
        setOperationState((prevState) => ({
          ...prevState,
          operationDesc: l('approval.operation.approve'),
          operationType: operation,
          operationModalOpen: true
        }));
        break;
    }
  };

  const handleWithdraw = async (entity: ApprovalBasicInfo) => {
    await executeAndCallbackRefresh(async () => {
      await handleOption(API_CONSTANTS.APPROVAL_WITHDRAW, l('approval.operation.withdraw'), entity);
    });
  };

  const handleCancel = async (entity: ApprovalBasicInfo) => {
    await executeAndCallbackRefresh(async () => {
      await handleOption(API_CONSTANTS.APPROVAL_CANCEL, l('approval.operation.cancel'), entity);
    });
  };

  const queryApproval = async (params, sorter, filter: any) => {
    const queryRes = await queryList(
      props.tableType === 'review'
        ? API_CONSTANTS.GET_REVIEW_REQUIRED_APPROVAL
        : API_CONSTANTS.GET_SUBMITTED_APPROVAL,
      {
        ...params,
        sorter,
        filter
      }
    );
    const convertedQueryRes = [];
    queryRes.data.forEach((approval) => {
      convertedQueryRes.push({
        ...approval,
        submitterName: userMap?.get(approval.submitter),
        reviewerName: userMap?.get(approval.reviewer)
      });
    });
    return { ...queryRes, data: convertedQueryRes };
  };

  const queryTaskDiffInfo = async (taskId: number, preVersionId: number, curVersionId: number) => {
    const taskInfo = await getTaskDetails(taskId);
    setActiveApprovalState((prevState) => ({
      ...prevState,
      taskBasicInfo: taskInfo,
      preVersionStatement: '',
      curVersionStatement: ''
    }));
    const versions = await queryDataByParams(API_CONSTANTS.GET_JOB_VERSION, { taskId: taskId });

    versions.forEach((version) => {
      if (version.versionId == preVersionId) {
        setActiveApprovalState((prevState) => ({
          ...prevState,
          taskBasicInfo: taskInfo,
          preVersionStatement: version.statement
        }));
      }
      if (version.versionId == curVersionId) {
        setActiveApprovalState((prevState) => ({
          ...prevState,
          taskBasicInfo: taskInfo,
          curVersionStatement: version.statement
        }));
      }
    });
  };

  const handleApprovalSubmit = async (record) => {
    await executeAndCallbackRefresh(async () => {
      switch (operationState.operationType) {
        case OperationType.SUBMIT:
          await handleOption(API_CONSTANTS.APPROVAL_SUBMIT, l('approval.operation.submit'), record);
          break;
        case OperationType.REJECT:
          await handleOption(API_CONSTANTS.APPROVAL_REJECT, l('approval.operation.reject'), record);
          break;
        case OperationType.APPROVE:
          await handleOption(
            API_CONSTANTS.APPROVAL_APPROVE,
            l('approval.operation.approve'),
            record
          );
          break;
      }
    });
    setOperationState((prevState) => ({ ...prevState, operationModalOpen: false }));
  };

  /**
   * render operation based on current state
   * @param entity entity
   */
  const renderOperation = (entity: ApprovalBasicInfo) => {
    const buttons = [];
    // review list: approve and reject, submit list: submit withdraw cancel
    switch (entity.status) {
      case OperationStatus.CREATED:
        if (props.tableType == 'submit') {
          buttons.push(
            <Button
              size={'small'}
              type={'primary'}
              onClick={() => {
                handleOperationButtonClick(OperationType.SUBMIT, entity);
              }}
            >
              {l('approval.operation.submit')}
            </Button>
          );
          buttons.push(
            <Button
              size={'small'}
              onClick={async () => {
                await handleCancel(entity);
              }}
            >
              {l('approval.operation.cancel')}
            </Button>
          );
        }
        break;
      case OperationStatus.SUBMITTED:
        if (props.tableType == 'review') {
          buttons.push(
            <Button
              size={'small'}
              type={'primary'}
              onClick={() => {
                handleOperationButtonClick(OperationType.APPROVE, entity);
              }}
            >
              {l('approval.operation.approve')}
            </Button>
          );
          buttons.push(
            <Button
              size={'small'}
              type={'primary'}
              onClick={() => {
                handleOperationButtonClick(OperationType.REJECT, entity);
              }}
              danger
            >
              {l('approval.operation.reject')}
            </Button>
          );
        } else {
          buttons.push(
            <Button
              size={'small'}
              onClick={async () => {
                await handleWithdraw(entity);
              }}
              danger
            >
              {l('approval.operation.withdraw')}
            </Button>
          );
        }
        break;
    }
    return (
      <>
        <Flex gap={'small'} warp>
          {buttons}
        </Flex>
      </>
    );
  };

  const renderInfo = (entity: ApprovalBasicInfo) => {
    return (
      <>
        <Button
          onClick={async () => {
            await queryTaskDiffInfo(
              entity.taskId,
              entity.previousTaskVersion,
              entity.currentTaskVersion
            );
            setActiveApprovalState((prevState) => ({
              ...prevState,
              taskInfoOpen: true,
              activeId: entity.id
            }));
          }}
          size={'small'}
        >
          {l('button.check')}
        </Button>
      </>
    );
  };

  /**
   * status color
   */
  const statusNum = {
    CREATED: {
      text: <Tag color={'yellow'}>{l('approval.status.created')}</Tag>
    },
    SUBMITTED: {
      text: <Tag color={'blue'}>{l('approval.status.submitted')}</Tag>
    },
    APPROVED: {
      text: <Tag color={'green'}>{l('approval.status.approved')}</Tag>
    },
    REJECTED: {
      text: <Tag color={'red'}>{l('approval.status.rejected')}</Tag>
    },
    CANCELED: {
      text: <Tag color={'gray'}>{l('approval.status.canceled')}</Tag>
    }
  };

  const approvalColumns: ProColumns<ApprovalBasicInfo>[] = [
    {
      title: l('approval.id'),
      dataIndex: 'id',
      key: 'id'
    },
    {
      title: l('approval.taskId'),
      dataIndex: 'taskId'
    },
    {
      title: l('approval.previousTaskVersion'),
      dataIndex: 'previousTaskVersion',
      hideInSearch: true
    },
    {
      title: l('approval.currentTaskVersion'),
      dataIndex: 'currentTaskVersion',
      hideInSearch: true
    },
    {
      title: l('approval.taskInfo'),
      valueType: 'option',
      render: (_: any, record: ApprovalBasicInfo) => renderInfo(record)
    },
    {
      title: l('approval.status'),
      dataIndex: 'status',
      valueEnum: statusNum
    },
    {
      title: l('approval.submitterName'),
      dataIndex: 'submitterName',
      hideInSearch: true
    },
    {
      title: l('approval.submitterComment'),
      dataIndex: 'submitterComment',
      hideInSearch: true
    },
    {
      title: l('approval.reviewerName'),
      dataIndex: 'reviewerName',
      hideInSearch: true
    },
    {
      title: l('approval.reviewerComment'),
      dataIndex: 'reviewerComment',
      hideInSearch: true
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      hideInSearch: true,
      sorter: true
    },
    {
      title: l('global.table.updateTime'),
      dataIndex: 'updateTime',
      hideInSearch: true
    },
    {
      title: l('global.table.operate'),
      valueType: 'option',
      width: '12%',
      fixed: 'right',
      render: (_: any, record: ApprovalBasicInfo) => renderOperation(record)
    }
  ];

  return (
    <>
      <ApprovalModal
        open={operationState.operationModalOpen}
        onOpenChange={(open) => {
          setOperationState((prevState) => ({ ...prevState, operationModalOpen: open }));
        }}
        title={operationState.operationDesc}
        activeId={activeApprovalState.activeId}
        operationType={operationState.operationType}
        handleSubmit={handleApprovalSubmit}
      />
      <TaskInfoModal
        open={activeApprovalState.taskInfoOpen}
        onCancel={() => {
          setActiveApprovalState((prevState) => ({ ...prevState, taskInfoOpen: false }));
        }}
        taskInfo={activeApprovalState.taskBasicInfo}
        preVersionStatement={activeApprovalState.preVersionStatement}
        curVersionStatement={activeApprovalState.curVersionStatement}
      />
      <ProTable<ApprovalBasicInfo>
        search={{ filterType: 'query' }}
        pagination={{ pageSize: 20, size: 'small' }}
        options={false}
        rowKey={(record) => record.id}
        loading={approvalListState.loading}
        columns={approvalColumns}
        request={queryApproval}
        actionRef={actionRef}
      />
    </>
  );
};

export default ApprovalTable;
