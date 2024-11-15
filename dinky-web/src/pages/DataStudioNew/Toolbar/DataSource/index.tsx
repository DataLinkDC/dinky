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

import SchemaTree from '@/pages/RegCenter/DataSource/components/DataSourceDetail/SchemaTree';
import DataSourceModal from '@/pages/RegCenter/DataSource/components/DataSourceModal';
import { handleTest, saveOrUpdateHandle } from '@/pages/RegCenter/DataSource/service';
import { DataSources } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { DatabaseOutlined, TableOutlined } from '@ant-design/icons';
import { Key, ProForm } from '@ant-design/pro-components';
import { CascaderProps, Spin, Tag } from 'antd';
import { memo, useEffect, useRef, useState } from 'react';
import { clearDataSourceTable, getDataSourceList, showDataSourceTable } from './service';
import { useAsyncEffect } from 'ahooks';
import { ProFormCascader } from '@ant-design/pro-form/lib';
import { CenterTab, DataStudioState } from '@/pages/DataStudioNew/model';
import { mapDispatchToProps } from '@/pages/DataStudioNew/DvaFunction';
import { connect } from '@umijs/max';
import { DataStudioActionType } from '@/pages/DataStudioNew/data.d';

interface Option {
  value: number | string;
  label: string;
  children?: Option[];
}

