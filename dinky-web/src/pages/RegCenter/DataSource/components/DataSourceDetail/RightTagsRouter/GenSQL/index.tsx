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

import CodeShow from '@/components/CustomEditor/CodeShow';
import { QueryParams } from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/data';
import { queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { DataSources } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { ProCardTabsProps } from '@ant-design/pro-card/es/typing';
import { ProCard } from '@ant-design/pro-components';
import { Empty, Typography } from 'antd';
import React, { useCallback, useEffect, useState } from 'react';

const { Paragraph } = Typography;

type GenSQLProps = {
  queryParams: Partial<QueryParams>;
  tagDisabled: boolean;
};

/**
 * props for CodeShow
 */
const CodeShowProps = {
  height: '68vh',
  width: '100%',
  lineNumbers: 'on',
  language: 'sql',
  showFloatButton: true
};

const GenSQL: React.FC<GenSQLProps> = (props) => {
  const { queryParams, tagDisabled } = props;
  const [genSQL, setGenSQL] = useState<DataSources.SqlGeneration>({
    flinkSqlCreate: '',
    sqlSelect: '',
    sqlCreate: ''
  });
  const [activeKey, setActiveKey] = React.useState<string>('flinkddl');

  const queryDDL = useCallback(async () => {
    //get gen sql
    const genSQLData = await queryDataByParams(API_CONSTANTS.DATASOURCE_GET_GEN_SQL, {
      ...queryParams
    });
    setGenSQL(genSQLData as DataSources.SqlGeneration);
  }, [queryParams]);

  /**
   * query
   */
  useEffect(() => {
    if (queryParams.id && queryParams.tableName && queryParams.schemaName) {
      queryDDL();
    }
  }, [queryParams]);

  /**
   * render content
   * @param code
   */
  const renderContent = (code = '') => {
    return <CodeShow {...CodeShowProps} language={'flinksql'} code={tagDisabled ? '' : code} />;
  };

  /**
   * render label
   * @param {string} content
   * @param {string} title
   * @returns {JSX.Element}
   */
  const renderLabel = (content: string, title: string) => {
    return <Paragraph copyable={{ text: content }}>{title}</Paragraph>;
  };

  /**
   * tab list
   */
  const tabList = [
    {
      key: 'flinkddl',
      label: renderLabel(genSQL?.flinkSqlCreate ?? '', 'Flink DDL'),
      disabled: tagDisabled,
      children: renderContent(genSQL?.flinkSqlCreate ?? '')
    },
    {
      key: 'select',
      label: renderLabel(genSQL?.sqlSelect ?? '', 'Select'),
      disabled: tagDisabled,
      children: renderContent(genSQL?.sqlSelect ?? '')
    },
    {
      key: 'sqlddl',
      label: renderLabel(genSQL?.sqlCreate ?? '', 'SQL DDL'),
      disabled: tagDisabled,
      children: renderContent(genSQL?.sqlCreate ?? '')
    }
  ];

  // tab props
  const restTabProps: ProCardTabsProps = {
    activeKey,
    onChange: (key: string) => setActiveKey(key),
    tabPosition: 'left',
    size: 'small',
    items: tabList
  };

  /**
   * render
   */
  return (
    <>
      {genSQL?.flinkSqlCreate || genSQL?.sqlSelect || genSQL?.sqlCreate ? (
        <ProCard tabs={{ ...restTabProps }} />
      ) : (
        <Empty className={'code-content-empty'} description={l('rc.ds.detail.tips')} />
      )}
    </>
  );
};

export default GenSQL;
