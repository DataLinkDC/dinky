import React from "react";
import { Button, Tooltip } from 'antd';
import { QuestionCircleOutlined } from '@ant-design/icons';
import type { ProColumns } from '@ant-design/pro-table';
import ProTable, { TableDropdown } from '@ant-design/pro-table';
import { Column } from "./data";
import {getData, queryData} from "@/components/Common/crud";



const Columns = (props: any) => {

  const {dbId,table,schema} = props;

  const columns: ProColumns<Column>[] = [
    {
      title: '列名',
      dataIndex: 'name',
      render: (_) => <a>{_}</a>,
      /*formItemProps: {
        lightProps: {
          labelFormatter: (value) => `app-${value}`,
        },
      },*/
    },
    {
      title: '注释',
      dataIndex: 'comment',
    },
    {
      title: '类型',
      dataIndex: 'type',
    },
    {
      title: '主键',
      dataIndex: 'keyFlag',
    },{
      title: '自增',
      dataIndex: 'keyIdentityFlag',
    },{
      title: '默认值',
      dataIndex: 'fill',
    },{
      title: '非空',
      dataIndex: 'isNotNull',
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
      }
      }
      rowKey="name"
      pagination={{
        pageSize: 10,
      }}
      search={{
        filterType: 'light',
      }}
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
