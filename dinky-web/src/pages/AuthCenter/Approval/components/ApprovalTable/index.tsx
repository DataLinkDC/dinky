import { ApprovalBasicInfo, OperationStatus, OperationType } from "@/types/ApprovalCenter/data.d";
import React, { useEffect, useRef, useState } from "react";
import { ApprovalListState } from "@/types/ApprovalCenter/state.d";
import { InitApprovalList } from "@/types/ApprovalCenter/init.d";
import { ActionType, ProColumns } from "@ant-design/pro-table";
import { Button, Flex, Tag } from "antd";
import { l } from "@/utils/intl";
import { ProTable } from "@ant-design/pro-components";
import { queryList } from "@/services/api";
import { API_CONSTANTS } from "@/services/endpoints";
import { handleOption, queryDataByParams } from "@/services/BusinessCrud";
import { getTaskDetails, getUserData } from "@/pages/DataStudio/service";
import { TaskState } from "@/pages/DataStudio/type";
import TaskInfoModal from "@/pages/AuthCenter/Approval/components/TaskInfoModal";
import ApprovalModal from "@/pages/AuthCenter/Approval/components/ApprovalModal";
import { useAsyncEffect } from "ahooks";
import { getValueFromLocalStorage } from "@/utils/function";
import { TENANT_ID } from "@/services/constants";


type UserFormProps = {
  tableType: 'review' | 'submit'
}