const DataSource = memo((props: any) => {
  const {
    addCenterTab,
    dataSourceDataList,
    action: { actionType, params }
  } = props;
  const [dbData, setDbData] = useState<Option[]>([]);
  const [selectedKeys, setSelectedKeys] = useState<Key[]>([]);
  const [expandKeys, setExpandKeys] = useState<Key[]>([]);
  const [selectDatabaseId, setSelectDatabaseId] = useState<number>();
  const [selectDbType, setSelectDbType] = useState<string>();

  const [treeData, setTreeData] = useState<[]>([]);
  const [isLoadingDatabase, setIsLoadingDatabase] = useState(false);
  const [showCreate, setShowCreate] = useState(false);
  const [treeHeight, setTreeHeight] = useState<number>(100);
  const ref = useRef<HTMLDivElement>(null);

  useAsyncEffect(async () => {
    switch (actionType) {
      case DataStudioActionType.DATASOURCE_CREATE:
        setShowCreate(true);
        break;
      case DataStudioActionType.DATASOURCE_REFRESH:
        if (!selectDatabaseId) return;
        setIsLoadingDatabase(true);
        await clearDataSourceTable(selectDatabaseId);
        await onChangeDataBase(selectDatabaseId);
        break;
    }
  }, [actionType, params]);

  /**
   * @description: 刷新树数据
   * @param {number} databaseId
   */
  const onRefreshTreeData = async (databaseId: number) => {
    if (!databaseId) {
      setIsLoadingDatabase(false);
      return;
    }

    setIsLoadingDatabase(true);
    const tables = (await showDataSourceTable(databaseId)) ?? [];
    setIsLoadingDatabase(false);

    for (let table of tables) {
      table.title = table.name;
      table.key = table.name;
      table.icon = <DatabaseOutlined />;
      table.children = table.tables;
      for (let child of table.children) {
        child.title = child.name;
        child.key = table.name + '.' + child.name;
        child.icon = <TableOutlined />;
        child.isLeaf = true;
        child.schema = table.name;
        child.table = child.name;
      }
    }
    setTreeData(tables);
  };

  useEffect(() => {
    // 监控布局宽度高度变化，重新计算树的高度
    const element = ref.current!!;
    const observer = new ResizeObserver((entries) => {
      if (entries?.length === 1) {
        // 这里节点理应为一个，减去的高度是为搜索栏的高度
        setTreeHeight(entries[0].contentRect.height - 55);
      }
    });
    observer.observe(element);
    return () => observer.unobserve(element);
  }, []);

  useAsyncEffect(async () => {
    const typeGroup: Record<string, DataSources.DataSource[]> = {};
    // 根据 type分组，做级联
    (dataSourceDataList as DataSources.DataSource[]).forEach((item) => {
      if (!typeGroup[item.type]) {
        typeGroup[item.type] = [];
      }
      typeGroup[item.type].push(item);
    });
    const options: Option[] = Object.keys(typeGroup).map((type) => ({
      label: type,
      value: type,
      children: typeGroup[type].map((item) => ({
        label: item.name,
        value: item.id
      }))
    }));
    setDbData(options);
  }, []);

  /**
   * 数据库选择改变时间时 刷新树数据
   * @param {number} value
   */
  const onChangeDataBase = async (value: number) => {
    await onRefreshTreeData(value);
  };

  /**
   * 树节点点击事件 添加tab页 并传递参数
   * @param keys
   * @param info
   */
  const handleTreeNodeClick = async (keys: Key[], info: any) => {
    // 选中的key
    setSelectedKeys(keys);

    const {
      node: { isLeaf, parentId: schemaName, name: tableName, fullInfo }
    } = info;

    if (!isLeaf) {
      return;
    }

    addCenterTab({
      id: 'DataSource/' + [selectDatabaseId, schemaName, tableName].join('/'),
      tabType: 'dataSource',
      title: [schemaName, tableName].join('.'),
      isUpdate: false,
      params: {
        selectDatabaseId,
        schemaName,
        tableName,
        type: selectDbType
      }
    } as CenterTab);
  };

  /**
   * 数据库选择改变事件
   * @param {number} databaseId
   */
  const handleSelectDataBaseId = async (databaseId: number) => {
    setSelectDatabaseId(databaseId);
    await onChangeDataBase(databaseId);
  };

  /**
   * 树节点展开事件
   * @param {Key[]} expandedKeys
   */
  const handleTreeExpand = (expandedKeys: Key[]) => {
    setExpandKeys(expandedKeys);
  };

  const cascaderDisplayRender: CascaderProps<Option>['displayRender'] = (
    labels,
    selectedOptions = []
  ) =>
    labels.map((label, i) => {
      const option = selectedOptions[i];
      if (i === labels.length - 1) {
        return <span key={option.value}>{option.label}</span>;
      }
      return (
        <Tag key={option.value} color='processing'>
          {option.value}
        </Tag>
      );
    });
  return (
    <div style={{ paddingInline: 6, height: 'inherit' }} ref={ref}>
      <Spin spinning={isLoadingDatabase} delay={500}>
        <DataSourceModal
          values={{}}
          visible={showCreate}
          onCancel={() => setShowCreate(false)}
          onTest={(value) => handleTest(value)}
          onSubmit={async (value) => {
            await saveOrUpdateHandle(value);
            const data = (await getDataSourceList()) ?? [];
          }}
        />
        <ProForm
          style={{ height: 40, marginTop: 10 }}
          initialValues={{ selectDb: selectDatabaseId }}
          submitter={false}
        >
          <ProFormCascader
            allowClear={false}
            name={'selectDb'}
            placeholder={l('pages.metadata.selectDatabase')}
            fieldProps={{
              options: dbData,
              displayRender: cascaderDisplayRender,
              onChange: async (value) => {
                setSelectDbType(value[0]);
                await handleSelectDataBaseId(value[value.length - 1] as number);
              },
              showSearch: true
            }}
          />
        </ProForm>
        <SchemaTree
          selectKeys={selectedKeys}
          expandKeys={expandKeys}
          height={treeHeight - 40}
          onNodeClick={handleTreeNodeClick}
          treeData={treeData}
          onExpand={handleTreeExpand}
        />
      </Spin>
    </div>
  );
});

export default connect(
  ({ DataStudio }: { DataStudio: DataStudioState }) => ({
    dataSourceDataList: DataStudio.tempData.dataSourceDataList,
    action: DataStudio.action
  }),
  mapDispatchToProps
)(DataSource);
