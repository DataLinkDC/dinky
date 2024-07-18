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

import { DataSources } from '@/types/RegCenter/data';
import { Descriptions } from 'antd';
import React from 'react';
import EllipsisMiddle from '@/components/Typography/EllipsisMiddle';
import { DATA_SOURCE_TYPE } from '@/pages/RegCenter/DataSource/components/constants';

type TableInfoProps = {
  tableInfo: Partial<DataSources.Table>;
};

const TableInfo: React.FC<TableInfoProps> = (props) => {
  const { tableInfo } = props;

  const splitOptions = () => {
    const dt = tableInfo.driverType;
    if (DATA_SOURCE_TYPE.PAIMON == dt) {
      const options = JSON.parse(tableInfo.options ?? '{}');
      return (
        <>
          {Object.entries(options).map(([key]) => (
            <Descriptions.Item key={key} label={key}>
              <EllipsisMiddle maxCount={20} children={options[key]} copyable={false} />
            </Descriptions.Item>
          ))}
        </>
      );
    } else {
      return (
        <>
          <Descriptions.Item label='Catalog'>{tableInfo.catalog}</Descriptions.Item>
          <Descriptions.Item label='Rows'>{tableInfo.rows}</Descriptions.Item>
          <Descriptions.Item label='Type'>{tableInfo.type}</Descriptions.Item>
          <Descriptions.Item label='Engine'>{tableInfo.engine}</Descriptions.Item>
          <Descriptions.Item label='Options'>{tableInfo.options}</Descriptions.Item>
          <Descriptions.Item label='CreateTime'>{tableInfo.createTime}</Descriptions.Item>
          <Descriptions.Item label='UpdateTime'>{tableInfo.updateTime || '-'}</Descriptions.Item>
          <Descriptions.Item label='Comment' span={3}>
            {tableInfo.comment || '-'}
          </Descriptions.Item>
        </>
      );
    }
  };

  return (
    <>
      <Descriptions size={'small'} bordered column={3}>
        <Descriptions.Item label='Name'>{tableInfo.name}</Descriptions.Item>
        <Descriptions.Item label='Schema'>{tableInfo.schema}</Descriptions.Item>
        {splitOptions()}
      </Descriptions>
    </>
  );
};

export default TableInfo;
