import React from "react";
import { Button, Tooltip } from 'antd';
import { KeyOutlined,CheckSquareOutlined } from '@ant-design/icons';
import type { ProColumns } from '@ant-design/pro-table';
import ProTable, { TableDropdown } from '@ant-design/pro-table';
import { Column } from "../data";
import {getData, queryData} from "@/components/Common/crud";



const Columns = (props: any) => {

  const {dbId,table,schema} = props;

  const columns: ProColumns<Column>[] = [
    {
      title: '序号',
      dataIndex: 'position',
      sorter: (a, b) => a.position - b.position,
    },
    {
      title: '列名',
      dataIndex: 'name',
      render: (_) => <a>{_}</a>,
      // sorter: (a, b) => a.name - b.name,
      copyable: true,
    },
    {
      title: '注释',
      dataIndex: 'comment',
      // ellipsis: true,
    },
    {
      title: '类型',
      dataIndex: 'type',
    },
    {
      title: '主键',
      dataIndex: 'keyFlag',
      render: (_, record) => (
        <>
          {record.keyFlag?<KeyOutlined style={{ color:'#FAA100'}} />:undefined}
        </>
      ),
    },{
      title: '自增',
      dataIndex: 'autoIncrement',
      render: (_, record) => (
        <>
          {record.autoIncrement?<CheckSquareOutlined style={{ color:'#1296db'}} />:undefined}
        </>
      ),
    },{
      title: '非空',
      dataIndex: 'nullable',
      render: (_, record) => (
        <>
          {!record.nullable?<CheckSquareOutlined style={{ color:'#1296db'}} />:undefined}
        </>
      ),
    },{
      title: '默认值',
      dataIndex: 'defaultValue',
    },{
      title: '精度',
      dataIndex: 'precision',
    },{
      title: '小数范围',
      dataIndex: 'scale',
    },{
      title: '字符集',
      dataIndex: 'characterSet',
    },{
      title: '排序规则',
      dataIndex: 'collation',
    },{
      title: 'Java 类型',
      dataIndex: 'javaType',
    },
    /*{
      title: '类型',
      dataIndex: 'type',
      valueType: 'select',
      valueEnum: {
        all: { text: '全部' },
        付小小: { text: '付小小' },
        曲丽丽: { text: '曲丽丽' },
        林东东: { text: '林东东' },
        陈帅帅: { text: '陈帅帅' },
        兼某某: { text: '兼某某' },
      },
    },*/
    /*{
      title: '操作',
      width: '164px',
      key: 'option',
      valueType: 'option',
      render: () => [
        <a key="link">链路</a>,
        <a key="link2">报警</a>,
        <a key="link3">监控</a>,
        <TableDropdown
          key="actionGroup"
          menus={[
            { key: 'copy', name: '复制' },
            { key: 'delete', name: '删除' },
          ]}
        />,
      ],
    },*/
  ];


  return (
    <ProTable<Column>
      columns={columns}
      style={{width: '100%'}}
      request={async() =>
      {
        const msg = await getData('api/database/listColumns', {id:dbId,schemaName:schema,tableName:table});
        return {
          data: msg.datas,
          success: msg.code===0,
        };
      }}
      rowKey="name"
      pagination={{
        pageSize: 10,
      }}
      /*search={{
        filterType: 'light',
      }}*/
      search={false}
      /*toolBarRender={() => [
        <Button key="show">查看日志</Button>,
        <Button type="primary" key="primary">
          创建应用
        </Button>,
      ]}*/
    />
  );
};

export default Columns;
