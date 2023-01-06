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
    title:  l('pages.flinkColumns.position'),
    dataIndex: 'position',
    isString: false,
  },
    {
      title: l('pages.flinkColumns.name'),
      dataIndex: 'name',
      copyable: true,
    },
    {
      title: l('pages.flinkColumns.type'),
      dataIndex: 'type',
    },
    {
      title: l('pages.flinkColumns.key.true'),
      dataIndex: 'key',
      render: (_, record) => (
        <>
          {record.key ? <KeyOutlined style={{color: '#FAA100'}}/> : undefined}
        </>
      ),
      filters: [
        {
          text: l('pages.flinkColumns.key.true'),
          value: true,
        },
        {
          text: l('pages.flinkColumns.key.other'),
          value: '',
        },
      ],
      openSearch: 'dict',
    }, {
      title: l('pages.flinkColumns.isnull'),
      dataIndex: 'nullable',
      render: (_, record) => (
        <>
          {record.nullable ? <CheckSquareOutlined style={{color: '#1296db'}}/> : undefined}
        </>
      ),
      filters: [
        {
          text: l('pages.flinkColumns.isnull'),
          value: true,
        },
        {
          text: l('pages.flinkColumns.isnotnull'),
          value: '',
        },
      ],
      openSearch: 'dict',
    }, {
      title: l('pages.flinkColumns.extras'),
      dataIndex: 'extras',
    }, {
      title: l('pages.flinkColumns.watermark'),
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
