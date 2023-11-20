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

import { queryList } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { GlobalVar } from '@/types/RegCenter/data';
import { l } from '@/utils/intl';
import { EyeTwoTone } from '@ant-design/icons';
import ProTable, { ProColumns } from '@ant-design/pro-table';
import { Modal, Space, Typography } from 'antd';

const { Text, Link } = Typography;
const { confirm } = Modal;

const GlobalVariable = (props: any) => {
  const columns: ProColumns<GlobalVar>[] = [
    {
      title: l('rc.gv.name'),
      dataIndex: 'name',
      sorter: true,
      width: '80%',
      ellipsis: true,
      render: (_, record) => {
        return (
          <Space direction='vertical' size={0}>
            <Text copyable>{`\$\{${record.name}\}`}</Text>
            <Text type='secondary'>{record.note}</Text>
          </Space>
        );
      }
    },
    {
      title: l('rc.gv.value'),
      dataIndex: 'id',
      width: '20%',
      render: (_, record) => (
        <EyeTwoTone onClick={() => confirm({ content: record.fragmentValue })} />
      )
    }
  ];

  return (
    <ProTable<GlobalVar>
      showHeader={false}
      tableStyle={{ margin: 0, paddingInline: 0 }}
      search={false}
      defaultSize={'small'}
      options={false}
      type={'list'}
      toolBarRender={() => [<Link href={'/#/registration/fragment'}>{l('rc.gv.Management')}</Link>]}
      columns={columns}
      request={(params) =>
        queryList(API_CONSTANTS.GLOBAL_VARIABLE, { ...params, filter: { enabled: [1] } })
      }
      pagination={{ hideOnSinglePage: true, pageSize: 10, size: 'small', showTotal: () => '' }}
    />
  );
};

export default GlobalVariable;
