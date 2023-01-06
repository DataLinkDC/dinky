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


import {Drawer, Input} from 'antd';
import React, {useRef, useState} from 'react';
import type {ActionType, ProColumns} from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import ProDescriptions from '@ant-design/pro-descriptions';

import type {DocumentTableListItem} from '@/pages/RegistrationCenter/data';

import {queryData,} from "@/components/Common/crud";
import {l} from "@/utils/intl";

const url = '/api/document';

const StudioFX = () => {

  const actionRef = useRef<ActionType>();
  const [row, setRow] = useState<DocumentTableListItem>();
  const columns: ProColumns<DocumentTableListItem>[] = [
    {
      title: '函数',
      dataIndex: 'name',
      sorter: true,
      width: '400px',
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '文档ID',
      dataIndex: 'id',
      hideInTable: true,
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: '文档类型',
      sorter: true,
      dataIndex: 'category',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
      filters: [
        {
          text: '函数',
          value: 'function',
        }
      ],
      filterMultiple: false,
      valueEnum: {
        'function': {text: '函数'},
      },
    },
    {
      title: '类型',
      sorter: true,
      dataIndex: 'type',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: false,
      filters: [
        {
          text: '内置函数',
          value: '内置函数',
        },
        {
          text: 'UDF',
          value: 'UDF',
        },
      ],
      filterMultiple: false,
      valueEnum: {
        '内置函数': {text: '内置函数'},
        'UDF': {text: 'UDF'},
      },
    },
    {
      title: '子类型',
      sorter: true,
      dataIndex: 'subtype',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: false,
      filters: [
        {
          text: '比较函数',
          value: '比较函数',
        },
        {
          text: '逻辑函数',
          value: '逻辑函数',
        }, {
          text: '算术函数',
          value: '算术函数',
        }, {
          text: '字符串函数',
          value: '字符串函数',
        }, {
          text: '时间函数',
          value: '时间函数',
        }, {
          text: '条件函数',
          value: '条件函数',
        }, {
          text: '类型转换函数',
          value: '类型转换函数',
        }, {
          text: 'Collection 函数',
          value: 'Collection 函数',
        }, {
          text: 'Value Collection 函数',
          value: 'Value Collection 函数',
        }, {
          text: 'Value Access 函数',
          value: 'Value Access 函数',
        }, {
          text: '分组函数',
          value: '分组函数',
        }, {
          text: 'hash函数',
          value: 'hash函数',
        }, {
          text: '聚合函数',
          value: '聚合函数',
        }, {
          text: '列函数',
          value: '列函数',
        }, {
          text: '表值聚合函数',
          value: '表值聚合函数',
        }, {
          text: '其他函数',
          value: '其他函数',
        },
      ],
      filterMultiple: false,
      valueEnum: {
        '比较函数': {text: '比较函数'},
        '逻辑函数': {text: '逻辑函数'},
        '算术函数': {text: '算术函数'},
        '字符串函数': {text: '字符串函数'},
        '时间函数': {text: '时间函数'},
        '条件函数': {text: '条件函数'},
        '类型转换函数': {text: '类型转换函数'},
        'Collection 函数': {text: 'Collection 函数'},
        'Value Collection 函数': {text: 'Value Collection 函数'},
        'Value Access 函数': {text: 'Value Access 函数'},
        '分组函数': {text: '分组函数'},
        'hash函数': {text: 'hash函数'},
        '聚合函数': {text: '聚合函数'},
        '列函数': {text: '列函数'},
        '表值聚合函数': {text: '表值聚合函数'},
        '其他函数': {text: '其他函数'},
      },
    },
    {
      title: '描述',
      sorter: true,
      dataIndex: 'description',
      valueType: 'textarea',
      hideInForm: false,
      hideInSearch: false,
      hideInTable: false,
      width: '400px',
    },
    {
      title: '版本',
      sorter: true,
      dataIndex: 'version',
      hideInForm: true,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: '赞',
      sorter: true,
      dataIndex: 'likeNum',
      hideInForm: true,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: l('global.table.isEnable'),
      dataIndex: 'enabled',
      hideInForm: true,
      hideInSearch: true,
      hideInTable: true,
      filters: [
        {
          text: l('status.enabled'),
          value: 1,
        },
        {
          text: l('status.disabled'),
          value: 0,
        },
      ],
      filterMultiple: false,
      valueEnum: {
        true: {text: l('status.enabled'), status: 'Success'},
        false: {text: l('status.disabled'), status: 'Error'},
      },
    },
    {
      title: l('global.table.createTime'),
      dataIndex: 'createTime',
      sorter: true,
      valueType: 'dateTime',
      hideInForm: true,
      hideInTable: true,
      hideInSearch: true,
      renderFormItem: (item, {defaultRender, ...rest}, form) => {
        const status = form.getFieldValue('status');
        if (`${status}` === '0') {
          return false;
        }
        if (`${status}` === '3') {
          return <Input {...rest} placeholder="请输入异常原因！"/>;
        }
        return defaultRender(item);
      },
    },
    {
      title: l('global.table.lastUpdateTime'),
      dataIndex: 'updateTime',
      sorter: true,
      valueType: 'dateTime',
      hideInForm: true,
      hideInTable: true,
      hideInSearch: true,
      renderFormItem: (item, {defaultRender, ...rest}, form) => {
        const status = form.getFieldValue('status');
        if (`${status}` === '0') {
          return false;
        }
        if (`${status}` === '3') {
          return <Input {...rest} placeholder="请输入异常原因！"/>;
        }
        return defaultRender(item);
      },
    },
  ];

  return (
    <>
      <ProTable<DocumentTableListItem>
        headerTitle="FlinkSql 函数浏览"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 120,
        }}
        request={(params, sorter, filter) => queryData(url, {...params, sorter, filter})}
        columns={columns}
        pagination={{
          defaultPageSize: 5,
          showSizeChanger: true,
        }}
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
          <ProDescriptions<DocumentTableListItem>
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
    </>);
};

export default StudioFX;
