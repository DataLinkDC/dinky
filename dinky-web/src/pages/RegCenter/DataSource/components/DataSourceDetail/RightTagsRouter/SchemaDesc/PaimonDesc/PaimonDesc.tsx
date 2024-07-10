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
