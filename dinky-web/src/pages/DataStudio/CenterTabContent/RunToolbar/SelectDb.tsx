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
import { DataSources } from '@/types/RegCenter/data';
import { TagAlignLeft } from '@/components/StyledComponents';
import { Tag } from 'antd';
import { ProFormSelect } from '@ant-design/pro-components';
import { l } from '@/utils/intl';
import { TaskState, TempData } from '@/pages/DataStudio/type';

export default (props: { databaseDataList: TempData['dataSourceDataList']; data: TaskState }) => {
  const dataSourceData: Record<string, React.ReactNode> = {};
  const { databaseDataList, data } = props;
  databaseDataList
    .filter((x) => x.type.toLowerCase() === data?.dialect.toLowerCase())
    .forEach((item: DataSources.DataSource) => {
      dataSourceData[item.id] = item.name;
    });
  return (
    <ProFormSelect
      // width={'sm'}
      name={'databaseId'}
      label={l('pages.datastudio.label.execConfig.selectDatabase')}
      convertValue={(value) => String(value)}
      valueEnum={dataSourceData}
      placeholder='Please select a dataSource'
      rules={[{ required: true, message: 'Please select your dataSource!' }]}
      allowClear={false}
      fieldProps={{
        popupMatchSelectWidth: false
      }}
    />
  );
};
