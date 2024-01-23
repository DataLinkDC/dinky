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

import { AuthorizedObject, useAccess } from '@/hooks/useAccess';
import { QueryParams } from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/data';
import GenSQL from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/GenSQL';
import SchemaDesc from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SchemaDesc';
import SQLConsole from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SQLConsole';
import SQLQuery from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SQLQuery';
import { queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { PermissionConstants } from '@/types/Public/constants';
import { DataSources } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { BookOutlined, ConsoleSqlOutlined, HighlightOutlined } from '@ant-design/icons';
import { ProCardTabsProps } from '@ant-design/pro-card/es/typing';
import { ProCard } from '@ant-design/pro-components';
import { Space } from 'antd';
import { SearchOutline } from 'antd-mobile-icons';
import React, { useEffect, useState } from 'react';

/**
 * props
 */
type RightTagsRouterProps = {
  tableInfo: Partial<DataSources.Table>;
  rightButtons?: React.ReactNode;
  queryParams: QueryParams;
  tagDisabled?: boolean;
};

const RightTagsRouter: React.FC<RightTagsRouterProps> = (props) => {
  const access = useAccess();

  const { tableInfo, queryParams, tagDisabled = false, rightButtons } = props;
  const [tableColumns, setTableColumns] = useState<Partial<DataSources.Column[]>>([]);
  useEffect(() => {
    const fetchData = async () => {
      const result = await queryDataByParams(
        API_CONSTANTS.DATASOURCE_GET_COLUMNS_BY_TABLE,
        queryParams
      );
      setTableColumns(result as DataSources.Column[]);
    };
    if (queryParams.id !== 0) {
      fetchData();
    }
  }, [queryParams]);
  // state
  const [activeKey, setActiveKey] = useState('desc');

  // tab list
  const tabList = [
    {
      key: 'desc',
      label: (
        <Space>
          <BookOutlined />
          {l('rc.ds.detail.tag.desc')}
        </Space>
      ),
      children: <SchemaDesc tableInfo={tableInfo} tableColumns={tableColumns} />,
      disabled: tagDisabled,
      auth: PermissionConstants.REGISTRATION_DATA_SOURCE_DETAIL_DESC
    },
    {
      key: 'query',
      label: (
        <Space>
          <SearchOutline />
          {l('rc.ds.detail.tag.query')}
        </Space>
      ),
      children: <SQLQuery queryParams={queryParams} />,
      disabled: tagDisabled,
      auth: PermissionConstants.REGISTRATION_DATA_SOURCE_DETAIL_QUERY
    },
    {
      key: 'gensql',
      label: (
        <Space>
          <HighlightOutlined />
          {l('rc.ds.detail.tag.gensql')}
        </Space>
      ),
      children: <GenSQL tagDisabled={tagDisabled} queryParams={queryParams} />,
      disabled: tagDisabled,
      auth: PermissionConstants.REGISTRATION_DATA_SOURCE_DETAIL_GENSQL
    },
    {
      key: 'console',
      label: (
        <Space>
          <ConsoleSqlOutlined />
          {l('rc.ds.detail.tag.console')}
        </Space>
      ),
      disabled: true,
      children: <SQLConsole />,
      auth: PermissionConstants.REGISTRATION_DATA_SOURCE_DETAIL_CONSOLE
    }
  ];

  // tab props
  const restTabProps: ProCardTabsProps = {
    activeKey: activeKey,
    type: 'card',
    tabBarExtraContent: rightButtons,
    animated: true,
    onChange: (key: string) => setActiveKey(key),
    items: tabList.filter((item) => AuthorizedObject({ path: item.auth, children: item, access }))
  };

  /**
   * render
   */
  return (
    <>
      <ProCard className={'schemaTree'} size='small' bordered tabs={{ ...restTabProps }} />
    </>
  );
};

export default RightTagsRouter;
