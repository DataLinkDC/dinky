import {ApprovalBasicInfo, ApprovalOperationInfo, OperationStatus, OperationType} from "@/types/ApprovalCenter/data.d";
import React, {useEffect, useRef, useState} from "react";
import {ApprovalListState} from "@/types/ApprovalCenter/state.d";
import {InitApprovalList} from "@/types/ApprovalCenter/init.d";
import {ActionType, ProColumns} from "@ant-design/pro-table";
import {Button, Flex, Tag} from "antd";
import {l} from "@/utils/intl";
import {ProTable} from "@ant-design/pro-components";
import ApprovalModal from "@/pages/ApprovalCenter/TaskApproval/components/ApprovalModal";
import {queryList} from "@/services/api";
import TaskInfoModal from "@/pages/ApprovalCenter/TaskApproval/components/TaskInfoModal";
import {API_CONSTANTS} from "@/services/endpoints";


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
  const [modalTitle, setModalTitle] = useState('');
  const [reviewerList, setReviewerList] = useState([]);
  const [taskInfoOpen, setTaskInfoOpen] = useState(false);

  // init approval list
  useEffect(() => {
    // TODO get from backend
    const testApprovalList: ApprovalBasicInfo[] = [];

    const createStatusApproval: ApprovalBasicInfo = {
      id: 11,
      taskId: 1,
      previousTaskVersion: 1,
      currentTaskVersion: 2,
      status: OperationStatus.CREATED,
      submitterName: 'test',
      reviewerName: 'test',
      submitterComment: 'Comment',
      reviewerComment: 'Comment',
      createTime: '2024-12-11',
      updateTime: '2024-12-11'
    };
    testApprovalList.push(createStatusApproval);
    const submitStatusApproval: ApprovalBasicInfo = {
      id: 1,
      taskId: 1,
      previousTaskVersion: 1,
      currentTaskVersion: 2,
      status: OperationStatus.SUBMITTED,
      submitterName: 'test',
      reviewerName: 'test',
      submitterComment: 'Comment',
      reviewerComment: 'Comment',
      createTime: '2024-12-11',
      updateTime: '2024-12-11'
    };
    testApprovalList.push(submitStatusApproval);
    const approvedStatusApproval: ApprovalBasicInfo = {
      id: 12,
      taskId: 1,
      previousTaskVersion: 1,
      currentTaskVersion: 2,
      status: OperationStatus.APPROVED,
      submitterName: 'test',
      reviewerName: 'test',
      submitterComment: 'Comment',
      reviewerComment: 'Comment',
      createTime: '2024-12-11',
      updateTime: '2024-12-11'
    };
    testApprovalList.push(approvedStatusApproval);
    const rejectedStatusApproval: ApprovalBasicInfo = {
      id: 13,
      taskId: 1,
      previousTaskVersion: 1,
      currentTaskVersion: 2,
      status: OperationStatus.REJECTED,
      submitterName: 'test',
      reviewerName: 'test',
      submitterComment: 'Comment',
      reviewerComment: 'Comment',
      createTime: '2024-12-11',
      updateTime: '2024-12-11'
    };
    testApprovalList.push(rejectedStatusApproval);

    setApprovalListState((prevState) => ({...prevState, approvalList: testApprovalList}));
    setReviewerList([{1: 'admin'}, {2: 'reviewer'}]);
  }, []);

  const actionRef = useRef<ActionType>(); // table action

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setApprovalListState((prevState) => ({...prevState, loading: true}));
    await callback();
    setApprovalListState((prevState) => ({...prevState, loading: false}));
    actionRef.current?.reload?.();
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

  const handleModalSubmit = async (info: ApprovalOperationInfo, type: OperationType) => {
    await executeAndCallbackRefresh(async () => {
      console.log(type + ' ' + info);
    })
  };

  const handleWithdraw = async (id: number) => {
    await executeAndCallbackRefresh(async () => {
      console.log(id);
    })
  };

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
        break;
      case OperationStatus.SUBMITTED:
        if (props.tableType == 'review') {
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
        } else {
          buttons.push(
            <Button
              size={'small'}
              type={'primary'}
              onClick={() => {
                handleWithdraw(entity.id).then(r => {
                });
              }}
              danger
            >
              {l('approval.operation.reject')}
            </Button>)
        }
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
          onClick={() => {
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
      render: (_:any, record:ApprovalBasicInfo) => renderInfo(record)
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
        onFinish={handleModalSubmit}
      />
      <TaskInfoModal
        open = {taskInfoOpen}
        onCancel={() => {setTaskInfoOpen(false)}}
      />
      <ProTable<ApprovalBasicInfo>
        search={{filterType: 'query'}}
        pagination={{pageSize: 20, size: 'small'}}
        options={false}
        rowKey={(record) => record.id}
        loading={approvalListState.loading}
        dataSource={approvalListState.approvalList}
        columns={approvalColumns}
        request={(params, sorter, filter: any) =>
          queryList(API_CONSTANTS.ROLE, { ...params, sorter, filter })
        }
      />
    </>
  );
}

export default ApprovalTable;


