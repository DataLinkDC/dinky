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

import { PROTABLE_OPTIONS_PUBLIC } from '@/services/constants';
import { ProTable } from '@ant-design/pro-components';
import { ProColumns } from '@ant-design/pro-table';
import React from 'react';

type DataListProps = {
  columns: ProColumns[];
  data: any[];
};
const DataList: React.FC<DataListProps> = (props) => {
  const { columns, data } = props;

  return (
    <>
      <ProTable
        {...PROTABLE_OPTIONS_PUBLIC}
        pagination={{
          position: ['bottomCenter'],
          pageSize: 10,
          showQuickJumper: true,
          hideOnSinglePage: true
        }}
        columns={columns}
        dataSource={data}
        search={false}
        options={false}
        scroll={{ x: 'max-content' }}
      />
    </>
  );
};

export default DataList;
