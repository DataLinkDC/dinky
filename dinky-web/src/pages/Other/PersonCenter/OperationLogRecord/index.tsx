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

import { ShowLogIcon } from '@/components/Icons/CustomIcons';
import ResultShow from '@/pages/Other/PersonCenter/OperationLogRecord/ResultShow';
import { queryList } from '@/services/api';
import { PROTABLE_OPTIONS_PUBLIC } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { OperateLog } from '@/types/AuthCenter/data';
import { l } from '@/utils/intl';
import { ProTable } from '@ant-design/pro-components';
import { ProColumns } from '@ant-design/pro-table';
import { Button } from 'antd';
import React, { useState } from 'react';

type OperationLogRecordProps = {
  userId: number;
};
const OperationLogRecord: React.FC<OperationLogRecordProps> = (props) => {
  const { userId } = props;

  const [showResult, setShowResult] = useState({
    open: false,
    data: ''
  });

  const columns: ProColumns<OperateLog>[] = [
    {
      title: l('user.op.time'),
      dataIndex: 'operateTime',
      valueType: 'dateTime',
      hideInSearch: true
    },
    {
      title: l('user.op.name'),
      dataIndex: 'operateName',
      hideInSearch: true
    },
    {
      title: l('user.op.module'),
      dataIndex: 'moduleName',
      ellipsis: true
    },
    {
      title: l('user.op.type'),
      dataIndex: 'businessType'
    },
    {
      title: l('user.op.method'),
      dataIndex: 'method',
      ellipsis: true
    },
    {
      title: l('user.request.type'),
      dataIndex: 'requestMethod',
      hideInSearch: true
    },
    {
      title: l('user.op.params'),
      render: (text, record) => {
        return (
          <Button
            icon={<ShowLogIcon />}
            type={'link'}
            onClick={() =>
              setShowResult({
                open: true,
                data: record.operateParam
              })
            }
          />
        );
      },
      hideInSearch: true
    },
    {
      title: l('user.op.result'),
      dataIndex: 'jsonResult',
      hideInSearch: true,
      render: (text, record) => {
        return (
          <Button
            icon={<ShowLogIcon />}
            type={'link'}
            onClick={() =>
              setShowResult({
                open: true,
                data: record.jsonResult
              })
            }
          />
        );
      }
    },
    {
      title: l('user.op.url'),
      dataIndex: 'operateUrl',
      hideInSearch: true
    },
    {
      title: l('user.op.ip'),
      dataIndex: 'operateIp',
      hideInSearch: true
    },
    {
      title: l('user.op.status'),
      dataIndex: 'status',
      valueEnum: {
        1: { text: l('global.error'), status: 'error' },
        0: { text: l('global.success'), status: 'Success' }
      },
      hideInSearch: true
    },
    {
      title: l('user.op.error.msg'),
      dataIndex: 'errorMsg',
      render: (text, record) => {
        return (
          <>
            {record.errorMsg ? (
              <Button
                icon={<ShowLogIcon />}
                type={'link'}
                onClick={() =>
                  setShowResult({
                    open: true,
                    data: record.errorMsg
                  })
                }
              />
            ) : (
              <>-</>
            )}
          </>
        );
      }
    }
  ];

  const handleCloseResult = () => {
    setShowResult((prevState) => ({
      ...prevState,
      open: false,
      data: ''
    }));
  };

  return (
    <>
      <ProTable<OperateLog>
        headerTitle={undefined}
        ghost
        options={false}
        size={'small'}
        {...PROTABLE_OPTIONS_PUBLIC}
        pagination={{
          position: ['bottomCenter'],
          pageSize: 18,
          hideOnSinglePage: true,
          showQuickJumper: true
        }}
        request={async (params, sorter, filter: any) =>
          await queryList(`${API_CONSTANTS.OPERATE_LOG}/${userId}`, {
            ...params,
            sorter,
            filter
          })
        }
        columns={columns}
      />

      <ResultShow onCancel={handleCloseResult} config={showResult} />
    </>
  );
};

export default OperationLogRecord;
