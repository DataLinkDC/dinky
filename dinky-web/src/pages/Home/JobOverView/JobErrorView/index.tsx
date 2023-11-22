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

import CountFormatter from '@/components/CountFormatter';
import styles from '@/global.less';
import { parseMilliSecondStr } from '@/utils/function';
import { l } from '@/utils/intl';
import { Progress, ProgressConfig } from '@ant-design/plots';
import { ProColumns, ProTable, StatisticCard } from '@ant-design/pro-components';
import { Space } from 'antd';
import React, { useState } from 'react';

const { Statistic } = StatisticCard;

interface DataType {
  rank: React.Key;
  name: string;
  value: number;
  key: string;
}

const JobErrorView: React.FC = () => {
  const [data, setData] = useState<DataType[]>([
    {
      rank: 1,
      name: '任务1',
      value: 10000,
      key: '任务1'
    },
    {
      rank: 2,
      name: '任务2',
      value: 20000,
      key: '任务2'
    },
    {
      rank: 3,
      name: '任务3',
      value: 30000,
      key: '任务3'
    },
    {
      rank: 4,
      name: '任务4',
      value: 40000,
      key: '任务4'
    },
    {
      rank: 5,
      name: '任务5',
      value: 50000,
      key: '任务5'
    },
    {
      rank: 6,
      name: '任务6',
      value: 60000,
      key: '任务6'
    },
    {
      rank: 7,
      name: '任务7',
      value: 70000,
      key: '任务7'
    },
    {
      rank: 8,
      name: '任务8',
      value: 80000,
      key: '任务8'
    }
  ]);

  const columns: ProColumns<DataType>[] = [
    {
      title: l('home.job.failed.rank'),
      dataIndex: 'rank',
      valueType: 'indexBorder'
    },
    {
      title: l('home.job.failed.name'),
      dataIndex: 'name'
    },
    {
      title: l('home.job.failed.time'),
      dataIndex: 'value',
      render: (_: any, record: DataType) => {
        return <>{parseMilliSecondStr(record.value)}</>;
      }
    }
  ];

  const config: ProgressConfig = {
    height: 50,
    width: 200,
    autoFit: false,
    percent: 0.7,
    color: ['#5B8FF9', '#acaeb0']
  };

  return (
    <>
      <StatisticCard
        chartPlacement='right'
        statistic={{
          title: l('home.job.failed.unhandle'),
          value: 3,
          suffix: l('global.item'),
          formatter: (value) => <CountFormatter value={Number(value)} />,
          description: (
            <Space>
              <Statistic title={l('home.job.failed')} value='10 ' suffix={l('global.item')} />
              <Statistic title={l('home.job.failed.handle')} value='70%' />
            </Space>
          )
        }}
        chart={
          <div className={styles['tiny-charts']}>
            <Progress {...config} />
          </div>
        }
      />
      <ProTable
        columns={columns}
        scroll={{ y: 200 }}
        dataSource={data}
        search={false}
        toolBarRender={false}
        pagination={false}
        size='small'
      />
    </>
  );
};

export default JobErrorView;