const ApprovalTable: React.FC<UserFormProps> = (props) => {
  //states
  // approval list
  const [approvalListState, setApprovalListState] = useState<ApprovalListState>(InitApprovalList);
  // modal states
  const [modalOpen, setModalOpen] = useState(false);
  const [activeOperation, setActiveOperationType] = useState<OperationType>(OperationType.UNKNOWN);
  const [activeId, setActiveId] = useState(0);
  const [taskInfo, setTaskInfo] = useState<TaskState>({});
  const [preVersionStatement, setPreVersionStatement] = useState<string>("");
  const [curVersionStatement, setCurVersionStatement] = useState<string>("");

  const [modalTitle, setModalTitle] = useState('');
  const [taskInfoOpen, setTaskInfoOpen] = useState(false);
  const actionRef = useRef<ActionType>(); // table action

  const userMap: Map<number, string> = new Map();

  useAsyncEffect(async () => {
    const usersRes = await queryDataByParams(API_CONSTANTS.GET_USER_LIST_BY_TENANTID, {id: getValueFromLocalStorage(TENANT_ID)});
    usersRes.users.forEach((user) => {
      userMap.set(user.id, user.username);
    })
    console.log(userMap)
  }, [])

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setApprovalListState((prevState) => ({...prevState, loading: true}));
    await callback();
    actionRef.current?.reload();
    setApprovalListState((prevState) => ({...prevState, loading: false}));
  };

  const handleApprovalOperation = (operation: OperationType, entity: ApprovalBasicInfo) => {
    setActiveOperationType(operation);
    setActiveId(entity.id);
    switch (operation) {
      case OperationType.SUBMIT:
        setModalTitle(l('approval.operation.submit'));
        break;
      case OperationType.REJECT:
        setModalTitle(l('approval.operation.reject'));
        break;
      case OperationType.APPROVE:
        setModalTitle(l('approval.operation.approve'));
        break;
    }
    setModalOpen(true);
  };

  const handleWithdraw = async (entity: ApprovalBasicInfo) => {
    await executeAndCallbackRefresh(async () => {
      await handleOption(API_CONSTANTS.APPROVAL_WITHDRAW, l('approval.operation.withdraw'), entity);
    })
  };

  const handleCancel = async (entity: ApprovalBasicInfo) => {
    await executeAndCallbackRefresh(async () => {
      await handleOption(API_CONSTANTS.APPROVAL_CANCEL, l('approval.operation.cancel'), entity);
    })
  };

  const queryApproval = async (params, sorter, filter: any) => {
    const queryRes = await queryList(props.tableType === 'review' ? API_CONSTANTS.GET_REVIEW_REQUIRED_APPROVAL : API_CONSTANTS.GET_SUBMITTED_APPROVAL, {
      ...params,
      sorter,
      filter
    });
    const convertedQueryRes = [];
    queryRes.data.forEach((approval) => {
      console.log(userMap)
      convertedQueryRes.push({...approval, submitterName: userMap.get(approval.submitter), reviewerName: userMap.get(approval.reviewer)})
    })
    console.log(convertedQueryRes)
    return {...queryRes, data: convertedQueryRes};
  }

  const queryTaskDiffInfo = async (taskId: number, preVersionId: number, curVersionId: number) => {
    const taskInfo = await getTaskDetails(taskId);
    setTaskInfo(taskInfo);

    // when task submit first no previous version exits
    setPreVersionStatement("");
    const versions = await queryDataByParams(API_CONSTANTS.GET_JOB_VERSION, {taskId: taskId});

    versions.forEach((version) => {
      if (version.versionId == preVersionId) {
        setPreVersionStatement(version.statement);
      } else if (version.versionId == curVersionId) {
        setCurVersionStatement(version.statement);
      }
    })
  }

  const handleApprovalEvent = async (record) => {
    await executeAndCallbackRefresh(async () => {
      switch (activeOperation) {
        case OperationType.SUBMIT:
          await handleOption(API_CONSTANTS.APPROVAL_SUBMIT, l('approval.operation.submit'), record);
          break;
        case OperationType.REJECT:
          await handleOption(API_CONSTANTS.APPROVAL_REJECT, l('approval.operation.reject'), record);
          break;
        case OperationType.APPROVE:
          await handleOption(API_CONSTANTS.APPROVAL_APPROVE, l('approval.operation.approve'), record);
          break;
      }
    });
    setModalOpen(false);
  }

  /**
   * render operation based on current state
   * @param entity entity
   */
  const renderOperation = (entity: ApprovalBasicInfo) => {
    const buttons = [];
    switch (entity.status) {
      case OperationStatus.CREATED:
        buttons.push(
          <Button
            size={'small'}
            type={'primary'}
            onClick={() => {
              handleApprovalOperation(OperationType.SUBMIT, entity);
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
        break;
      case OperationStatus.SUBMITTED:
        buttons.push(
          <Button
            size={'small'}
            type={'primary'}
            onClick={() => {
              handleApprovalOperation(OperationType.APPROVE, entity);
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
              handleApprovalOperation(OperationType.REJECT, entity);
            }}
            danger
          >
            {l('approval.operation.reject')}
          </Button>
        );
        buttons.push(
          <Button
            size={'small'}
            onClick={async () => {
              await handleWithdraw(entity)
            }}
            danger
          >
            {l('approval.operation.withdraw')}
          </Button>
        );
        break;
    }
    return (
      <>
        <Flex gap={'small'} warp>
          {buttons}
        </Flex>
      </>
    )
  };

  const renderInfo = (entity: ApprovalBasicInfo) => {
    return (
      <>
        <Button
          onClick={async () => {
            await queryTaskDiffInfo(entity.taskId, entity.previousTaskVersion, entity.currentTaskVersion);
            setTaskInfoOpen(true);
            setActiveId(entity.id);
          }}
          size={'small'}
        >
          {l('button.check')}
        </Button>
      </>
    )
  }

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
    },
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
        open={modalOpen}
        onOpenChange={(open) => {
          setModalOpen(open);
        }}
        title={modalTitle}
        activeId={activeId}
        operationType={activeOperation}
        handleSubmit={handleApprovalEvent}
      />
      <TaskInfoModal
        open={taskInfoOpen}
        onCancel={() => {
          setTaskInfoOpen(false)
        }}
        taskInfo={taskInfo}
        preVersionStatement={preVersionStatement}
        curVersionStatement={curVersionStatement}
      />
      <ProTable<ApprovalBasicInfo>
        search={{filterType: 'query'}}
        pagination={{pageSize: 20, size: 'small'}}
        options={false}
        rowKey={(record) => record.id}
        loading={approvalListState.loading}
        columns={approvalColumns}
        request={queryApproval}
        actionRef={actionRef}
      />
    </>
  );
}

export default ApprovalTable;


