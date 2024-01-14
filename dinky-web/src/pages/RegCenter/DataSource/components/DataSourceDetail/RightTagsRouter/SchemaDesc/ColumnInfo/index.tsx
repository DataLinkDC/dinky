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

import { DataSources } from '@/types/RegCenter/data';
import { transformTreeData } from '@/utils/function';
import { l } from '@/utils/intl';
import { CheckSquareOutlined, KeyOutlined } from '@ant-design/icons';
import { ProTable } from '@ant-design/pro-components';
import { ProColumns } from '@ant-design/pro-table/es/typing';
import React from 'react';

type ColumnInfoProps = {
  columnInfo: Partial<DataSources.Column[]>;
};

const ColumnInfo: React.FC<ColumnInfoProps> = (props) => {
  const { columnInfo } = props;

  const columns: ProColumns<DataSources.Column>[] = [
    {
      title: l('rc.ds.no'),
      dataIndex: 'position',
      width: '4%'
    },
    {
      title: l('rc.ds.columnName'),
      dataIndex: 'name',
      width: '10%',
      ellipsis: true
    },
    {
      title: l('rc.ds.columnType'),
      dataIndex: 'type',
      width: '6%'
    },
    {
      title: l('rc.ds.primarykey'),
      dataIndex: 'keyFlag',
      width: '4%',
      render: (_, record) => {
        return record.keyFlag ? <KeyOutlined style={{ color: '#FAA100' }} /> : undefined;
      }
    },
    {
      title: l('rc.ds.autoIncrement'),
      dataIndex: 'autoIncrement',
      width: '4%',
      render: (_, record) => {
        return record.autoIncrement ? (
          <CheckSquareOutlined style={{ color: '#1296db' }} />
        ) : undefined;
      }
    },
    {
      title: l('rc.ds.isNull'),
      dataIndex: 'nullable',
      width: '4%',
      render: (_, record) => {
        return !record.nullable ? <CheckSquareOutlined style={{ color: '#1296db' }} /> : undefined;
      }
    },
    {
      title: l('rc.ds.default'),
      dataIndex: 'defaultValue',
      ellipsis: true,
      width: '8%'
    },
    {
      title: l('rc.ds.length'),
      dataIndex: 'length',
      width: '6%'
    },
    {
      title: l('rc.ds.precision'),
      dataIndex: 'precision',
      width: '4%'
    },
    {
      title: l('rc.ds.decimalDigits'),
      dataIndex: 'scale',
      ellipsis: true,
      width: '6%'
    },
    {
      title: l('rc.ds.character'),
      dataIndex: 'characterSet',
      width: '6%',
      ellipsis: true
    },
    {
      title: l('rc.ds.collationRule'),
      dataIndex: 'collation',
      width: '10%',
      ellipsis: true
    },
    {
      title: l('rc.ds.javaType'),
      dataIndex: 'javaType',
      ellipsis: true,
      width: '8%'
    },
    {
      title: l('rc.ds.comment'),
      dataIndex: 'comment',
      ellipsis: true
    }
  ];

  return (
    <>
      <ProTable<DataSources.Column>
        toolBarRender={false}
        pagination={{
          defaultPageSize: 14,
          hideOnSinglePage: true
        }}
        search={false}
        options={false}
        size={'small'}
        bordered
        columns={columns}
        dataSource={transformTreeData(columnInfo) as DataSources.Column[]}
      />
    </>
  );
};

export default ColumnInfo;
