import React, {useRef, useState} from "react";
import {DownOutlined, HeartOutlined, PlusOutlined, UserOutlined} from '@ant-design/icons';
import {ActionType, ProColumns} from "@ant-design/pro-table";
import {Drawer} from 'antd';
import {PageContainer} from '@ant-design/pro-layout';
import ProTable from '@ant-design/pro-table';
import ProDescriptions from '@ant-design/pro-descriptions';
import {queryData} from "@/components/Common/crud";
import {SavePointTableListItem} from "@/components/Studio/StudioRightTool/StudioSavePoint/data";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";

const url = '/api/savepoints';
const StudioSavePoint: React.FC<{}> = (props: any) => {
  const {current,dispatch} = props;
  const [row, setRow] = useState<SavePointTableListItem>();
  const actionRef = useRef<ActionType>();

  const columns: ProColumns<SavePointTableListItem>[] = [
    {
      title: '名称',
      dataIndex: 'name',
      sorter: true,
      hideInTable: true,
      hideInForm: true,
      hideInSearch: true,
      /*render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },*/
    },
    {
      title: 'id',
      dataIndex: 'id',
      hideInTable: true,
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: '作业ID',
      dataIndex: 'taskId',
      hideInTable: true,
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: '类型',
      dataIndex: 'type',
      hideInTable: true,
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: '路径',
      dataIndex: 'path',
      hideInTable: true,
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
  ];

  return (
    <PageContainer>
      <ProTable<SavePointTableListItem>
        actionRef={actionRef}
        rowKey="id"
        request={(params, sorter, filter) => queryData(url, {taskId:current.taskId,...params, sorter, filter})}
        columns={columns}
        />
        <Drawer
          width={600}
          visible={!!row}
          onClose={() => {
            setRow(undefined);
          }}
          closable={false}
        >
          {row?.name && (
            <ProDescriptions<SavePointTableListItem>
              column={2}
              title={row?.name}
              request={async () => ({
              data: row || {},
            })}
              params={{
              id: row?.name,
            }}
              columns={columns}
              />
              )}
        </Drawer>
    </PageContainer>
);
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
}))(StudioSavePoint);
