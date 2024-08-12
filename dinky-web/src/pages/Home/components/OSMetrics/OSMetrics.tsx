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

import useHookRequest from '@/hooks/useHookRequest';
import { getData } from '@/services/api';
import { API_CONSTANTS } from '@/services/endpoints';
import { Card, Col, Descriptions, Divider, Progress, ProgressProps, Row, Space } from 'antd';
import { DescriptionsItemType } from 'antd/es/descriptions';
import React from 'react';
import { Bytes2Mb } from '@/utils/function';
import { ProgressType } from 'antd/es/progress/progress';

const OSMetrics = () => {
  const { loading, data } = useHookRequest<any, any>(getData, {
    defaultParams: [API_CONSTANTS.SYSTEM_JVM_INFO]
  });
  const cpu = data?.cpu;
  const mem = data?.mem;
  const jvm = data?.jvm;
  const systemInfo = data?.systemInfo;

  const renderProgress = (max: number, free: number, unit: string, type: ProgressType = 'line') => {
    max = Math.floor(max);
    free = Math.floor(free);
    const used = Math.floor(max - free);
    const precent = (used / max) * 100;

    const twoColors: ProgressProps['strokeColor'] = {
      '0%': '#d9d9d9',
      '100%': '#d9d9d9'
    };
    return (
      <Progress
        percent={precent}
        size={type === 'dashboard' ? 50 : [-1, 15]}
        percentPosition={{ align: 'center', type: 'inner' }}
        format={() => `${used}${unit}`}
        strokeColor={twoColors}
        type={type}
      />
    );
  };

  const items: DescriptionsItemType[] = [
    {
      key: 'osName',
      label: 'Os Name',
      children: systemInfo?.osName
    },
    {
      key: 'osArch',
      label: 'Os Arch',
      children: systemInfo?.osArch
    },
    {
      key: 'cpuNum',
      label: 'Cpu Number',
      children: cpu?.cpuNum
    },
    {
      key: 'jvmVersion',
      label: 'Jvm Version',
      children: systemInfo?.jvmVersion
    },
    {
      key: 'jvmName',
      label: 'Jvm Name',
      children: systemInfo?.jvmName
    },
    {
      key: 'osVersion',
      label: 'Os Version',
      children: systemInfo?.osVersion
    }
  ];

  const metricsItem: DescriptionsItemType[] = [
    {
      key: 'Heap',
      label: 'Heap',
      children: renderProgress(Bytes2Mb(jvm?.heapMax), Bytes2Mb(jvm?.heapMax - jvm?.heapUsed), 'MB')
    },
    {
      key: 'Jvm',
      label: 'Jvm',
      children: renderProgress(Bytes2Mb(jvm?.total), Bytes2Mb(jvm?.free), 'MB')
    },
    {
      key: 'Mem',
      label: 'Mem',
      children: renderProgress(Bytes2Mb(mem?.total), Bytes2Mb(mem?.free), 'MB')
    },
    {
      key: 'nonHeap',
      label: 'nonHeap',
      children: renderProgress(
        Bytes2Mb(jvm?.nonHeapMax),
        Bytes2Mb(jvm?.nonHeapMax - jvm?.nonHeapUsed),
        'MB'
      )
    }
  ];

  return (
    <Card
      // size={"small"}
      style={{
        marginBottom: 24
      }}
      bordered={false}
      title={systemInfo?.computerName}
      loading={loading}
    >
      <Row>
        <Col span={12}>
          <Descriptions layout={'vertical'} items={items} column={2} />
        </Col>
        <Divider style={{ height: '100%' }} type={'vertical'} />
        <Col span={10}>
          <Descriptions layout={'horizontal'} items={metricsItem} column={1} />
          <Space>
            <Space>Cpu:{renderProgress(100, data?.cpu?.free, '%', 'dashboard')}</Space>
            <Space>
              Mem:
              {renderProgress(
                Bytes2Mb(mem?.total) / 1024,
                Bytes2Mb(mem?.free) / 1024,
                'GB',
                'dashboard'
              )}
            </Space>
          </Space>
          {/*<Space>jvmCpu:{renderProgress(100, (1-jvm?.cpuUsed)*100, "%")}</Space>*/}
        </Col>
      </Row>
    </Card>
  );
};
export default OSMetrics;
