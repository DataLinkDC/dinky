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

import { DataSourceDetailBackButton } from '@/components/StyledComponents';
import { StateType, STUDIO_MODEL } from '@/pages/DataStudio/model';
import RightTagsRouter from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter';
import { QueryParams } from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/data';
import SchemaTree from '@/pages/RegCenter/DataSource/components/DataSourceDetail/SchemaTree';
import { getDataByIdReturnResult } from '@/services/BusinessCrud';
import { RESPONSE_CODE } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { DataSources } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { BackwardOutlined, ReloadOutlined } from '@ant-design/icons';
import { Key, ProCard } from '@ant-design/pro-components';
import { connect, history } from '@umijs/max';
import { Button, Space } from 'antd';
import { useCallback, useEffect, useState } from 'react';

interface DataSourceDetailProps {
  dataSource: DataSources.DataSource;
  backClick: () => void;
}
const DataSourceDetail = (props: DataSourceDetailProps & connect) => {
  const {
    dataSource,
    backClick,
    dispatch,
    database: { dbData, selectDatabaseId, expandKeys, selectKeys }
  } = props;
  const [loading, setLoading] = useState<boolean>(false);
  const [disabled, setDisabled] = useState<boolean>(true);
  const [treeData, setTreeData] = useState<Partial<any>[]>([]);
  const [tableInfo, setTableInfo] = useState<Partial<DataSources.Table>>({});
  const [params, setParams] = useState<QueryParams>({
    id: 0,
    schemaName: '',
    tableName: ''
  });
  const selectDb = (dbData as DataSources.DataSource[]).filter((x) => x.id === selectDatabaseId)[0];

  const handleBackClick = () => {
    // go back
    history.push(`/registration/datasource`);
    // back click callback
    backClick();
  };

  const clearState = () => {
    setDisabled(true);
    setTableInfo({});
    setParams({
      id: 0,
      schemaName: '',
      tableName: ''
    });
  };

  const querySchemaTree = useCallback(async () => {
    clearState();
    setLoading(true);
    await getDataByIdReturnResult(API_CONSTANTS.DATASOURCE_GET_SCHEMA_TABLES, dataSource.id).then(
      (res) => {
        if (res.code === RESPONSE_CODE.SUCCESS) {
          setTreeData(res.data);
        }
      }
    );
    setLoading(false);
  }, [dataSource.id]);

  useEffect(() => {
    querySchemaTree();
  }, []);

  /**
   * tree node click
   */
  const onSchemaTreeNodeClick = useCallback(async (keys: Key[], info: any) => {
    const {
      node: { isLeaf, parentId: schemaName, name: tableName, fullInfo }
    } = info;
    // 选中的key
    dispatch({
      type: STUDIO_MODEL.updateDatabaseSelectKey,
      payload: keys
    });

    if (!isLeaf) {
      clearState();
      return;
    }

    dispatch({
      type: STUDIO_MODEL.addTab,
      payload: {
        icon: selectDb.type,
        id: selectDatabaseId + schemaName + tableName,
        breadcrumbLabel: [selectDb.type, selectDb.name].join('/'),
        label: schemaName + '.' + tableName,
        params: {
          queryParams: { id: selectDatabaseId, schemaName, tableName },
          tableInfo: fullInfo
        },
        type: 'metadata'
      }
    });

    setParams({
      id: dataSource.id as number,
      schemaName,
      tableName
    });

    setDisabled(false);
    // get table columns
    setTableInfo(fullInfo);
  }, []);

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

  /**
   * render back button and refresh button
   * @return {JSX.Element}
   */
  const renderBackButton = (
    <DataSourceDetailBackButton>
      <Space size={'middle'}>
        <Button
          size={'middle'}
          icon={<ReloadOutlined spin={loading} />}
          type='primary'
          onClick={() => querySchemaTree()}
        >
          {l('button.refresh')}
        </Button>
        <Button
          size={'middle'}
          icon={<BackwardOutlined />}
          type='primary'
          onClick={handleBackClick}
        >
          {l('button.back')}
        </Button>
      </Space>
    </DataSourceDetailBackButton>
  );

  /**
   * render
   */
  return (
    <ProCard loading={loading} ghost gutter={[16, 16]} split='vertical'>
      <ProCard hoverable bordered className={'siderTree schemaTree'} colSpan='16%'>
        {/* tree */}
        <SchemaTree
          selectKeys={selectKeys}
          expandKeys={expandKeys}
          onExpand={handleTreeExpand}
          onNodeClick={onSchemaTreeNodeClick}
          treeData={treeData}
        />
      </ProCard>
      <ProCard hoverable colSpan='84%' ghost headerBordered>
        {/* tags */}
        <RightTagsRouter
          tableInfo={tableInfo}
          queryParams={params}
          rightButtons={renderBackButton}
          tagDisabled={disabled}
        />
      </ProCard>
    </ProCard>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  database: Studio.database
}))(DataSourceDetail);
