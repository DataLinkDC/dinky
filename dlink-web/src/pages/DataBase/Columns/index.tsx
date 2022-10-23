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

  const intl = useIntl();
  const l = (id: string, defaultMessage?: string, value?: {}) => intl.formatMessage({id, defaultMessage}, value);


  const {dbId, table, schema, scroll} = props;

  const cols = [{
    title: l('global.table.no'),
    dataIndex: 'position',
    isString: false,
  },
    {
      title: l('global.table.columnName'),
      dataIndex: 'name',
      copyable: true,
    },
    {
      title: l('global.table.annotation'),
      dataIndex: 'comment',
      // ellipsis: true,
    },
    {
      title: l('global.table.type'),
      dataIndex: 'type',
    },
    {
      title: l('global.table.primarykey'),
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
      title: l('global.table.automationAdd'),
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
      title: l('global.table.isNull'),
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
      title: l('global.table.default'),
      dataIndex: 'defaultValue',
    }, {
      title: l('global.table.precision'),
      dataIndex: 'precision',
      isString: false,
    }, {
      title: l('global.table.decimalDigits'),
      dataIndex: 'scale',
      isString: false,
    }, {
      title: l('global.table.character'),
      dataIndex: 'characterSet',
    }, {
      title: l('global.table.collationRule'),
      dataIndex: 'collation',
    }, {
      title: l('global.table.javaType'),
      dataIndex: 'javaType',
    },]
  return (
    <DTable columns={cols}
            scroll={scroll}
            dataSource={{url: 'api/database/listColumns', params: {id: dbId, schemaName: schema, tableName: table}}}/>
  )
};

export default Columns;
