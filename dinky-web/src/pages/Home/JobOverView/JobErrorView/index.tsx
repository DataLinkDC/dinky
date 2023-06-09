/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import {Space, Table} from 'antd';
import React, {useState} from 'react';
import {ProColumns, ProTable, StatisticCard} from "@ant-design/pro-components";
import styles from "@/global.less";
import {Progress} from "@ant-design/plots";
import CountFormatter from "@/components/CountFormatter";
import {ProgressConfig} from "@ant-design/plots/es/components/progress";

const {Statistic} = StatisticCard;

interface DataType {
  rank: React.Key;
  name: string;
  value: string;
}

const JobErrorView: React.FC = () => {

  const [  data, setData] =  useState<DataType[]>([
    {
      "rank": 1,
      "name": '任务1',
      "value": '10 分钟'
    },
    {
      "rank": 2,
      "name": '任务2',
      "value": '10 分钟'
    },
    {
      "rank": 3,
      "name": '任务3',
      "value": '10 分钟'
    },
    {
      "rank": 4,
      "name": '任务4',
      "value": '10 分钟'
    },
    {
      "rank": 5,
      "name": '任务5',
      "value": '10 分钟'
    },
  ]);

  const columns: ProColumns<DataType>[] = [
    {
      title: '排名',
      dataIndex: 'rank',
      valueType: 'indexBorder',
    },
    {
      title: '任务名',
      dataIndex: 'name',
    },
    {
      title: '已异常时间',
      dataIndex: 'value',
    },
  ];

  const config : ProgressConfig = {
    height: 50,
    width: 200,
    autoFit: false,
    percent: 0.7,
    color: ['#5B8FF9', '#acaeb0'],
  };

  return <>
    <StatisticCard
      chartPlacement="right"
      statistic={{
        title: '当前未处理失败',
        value: 3,
        suffix: '个',
        formatter: (value)=> <CountFormatter value={Number(value)}/>,
        description: (
          <Space>
            <Statistic
              title="今日失败"
              value='10 '
              suffix='个'
            />
            <Statistic
              title="已处理"
              value="70%"
            />
          </Space>
        ),
      }}
      chart={
        <div className={styles['tiny-charts']}>
          <Progress {...config} />
        </div>
      }
    />
    <ProTable columns={columns} dataSource={data} search={false} toolBarRender={false} pagination={false} size="small"/>
  </>;
};

export default JobErrorView
