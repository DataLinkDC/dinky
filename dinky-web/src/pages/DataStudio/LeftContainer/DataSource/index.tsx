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

import { TagAlignLeft } from '@/components/StyledComponents';
import { BtnRoute, useTasksDispatch } from '@/pages/DataStudio/LeftContainer/BtnContext';
import SchemaTree from '@/pages/RegCenter/DataSource/components/DataSourceDetail/SchemaTree';
import DataSourceModal from '@/pages/RegCenter/DataSource/components/DataSourceModal';
import { handleTest, saveOrUpdateHandle } from '@/pages/RegCenter/DataSource/service';
import { DataSources } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { DatabaseOutlined, TableOutlined } from '@ant-design/icons';
import { Key, ProForm, ProFormSelect } from '@ant-design/pro-components';
import { connect } from '@umijs/max';
import { Spin, Tag } from 'antd';
import { useEffect, useState } from 'react';
import { StateType, STUDIO_MODEL, TabsPageType } from '../../model';
import { clearDataSourceTable, getDataSourceList, showDataSourceTable } from './service';

const DataSource = (props: any) => {
  const {
    dispatch,
    toolContentHeight,
    database: { dbData, selectDatabaseId, expandKeys, selectKeys }
  } = props;
  const [treeData, setTreeData] = useState<[]>([]);
  const [isLoadingDatabase, setIsLoadingDatabase] = useState(false);
  const [showCreate, setShowCreate] = useState(false);
  const selectDb = (dbData as DataSources.DataSource[]).filter((x) => x.id === selectDatabaseId)[0];
  const btnDispatch = useTasksDispatch();

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
    if (selectDatabaseId) {
      onRefreshTreeData(selectDatabaseId);
    }
  }, []);

  /**
   * 数据库选择改变时间时 刷新树数据
   * @param {number} value
   */
  const onChangeDataBase = (value: number) => {
    onRefreshTreeData(value);
  };
  const currentTabName = 'menu.datastudio.datasource';

  const btnEvent = [...BtnRoute[currentTabName]];

  btnEvent[0].onClick = () => {
    setShowCreate(true);
  };
  btnEvent[1].onClick = () => {
    if (!selectDatabaseId) return;
    setIsLoadingDatabase(true);
    clearDataSourceTable(selectDatabaseId).then(() => {
      onChangeDataBase(selectDatabaseId);
    });
  };
  btnDispatch({
    type: 'change',
    selectKey: currentTabName,
    payload: btnEvent
  });

  /**
   * 构建数据库列表 下拉框
   */
  const getDataBaseOptions = () => {
    return dbData.map(({ id, name, type, enabled, status }: DataSources.DataSource) => ({
      key: id,
      value: id,
      label: (
        <TagAlignLeft>
          <Tag key={id} color={enabled ? 'processing' : 'error'}>
            {type}
          </Tag>
          {name}
        </TagAlignLeft>
      ),
      disabled: !enabled || !status
    }));
  };

  dispatch({
    type: STUDIO_MODEL.updateSelectRightKey,
    payload: ''
  });

  /**
   * 树节点点击事件 添加tab页 并传递参数
   * @param keys
   * @param info
   */
  const handleTreeNodeClick = async (keys: Key[], info: any) => {
    // 选中的key
    dispatch({
      type: STUDIO_MODEL.updateDatabaseSelectKey,
      payload: keys
    });

    const {
      node: { isLeaf, parentId: schemaName, name: tableName, fullInfo }
    } = info;

    if (!isLeaf) {
      return;
    }

    const queryParams = { id: selectDatabaseId, schemaName, tableName };
    dispatch({
      type: STUDIO_MODEL.addTab,
      payload: {
        icon: selectDb.type,
        id: selectDatabaseId + schemaName + tableName,
        breadcrumbLabel: [selectDb.type, selectDb.name].join('/'),
        label: schemaName + '.' + tableName,
        params: { queryParams: queryParams, tableInfo: fullInfo },
        type: TabsPageType.metadata
      }
    });
  };

  /**
   * 数据库选择改变事件
   * @param {number} databaseId
   */
  const handleSelectDataBaseId = (databaseId: number) => {
    dispatch({
      type: STUDIO_MODEL.updateSelectDatabaseId,
      payload: databaseId
    });
    onChangeDataBase(databaseId);
  };

  /**
   * 树节点展开事件
   * @param {Key[]} expandedKeys
   */
  const handleTreeExpand = (expandedKeys: Key[]) => {
    dispatch({
      type: STUDIO_MODEL.updateDatabaseExpandKey,
      payload: expandedKeys
    });
  };

  return (
    <Spin spinning={isLoadingDatabase} delay={500}>
      <DataSourceModal
        values={{}}
        visible={showCreate}
        onCancel={() => setShowCreate(false)}
        onTest={(value) => handleTest(value)}
        onSubmit={async (value) => {
          await saveOrUpdateHandle(value);
          const data = await getDataSourceList();
          dispatch({
            type: STUDIO_MODEL.saveDataBase,
            payload: data
          });
        }}
      />
      <ProForm
        style={{ height: 40 }}
        initialValues={{ selectDb: selectDatabaseId }}
        submitter={false}
      >
        <ProFormSelect
          style={{ paddingInline: 6 }}
          // width={leftContainer.width  }
          width={'xl'}
          // addonAfter={<ReloadOutlined spin={isLoadingDatabase} title={l('button.refresh')} onClick={() => refreshDataBase()} />}
          allowClear={false}
          name={'selectDb'}
          placeholder={l('pages.metadata.selectDatabase')}
          options={getDataBaseOptions()}
          fieldProps={{
            onSelect: (selectId) => handleSelectDataBaseId(selectId as number)
          }}
        />
      </ProForm>
      <SchemaTree
        selectKeys={selectKeys}
        expandKeys={expandKeys}
        style={{ height: toolContentHeight - 64 - 20 }}
        onNodeClick={handleTreeNodeClick}
        treeData={treeData}
        onExpand={handleTreeExpand}
      />
    </Spin>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  toolContentHeight: Studio.toolContentHeight,
  leftContainer: Studio.leftContainer,
  database: Studio.database
}))(DataSource);
