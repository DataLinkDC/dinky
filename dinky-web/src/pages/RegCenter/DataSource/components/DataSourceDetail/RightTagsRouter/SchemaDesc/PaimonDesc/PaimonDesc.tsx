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

import React from 'react';
import { Tabs, TabsProps } from 'antd';
import ColumnInfo from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SchemaDesc/ColumnInfo';
import SQLQuery from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SQLQuery';
import { DataSources } from '@/types/RegCenter/data';

const PaimonDesc: React.FC<DataSources.SchemaDescProps> = (props) => {
  const { tableInfo, queryParams } = props;

  const getBottomItems = () => {
    const items: TabsProps['items'] = [];
    items.push({
      key: 'Columns',
      label: 'ColumnInfo',
      children: <ColumnInfo columnInfo={tableInfo?.columns} />
    });
    if (!queryParams) {
      return;
    }
    items.push({
      key: 'Partition',
      label: 'Partation',
      children: (
        <SQLQuery
          hidlenFilter={true}
          queryParams={{
            id: queryParams.id,
            schemaName: queryParams.schemaName,
            tableName: queryParams.tableName + '$partitions'
          }}
        />
      )
    });
    items.push({
      key: 'snapshots',
      label: 'Snapshots',
      children: (
        <SQLQuery
          hidlenFilter={true}
          queryParams={{
            id: queryParams.id,
            schemaName: queryParams.schemaName,
            tableName: queryParams.tableName + '$snapshots'
          }}
        />
      )
    });
    items.push({
      key: 'schemas',
      label: 'Schema History',
      children: (
        <SQLQuery
          hidlenFilter={true}
          queryParams={{
            id: queryParams.id,
            schemaName: queryParams.schemaName,
            tableName: queryParams.tableName + '$schemas'
          }}
        />
      )
    });
    items.push({
      key: 'options',
      label: 'Table options',
      children: (
        <SQLQuery
          hidlenFilter={true}
          queryParams={{
            id: queryParams.id,
            schemaName: queryParams.schemaName,
            tableName: queryParams.tableName + '$options'
          }}
        />
      )
    });
    items.push({
      key: 'audit_log',
      label: 'AuditLog',
      children: (
        <SQLQuery
          hidlenFilter={true}
          queryParams={{
            id: queryParams.id,
            schemaName: queryParams.schemaName,
            tableName: queryParams.tableName + '$audit_log'
          }}
        />
      )
    });
    items.push({
      key: 'files',
      label: 'Files List',
      children: (
        <SQLQuery
          hidlenFilter={true}
          queryParams={{
            id: queryParams.id,
            schemaName: queryParams.schemaName,
            tableName: queryParams.tableName + '$files'
          }}
        />
      )
    });
    items.push({
      key: 'tags',
      label: 'Tags',
      children: (
        <SQLQuery
          hidlenFilter={true}
          queryParams={{
            id: queryParams.id,
            schemaName: queryParams.schemaName,
            tableName: queryParams.tableName + '$tags'
          }}
        />
      )
    });
    items.push({
      key: 'consumers',
      label: 'consumers',
      children: (
        <SQLQuery
          hidlenFilter={true}
          queryParams={{
            id: queryParams.id,
            schemaName: queryParams.schemaName,
            tableName: queryParams.tableName + '$consumers'
          }}
        />
      )
    });
    items.push({
      key: 'manifests',
      label: 'Manifests Files',
      children: (
        <SQLQuery
          hidlenFilter={true}
          queryParams={{
            id: queryParams.id,
            schemaName: queryParams.schemaName,
            tableName: queryParams.tableName + '$manifests'
          }}
        />
      )
    });

    return items;
  };

  return <Tabs type={'card'} items={getBottomItems()} />;
};

export default PaimonDesc;
