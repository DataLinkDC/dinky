/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {DataSources} from '@/types/RegCenter/data';
import React from 'react';
import CodeShow from '@/components/CustomEditor/CodeShow';
import {ProCard} from '@ant-design/pro-components';
import {Empty} from 'antd';
import {l} from '@/utils/intl';

type GenSQLProps = {
  sql: Partial<DataSources.SqlGeneration>;
}

/**
 * props for CodeShow
 */
const CodeShowProps = {
  height: '60vh',
  width: '100%',
  lineNumbers: 'on',
  language: 'sql',
  showFloatButton: true,
};


const GenSQL: React.FC<GenSQLProps> = (props) => {
  const {sql} = props;
  const [activeKey, setActiveKey] = React.useState<string>('flinkddl');

  // tab list
  const tabList = [
    {
      key: 'flinkddl',
      label: 'Flink DDL',
      children: <CodeShow {...CodeShowProps} code={sql.flinkSqlCreate || ''}/>
    },
    {
      key: 'select',
      label: 'Select',
      children: <CodeShow {...CodeShowProps} code={sql.sqlSelect || ''}/>
    },
    {
      key: 'sqlddl',
      label: 'SQL DDL',
      children: <CodeShow {...CodeShowProps} code={sql.sqlCreate || ''}/>
    }
  ]

  // tab props
  const restTabProps = {
    activeKey,
    onChange: (key: string) =>setActiveKey(key),
    tabPosition: 'left',
    items: tabList
  }


  return <>
    { (sql.flinkSqlCreate || sql.sqlSelect || sql.sqlCreate) ?
      <ProCard tabs={{...restTabProps}}/>
      : <Empty className={'code-content-empty'} description={l('rc.ds.detail.tips')}/>
    }
  </>;
};

export default GenSQL;
