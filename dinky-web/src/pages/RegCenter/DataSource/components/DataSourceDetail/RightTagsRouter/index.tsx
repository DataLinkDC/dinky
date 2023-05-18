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

import {Space, Tabs} from 'antd';
import {l} from '@/utils/intl';
import {
  BookOutlined,
} from '@ant-design/icons';
import React from 'react';
import {DataSources} from '@/types/RegCenter/data';
import SchemaDesc from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SchemaDesc';


type RightTagsRouterProps = {
  tableInfo: Partial<DataSources.Table>;
  tableColumns: Partial<DataSources.Column[]>;
}


const RightTagsRouter: React.FC<RightTagsRouterProps> = (props) => {

  const {tableColumns, tableInfo} = props;

  const [activeKey, setActiveKey] = React.useState('desc');

  return <>
    <Tabs
      activeKey={activeKey}
      onChange={(key) => setActiveKey(key)}
      size="small"
      animated
      destroyInactiveTabPane
      style={{height: '100%'}}
      items={[
        {
          key: 'desc',
          label: <Space><BookOutlined/>{l('rc.ds.detail.tag.desc')}</Space>,
          children: <SchemaDesc tableInfo={tableInfo} tableColumns={tableColumns}/>
        },
        {
          key: 'query',
          label: <Space><BookOutlined/>{l('rc.ds.detail.tag.query')}</Space>,
          children: <SchemaDesc tableInfo={tableInfo} tableColumns={tableColumns}/>
        },
        {
          key: 'gensql',
          label: <Space><BookOutlined/>{l('rc.ds.detail.tag.gensql')}</Space>,
          children: <SchemaDesc tableInfo={tableInfo} tableColumns={tableColumns}/>
        },
        {
          key: 'console',
          label: <Space><BookOutlined/>{l('rc.ds.detail.tag.console')}</Space>,
          children: <SchemaDesc tableInfo={tableInfo} tableColumns={tableColumns}/>
        },
      ]}
    />
  </>;
};

export default RightTagsRouter;
