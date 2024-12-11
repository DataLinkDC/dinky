import {ApprovalBasicInfo} from "@/types/ApprovalCenter/data";
import {useRef, useState} from "react";
import {ApprovalListState} from "@/types/ApprovalCenter/state.d";
import {InitApprovalList} from "@/types/ApprovalCenter/init.d";
import {ActionType, ProColumns} from "@ant-design/pro-table";
import {useAccess} from "@/hooks/useAccess";
import {Button, Flex, Tag} from "antd";
import {l} from "@/utils/intl";
import {ProTable} from "@ant-design/pro-components";
import ApprovalModal from "@/pages/ApprovalCenter/TaskApproval/components/ApprovalModal";


type UserFormProps = {
  tableType: 'review' | 'submit'
}

const ApprovalTable: React.FC<UserFormProps> = (props) => {
  // TODO get from backend
  // generate test cases
  const testApprovalList: ApprovalBasicInfo[] = [];
  {
    const createStatusApproval: ApprovalBasicInfo = {
      id: 11,
      taskId: 1,
      previousTaskVersion: 1,
      currentTaskVersion: 2,
      status: 'CREATED',
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
      status: 'SUBMITTED',
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
      status: 'APPROVED',
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
      status: 'REJECTED',
      submitterName: 'test',
      reviewerName: 'test',
      submitterComment: 'Comment',
      reviewerComment: 'Comment',
      createTime: '2024-12-11',
      updateTime: '2024-12-11'
    };
    testApprovalList.push(rejectedStatusApproval);
  }

  const [approvalListState, setApprovalListState] = useState<ApprovalListState>({
    ...InitApprovalList,
    approvalList: testApprovalList
  });
  const actionRef = useRef<ActionType>(); // table action
  const access = useAccess(); // access control

  const executeAndCallbackRefresh = async (callback: () => void) => {
    setApprovalListState((prevState) => ({...prevState, loading: true}));
    await callback();
    setApprovalListState((prevState) => ({...prevState, loading: false}));
    actionRef.current?.reload?.();
  };

  const handleApprovalOperation = (operation: 'submit' | 'withdraw' | 'approve' | 'reject') => {
    setApprovalListState((prevState) => ({...prevState, modalViable: true}))
  };

  /**
   * render operation based on current state
   * @param entity entity
   */
  const renderOperation = (entity: ApprovalBasicInfo) => {
    const buttons = [];
    switch (entity.status) {
      case 'CREATED':
      case 'WITHDRAWN':
        buttons.push(
          <Button
            size={'small'}
            type={'primary'}
            onClick={() => {
              handleApprovalOperation('submit');
            }}
          >
            {l('approval.operation.submit')}
          </Button>
        );
        break;
      case 'SUBMITTED':
        buttons.push(
          <Button
            size={'small'}
            type={'primary'}
            onClick={() => {
              handleApprovalOperation('approve');
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
              handleApprovalOperation('reject');
            }}
            danger
          >
            {l('approval.operation.reject')}
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
      <ApprovalModal viable={approvalListState.modalViable}/>
      <ProTable<ApprovalBasicInfo>
        search={{filterType: 'query'}}
        pagination={{ pageSize: 20, size: 'small'}}
        options={false}
        rowKey={(record) => record.id}
        loading={approvalListState.loading}
        dataSource={approvalListState.approvalList}
        columns={approvalColumns}
      />
    </>
  );
}

export default ApprovalTable;


