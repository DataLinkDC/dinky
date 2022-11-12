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
import {l} from "@/utils/intl";

const Columns = (props: any) => {

  const {dbId, table, schema, scroll} = props;

  const cols = [{
    title: l('pages.registerCenter.db.no'),
    dataIndex: 'position',
    isString: false,
  },
    {
      title: l('pages.registerCenter.db.columnName'),
      dataIndex: 'name',
      copyable: true,
    },
    {
      title: l('pages.registerCenter.db.comment'),
      dataIndex: 'comment',
      // ellipsis: true,
    },
    {
      title: l('pages.registerCenter.db.type'),
      dataIndex: 'type',
    },
    {
      title: l('pages.registerCenter.db.primarykey'),
      dataIndex: 'keyFlag',
      render: (_, record) => (
        <>
          {record.keyFlag ? <KeyOutlined style={{color: '#FAA100'}}/> : undefined}
        </>
      ),
      filters: [
        {
          text: l('pages.registerCenter.db.primarykey'),
          value: true,
        },
        {
          text: l('pages.registerCenter.db.other'),
          value: false,
        },
      ],
      openSearch: 'dict',
    }, {
      title: l('pages.registerCenter.db.autoIncrement'),
      dataIndex: 'autoIncrement',
      render: (_, record) => (
        <>
          {record.autoIncrement ? <CheckSquareOutlined style={{color: '#1296db'}}/> : undefined}
        </>
      ),
      filters: [
        {
          text: l('pages.registerCenter.db.autoIncrement'),
          value: true,
        },
        {
          text: l('pages.registerCenter.db.other'),
          value: false,
        },
      ],
      openSearch: 'dict',
    }, {
      title: l('pages.registerCenter.db.isNull'),
      dataIndex: 'nullable',
      render: (_, record) => (
        <>
          {!record.nullable ? <CheckSquareOutlined style={{color: '#1296db'}}/> : undefined}
        </>
      ),
      filters: [
        {
          text: l('pages.registerCenter.db.isNull'),
          value: true,
        },
        {
          text: l('pages.registerCenter.db.null'),
          value: false,
        },
      ],
      openSearch: 'dict',
    }, {
      title: l('pages.registerCenter.db.default'),
      dataIndex: 'defaultValue',
    }, {
      title: l('pages.registerCenter.db.precision'),
      dataIndex: 'precision',
      isString: false,
    }, {
      title: l('pages.registerCenter.db.decimalDigits'),
      dataIndex: 'scale',
      isString: false,
    }, {
      title: l('pages.registerCenter.db.character'),
      dataIndex: 'characterSet',
    }, {
      title: l('pages.registerCenter.db.collationRule'),
      dataIndex: 'collation',
    }, {
      title: l('pages.registerCenter.db.javaType'),
      dataIndex: 'javaType',
    },]
  return (
    <DTable columns={cols}
            scroll={scroll}
            dataSource={{url: 'api/database/listColumns', params: {id: dbId, schemaName: schema, tableName: table}}}/>
  )
};

export default Columns;
