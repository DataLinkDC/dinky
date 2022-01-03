import React from "react";
import {KeyOutlined, CheckSquareOutlined} from '@ant-design/icons';
import DTable from "@/components/Common/DTable";

const Columns = (props: any) => {

  const {dbId,table,schema} = props;

  const cols = [{
    title: '序号',
    dataIndex: 'position',
    isString: false,
  },
    {
      title: '列名',
      dataIndex: 'name',
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
      filters: [
        {
          text: '主键',
          value: true,
        },
        {
          text: '其他',
          value: false,
        },
      ],
      openSearch: 'dict',
    },{
      title: '自增',
      dataIndex: 'autoIncrement',
      render: (_, record) => (
        <>
          {record.autoIncrement?<CheckSquareOutlined style={{ color:'#1296db'}} />:undefined}
        </>
      ),
      filters: [
        {
          text: '自增',
          value: true,
        },
        {
          text: '其他',
          value: false,
        },
      ],
      openSearch: 'dict',
    },{
      title: '非空',
      dataIndex: 'nullable',
      render: (_, record) => (
        <>
          {!record.nullable?<CheckSquareOutlined style={{ color:'#1296db'}} />:undefined}
        </>
      ),
      filters: [
        {
          text: '非空',
          value: true,
        },
        {
          text: '可为空',
          value: false,
        },
      ],
      openSearch: 'dict',
    },{
      title: '默认值',
      dataIndex: 'defaultValue',
    },{
      title: '精度',
      dataIndex: 'precision',
      isString: false,
    },{
      title: '小数范围',
      dataIndex: 'scale',
      isString: false,
    },{
      title: '字符集',
      dataIndex: 'characterSet',
    },{
      title: '排序规则',
      dataIndex: 'collation',
    },{
      title: 'Java 类型',
      dataIndex: 'javaType',
    },]
  return (
    <DTable columns={cols}
            dataSource={{url:'api/database/listColumns',params:{id:dbId,schemaName:schema,tableName:table}}}/>
  )
};

export default Columns;
