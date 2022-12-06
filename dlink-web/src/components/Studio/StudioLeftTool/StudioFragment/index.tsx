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

import React, {useState} from "react";
import {ProColumns, ProTable} from "@ant-design/pro-table";
import {Drawer} from "antd";
import ProDescriptions from '@ant-design/pro-descriptions';
import {queryData} from "@/components/Common/crud";
import {FragmentVariableTableListItem} from "@/pages/RegistrationCenter/data";
import {l} from "@/utils/intl";

const StudioFragment = (props: any) => {


  const {toolHeight, dispatch} = props;
  const [row, setRow] = useState<{}>();

  const url = "/api/fragment"


  const getColumns: ProColumns<FragmentVariableTableListItem>[] = [
    {
      title: '名称',
      dataIndex: 'name',
      tip: '名称是唯一的',
      sorter: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '引用名称',
      copyable: true,
      render: (dom, entity) => {
        return <>
          ${"{" + entity?.name + "}"}
        </>;
      },
    },
    {
      title: '变量ID',
      dataIndex: 'id',
      hideInTable: true,
      hideInForm: true,
      hideInSearch: true,
    },
    {
      title: '别名',
      dataIndex: 'alias',
      hideInForm: false,
      hideInSearch: false,
      hideInTable: true,
    },
    {
      title: '变量值',
      sorter: true,
      dataIndex: 'fragmentValue',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: '使用变量值',
      sorter: true,
      dataIndex: 'fillValue',
      hideInForm: false,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: '描述',
      sorter: true,
      dataIndex: 'note',
      valueType: 'textarea',
      hideInForm: false,
      hideInSearch: false,
      hideInTable: true,
    },
    {
      title: l('global.table.isEnable'),
      dataIndex: 'enabled',
      hideInForm: false,
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
    },
    {
      title: l('global.table.lastUpdateTime'),
      dataIndex: 'updateTime',
      sorter: true,
      valueType: 'dateTime',
      hideInForm: true,
      hideInTable: true,
    },
  ];

  return (
    <>
      <ProTable<FragmentVariableTableListItem>
        columns={getColumns}
        style={{width: '100%'}}
        request={(params, sorter, filter) => queryData(url, {params, sorter, filter})}
        pagination={{
          defaultPageSize: 10,
          showSizeChanger: true,
        }}
        search={false}
        size="small"

      />
      <Drawer
        width={600}
        visible={!!row?.name}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        <ProDescriptions
          column={1}
          title={row?.name}
          request={async () => ({
            data: row || {},
          })}
          params={{
            name: row?.name,
          }}
          columns={getColumns}
        />
      </Drawer>
    </>
  );
};

export default StudioFragment;
