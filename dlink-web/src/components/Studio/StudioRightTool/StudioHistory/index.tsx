import {useRef, useState} from "react";
import {MinusSquareOutlined} from '@ant-design/icons';
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {Button, Col, Drawer, Modal, Row, Space, Tooltip} from 'antd';
import ProDescriptions from '@ant-design/pro-descriptions';
import {queryData, handleOption} from "@/components/Common/crud";
import {
  TaskHistoryTableListItem
} from "@/components/Studio/StudioRightTool/StudioHistory/data";
import {StateType} from "@/pages/DataStudio/model";
import {connect} from "umi";
import {Scrollbars} from 'react-custom-scrollbars';

const url = '/api/task/version';
const StudioHistory = (props: any) => {
  const {current, toolHeight, dispatch} = props;
  const [row, setRow] = useState<TaskHistoryTableListItem>();
  const actionRef = useRef<ActionType>();

  if (current.key) {
    actionRef.current?.reloadAndRest?.();
  }

  const columns: ProColumns<TaskHistoryTableListItem>[] = [
    // {
    //   title: 'id',
    //   dataIndex: 'id',
    //   hideInForm: false,
    //   hideInSearch: false,
    // },
    {
      title: '版本ID',
      dataIndex: 'versionId',
      sorter: true,
      hideInForm: true,
      hideInSearch: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: '操作',
      valueType: 'option',
      align: "center",
      render: (text, record, index) => (
        <Space size="middle">
          <Button type="link" onClick={() => onRollBackVersion(record)}>回滚</Button>
        </Space>
      )
    },
  ];


  const onRollBackVersion = (row: TaskHistoryTableListItem) => {
    Modal.confirm({
      title: '回滚Flink SQL版本',
      content: `确定回滚Flink SQL版本至【${row.versionId === "" ? row.versionId : row.versionId}】吗？`,
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        const TaskHistoryRollbackItem = {
          id: current.key, versionId: row.versionId
        }
        await handleOption('api/task/rollbackTask', "回滚Flink SQL版本", TaskHistoryRollbackItem);
        actionRef.current?.reloadAndRest?.();
      }
    });
  };

  return (
    <>
      <Row>
        <Col span={24}>
          <div style={{float: "right"}}>
            <Tooltip title="最小化">
              <Button
                type="text"
                icon={<MinusSquareOutlined/>}
              />
            </Tooltip>
          </div>
        </Col>
      </Row>
      <Scrollbars style={{height: (toolHeight - 32)}}>
        <ProTable<TaskHistoryTableListItem>
          actionRef={actionRef}
          rowKey="id"
          request={(params, sorter, filter) => queryData(url, {taskId: current.key, ...params, sorter, filter})}
          columns={columns}
          search={false}
        />
        <Drawer
          width={600}
          visible={!!row}
          onClose={() => {
            setRow(undefined);
          }}
          closable={false}
        >
          {row?.versionId && (
            <ProDescriptions<TaskHistoryTableListItem>
              column={2}
              title={row?.versionId}
              request={async () => ({
                data: row || {},
              })}
              params={{
                id: row?.versionId,
              }}
              columns={columns}
            />
          )}
        </Drawer>
      </Scrollbars>
    </>
  );
};

export default connect(({Studio}: { Studio: StateType }) => ({
  current: Studio.current,
  toolHeight: Studio.toolHeight,
}))(StudioHistory);
