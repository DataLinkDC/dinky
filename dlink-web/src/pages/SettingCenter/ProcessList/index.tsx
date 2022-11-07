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

import React, {useRef, useState} from "react";
import ProTable, {ActionType, ProColumns} from "@ant-design/pro-table";
import {Drawer} from 'antd';
import {PageContainer} from '@ant-design/pro-layout';
import ProDescriptions from '@ant-design/pro-descriptions';
import {getData} from "@/components/Common/crud";
import {ProcessItem} from "@/pages/SettingCenter/ProcessList/data";
import {useIntl} from "umi";

const url = '/api/process/listAllProcess';
const ProcessList: React.FC<{}> = (props: any) => {
  const {dispatch} = props;
  const [row, setRow] = useState<ProcessItem>();
  const actionRef = useRef<ActionType>();

  const intl = useIntl();
  const l = (id: string, defaultMessage?: string, value?: {}) => intl.formatMessage({id, defaultMessage}, value);

  const columns: ProColumns<ProcessItem>[] = [
    {
      title: '进程ID',
      dataIndex: 'pid',
      sorter: true,
      render: (dom, entity) => {
        return <a onClick={() => setRow(entity)}>{dom}</a>;
      },
    },
    {
      title: '进程名',
      sorter: true,
      dataIndex: 'name',
    },
    {
      title: '任务ID',
      sorter: true,
      dataIndex: 'taskId',
    },
    {
      title: '类型',
      sorter: true,
      dataIndex: 'type',
      filters: [
        {
          text: 'FlinkExplain',
          value: 'FlinkExplain',
        }, {
          text: 'FlinkSubmit',
          value: 'FlinkSubmit',
        }, {
          text: 'SQLExplain',
          value: 'SQLExplain',
        }, {
          text: 'SQLSubmit',
          value: 'SQLSubmit',
        }, {
          text: 'Unknown',
          value: 'Unknown',
        },
      ],
      filterMultiple: false,
      valueEnum: {
        'FlinkExplain': {text: 'FlinkExplain'},
        'FlinkSubmit': {text: 'FlinkSubmit'},
        'SQLExplain': {text: 'SQLExplain'},
        'SQLSubmit': {text: 'SQLSubmit'},
        'Unknown': {text: 'Unknown'},
      },
    }, {
      title: '状态',
      sorter: true,
      dataIndex: 'status',
      filters: [
        {
          text: 'INITIALIZING',
          value: 'INITIALIZING',
        }, {
          text: 'RUNNING',
          value: 'RUNNING',
        }, {
          text: 'FAILED',
          value: 'FAILED',
        }, {
          text: 'CANCELED',
          value: 'CANCELED',
        }, {
          text: 'FINISHED',
          value: 'FINISHED',
        }, {
          text: 'UNKNOWN',
          value: 'UNKNOWN',
        },
      ],
      filterMultiple: false,
      valueEnum: {
        'INITIALIZING': {text: 'INITIALIZING'},
        'RUNNING': {text: 'RUNNING'},
        'FAILED': {text: 'FAILED'},
        'CANCELED': {text: 'CANCELED'},
        'FINISHED': {text: 'FINISHED'},
        'UNKNOWN': {text: 'UNKNOWN'},
      },
    },
    {
      title: '开始时间',
      dataIndex: 'startTime',
      sorter: true,
      valueType: 'dateTime',
    },
    {
      title: '结束时间',
      dataIndex: 'endTime',
      sorter: true,
      valueType: 'dateTime',
    }, {
      title: '耗时',
      sorter: true,
      dataIndex: 'time',
    }, {
      title: '操作人ID',
      sorter: true,
      dataIndex: 'userId',
    }
  ];

  return (
    <PageContainer title={false}>
      <ProTable
        actionRef={actionRef}
        rowKey="pid"
        request={(params, sorter, filter) => getData(url, {active: false})}
        columns={columns}
        search={false}
      />
      <Drawer
        width={600}
        visible={!!row}
        onClose={() => {
          setRow(undefined);
        }}
        closable={false}
      >
        {row?.pid && (
          <ProDescriptions<ProcessItem>
            column={2}
            title={row?.pid}
            request={async () => ({
              data: row || {},
            })}
            params={{
              pid: row?.pid,
            }}
            columns={columns}
          />
        )}
      </Drawer>
    </PageContainer>
  );
};

export default ProcessList;
