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


import React from "react";
import {CheckSquareOutlined, KeyOutlined} from '@ant-design/icons';
import DTable from "@/components/Common/DTable";
import {useIntl} from 'umi';

const Columns = (props: any) => {

  const l = (key: string, defaultMsg?: string) => useIntl().formatMessage({id: key, defaultMessage: defaultMsg})


  const intl = useIntl();

  const {dbId, table, schema, scroll} = props;

  const cols = [{
    title: intl.formatMessage({id: 'global.table.no', defaultMessage: '序号',}),
    dataIndex: 'position',
    isString: false,
  },
    {
      title: intl.formatMessage({id: 'global.table.columnName', defaultMessage: '字段名称',}),
      dataIndex: 'name',
      copyable: true,
    },
    {
      title: intl.formatMessage({id: 'global.table.annotation', defaultMessage: '注释',}),
      dataIndex: 'comment',
      // ellipsis: true,
    },
    {
      title: intl.formatMessage({id: 'global.table.type', defaultMessage: '类型',}),
      dataIndex: 'type',
    },
    {
      title: intl.formatMessage({id: 'global.table.primarykey', defaultMessage: '主键',}),
      dataIndex: 'keyFlag',
      render: (_, record) => (
        <>
          {record.keyFlag ? <KeyOutlined style={{color: '#FAA100'}}/> : undefined}
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
    }, {
      title: intl.formatMessage({id: 'global.table.automationAdd', defaultMessage: '自增',}),
      dataIndex: 'autoIncrement',
      render: (_, record) => (
        <>
          {record.autoIncrement ? <CheckSquareOutlined style={{color: '#1296db'}}/> : undefined}
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
    }, {
      title: intl.formatMessage({id: 'global.table.isNull', defaultMessage: '非空',}),
      dataIndex: 'nullable',
      render: (_, record) => (
        <>
          {!record.nullable ? <CheckSquareOutlined style={{color: '#1296db'}}/> : undefined}
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
    }, {
      title: intl.formatMessage({id: 'global.table.default', defaultMessage: '默认值',}),
      dataIndex: 'defaultValue',
    }, {
      title: intl.formatMessage({id: 'global.table.precision', defaultMessage: '精度',}),
      dataIndex: 'precision',
      isString: false,
    }, {
      title: intl.formatMessage({id: 'global.table.decimalDigits', defaultMessage: '小数范围',}),
      dataIndex: 'scale',
      isString: false,
    }, {
      title: intl.formatMessage({id: 'global.table.character', defaultMessage: '字符集',}),
      dataIndex: 'characterSet',
    }, {
      title: intl.formatMessage({id: 'global.table.collationRule', defaultMessage: '排序规则',}),
      dataIndex: 'collation',
    }, {
      title: intl.formatMessage({id: 'global.table.javaType', defaultMessage: 'Java 类型',}),
      dataIndex: 'javaType',
    },]
  return (
    <DTable columns={cols}
            scroll={scroll}
            dataSource={{url: 'api/database/listColumns', params: {id: dbId, schemaName: schema, tableName: table}}}/>
  )
};

export default Columns;
