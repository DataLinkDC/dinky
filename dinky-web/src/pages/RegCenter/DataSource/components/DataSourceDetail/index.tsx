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
import { Authorized, HasAuthority } from '@/hooks/useAccess';
import RightTagsRouter from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter';
import { QueryParams } from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/data';
import SchemaTree from '@/pages/RegCenter/DataSource/components/DataSourceDetail/SchemaTree';
import { PermissionConstants } from '@/types/Public/constants';
import { l } from '@/utils/intl';
import { BackwardOutlined, ReloadOutlined } from '@ant-design/icons';
import { Key, ProCard } from '@ant-design/pro-components';
import { history } from '@umijs/max';
import { Button, Space } from 'antd';
import { useCallback, useState } from 'react';
import { useLocation } from 'umi';
import { getUrlParam } from '@/utils/function';
import { useAsyncEffect } from 'ahooks';
import {
  clearDataSourceTable,
  showDataSourceTable
} from '@/pages/DataStudio/Toolbar/DataSource/service';

export default () => {
  const location = useLocation();

  const [loading, setLoading] = useState<boolean>(false);
  const [disabled, setDisabled] = useState<boolean>(true);
  const [treeData, setTreeData] = useState<Partial<any>[]>([]);
  const [selectKeys, setSelectKeys] = useState<Key[]>([]);
  const [expandKeys, setExpandKeys] = useState<Key[]>([]);

  const [params, setParams] = useState<QueryParams>({
    id: Number.parseInt(getUrlParam(location.search, 'id')),
    schemaName: '',
    tableName: ''
  });

  const handleBackClick = () => {
    // go back
    history.push(`/registration/datasource/list`);
  };

  const clearState = () => {
    setDisabled(true);
    setParams((prevState) => ({
      ...prevState,
      schemaName: '',
      tableName: ''
    }));
  };

  const querySchemaTree = useCallback(async () => {
    clearState();
    setLoading(true);
    await showDataSourceTable(params.id).then((res) => res && setTreeData(res));
    setLoading(false);
  }, [params.id]);

  useAsyncEffect(async () => {
    await querySchemaTree();
  }, []);

  /**
   * tree node click
   */
  const onSchemaTreeNodeClick = useCallback(async (keys: Key[], info: any) => {
    const {
      node: { isLeaf, parentId: schemaName, name: tableName, fullInfo }
    } = info;
    setSelectKeys(keys);

    if (!isLeaf) {
      clearState();
      return;
    }

    setParams((prevState) => ({
      ...prevState,
      schemaName,
      tableName
    }));

    setDisabled(false);
  }, []);

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
          hidden={!HasAuthority(PermissionConstants.REGISTRATION_DATA_SOURCE_DETAIL_REFRESH)}
          onClick={() => clearDataSourceTable(params.id).then(() => querySchemaTree())}
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
        <Authorized
          key='schemaTree'
          path={PermissionConstants.REGISTRATION_DATA_SOURCE_DETAIL_TREE}
        >
          <SchemaTree
            selectKeys={selectKeys}
            expandKeys={expandKeys}
            onExpand={setExpandKeys}
            onNodeClick={onSchemaTreeNodeClick}
            treeData={treeData}
            height={innerHeight - 190}
          />
        </Authorized>
      </ProCard>
      <ProCard hoverable colSpan='84%' ghost headerBordered>
        {/* tags */}
        <RightTagsRouter
          queryParams={params}
          rightButtons={renderBackButton}
          tagDisabled={disabled}
        />
      </ProCard>
    </ProCard>
  );
};
