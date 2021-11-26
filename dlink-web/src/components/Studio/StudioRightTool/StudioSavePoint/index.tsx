import React, {useRef, useState} from "react";
import {MinusSquareOutlined} from '@ant-design/icons';
import {ActionType, ProColumns} from "@ant-design/pro-table";
import {Drawer,Row,Col,Tooltip,Button} from 'antd';
import ProTable from '@ant-design/pro-table';
import ProDescriptions from '@ant-design/pro-descriptions';
import {queryData} from "@/components/Common/crud";
import {SavePointTableListItem} from "@/components/Studio/StudioRightTool/StudioSavePoint/data";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import { Scrollbars } from 'react-custom-scrollbars';

const url = '/api/savepoints';
const StudioSavePoint: React.FC<{}> = (props: any) => {
  const {current,toolHeight,dispatch} = props;
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
      hideInForm: true,
      hideInSearch: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
  ];

  return (
    <>
      <Row>
        <Col span={24}>
          <div style={{float: "right"}}>
            <Tooltip title="最小化">
              <Button
                type="text"
                icon={<MinusSquareOutlined />}
              />
            </Tooltip>
          </div>
        </Col>
      </Row>
      <Scrollbars  style={{height:(toolHeight-32)}}>
      <ProTable<SavePointTableListItem>
        actionRef={actionRef}
        rowKey="id"
        request={(params, sorter, filter) => queryData(url, {taskId:current.key,...params, sorter, filter})}
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
      </Scrollbars>
    </>
);
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
  toolHeight: Studio.toolHeight,
}))(StudioSavePoint);
