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
import {DIALECT} from "@/components/Studio/conf";
import {l} from "@/utils/intl";

const FlinkColumns = (props: any) => {
  const {envId, catalog, database, table} = props;

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
      title: '类型',
      dataIndex: 'type',
    },
    {
      title: '主键',
      dataIndex: 'key',
      render: (_, record) => (
        <>
          {record.key ? <KeyOutlined style={{color: '#FAA100'}}/> : undefined}
        </>
      ),
      filters: [
        {
          text: '主键',
          value: true,
        },
        {
          text: '其他',
          value: '',
        },
      ],
      openSearch: 'dict',
    }, {
      title: '可为空',
      dataIndex: 'nullable',
      render: (_, record) => (
        <>
          {record.nullable ? <CheckSquareOutlined style={{color: '#1296db'}}/> : undefined}
        </>
      ),
      filters: [
        {
          text: '可为空',
          value: true,
        },
        {
          text: '非空',
          value: '',
        },
      ],
      openSearch: 'dict',
    }, {
      title: '扩展',
      dataIndex: 'extras',
    }, {
      title: '水印',
      dataIndex: 'watermark',
    },];

  return (
    <DTable columns={cols}
            dataSource={{
              url: 'api/studio/getMSFlinkColumns', params: {
                envId,
                dialect: DIALECT.FLINKSQL,
                catalog,
                database,
                table
              }
            }}/>
  )
};

export default FlinkColumns;
