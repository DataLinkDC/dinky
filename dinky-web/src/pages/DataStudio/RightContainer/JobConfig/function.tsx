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

import { TagAlignLeft } from '@/components/StyledComponents';
import { getAlertIcon } from '@/pages/RegCenter/Alert/AlertInstance/function';
import { RUN_MODE } from '@/services/constants';
import { Alert, ALERT_TYPE, Cluster } from '@/types/RegCenter/data.d';
import { l } from '@/utils/intl';
import { PaperClipOutlined } from '@ant-design/icons';
import { Badge, Tag } from 'antd';
import { DefaultOptionType } from 'antd/es/select';

/**
 * build job run model
 */
export const buildRunModelOptions = () => {
  let resultReturn: DefaultOptionType[] = [];
  resultReturn.push(
    {
      label: 'Local',
      value: RUN_MODE.LOCAL
    },
    {
      label: 'Standalone',
      value: RUN_MODE.STANDALONE
    },
    {
      label: 'Yarn Session',
      value: RUN_MODE.YARN_SESSION
    },
    {
      label: 'Yarn Per-Job',
      value: RUN_MODE.YARN_PER_JOB
    },
    {
      label: 'Yarn Application',
      value: RUN_MODE.YARN_APPLICATION
    },
    {
      label: 'Kubernetes Session',
      value: RUN_MODE.KUBERNETES_SESSION
    },
    {
      label: 'Kubernetes Application',
      value: RUN_MODE.KUBERNETES_APPLICATION
    },
    {
      label: 'Kubernetes Operator Application',
      value: RUN_MODE.KUBERNETES_APPLICATION_OPERATOR
    }
  );

  return resultReturn;
};

/**
 * build cluster options
 */
export const buildClusterOptions = (sessionCluster: Cluster.Instance[]) => {
  const sessionClusterOptions: DefaultOptionType[] = [];

  for (const item of sessionCluster) {
    const tag = (
      <TagAlignLeft>
        <Tag color={item.enabled ? 'processing' : 'error'}>{item.type}</Tag>
        {item.name}
      </TagAlignLeft>
    );
    sessionClusterOptions.push({
      label: tag,
      value: item.id,
      key: item.id
    });
  }
  return sessionClusterOptions;
};

/**
 *  build cluster config options
 */
export const buildClusterConfigOptions = (current: any, clusterConfiguration: Cluster.Config[]) => {
  const clusterConfigOptions: DefaultOptionType[] = [];
  for (const item of clusterConfiguration) {
    if (current.type.search(item.type.toLowerCase()) === -1) {
      continue;
    }
    const tag = (
      <TagAlignLeft>
        <Tag color={item.enabled ? 'processing' : 'error'}>{item.type}</Tag>
        {item.name}
      </TagAlignLeft>
    );
    clusterConfigOptions.push({
      label: tag,
      value: item.id,
      key: item.id
    });
  }
  return clusterConfigOptions;
};

/**
 * build env options
 */
export const buildEnvOptions = (env: any[]) => {
  const envList: DefaultOptionType[] = [
    {
      label: l('button.disable'),
      value: -1,
      key: -1
    }
  ];

  for (const item of env) {
    const tag = (
      <TagAlignLeft>
        {item.enabled ? <Badge status='success' /> : <Badge status='error' />}
        {item.fragment ? <PaperClipOutlined /> : undefined}
        {item.name}
      </TagAlignLeft>
    );

    envList.push({
      label: tag,
      value: item.id,
      key: item.id,
      disabled: !item.enabled
    });
  }
  return envList;
};

/**
 * build job alert groups
 */
export const buildAlertGroupOptions = (alertGroups: Alert.AlertGroup[]) => {
  const alertGroupOptions: DefaultOptionType[] = [
    {
      label: (
        <TagAlignLeft>
          {getAlertIcon(ALERT_TYPE.GROUP, 20)}
          {l('button.disable')}
        </TagAlignLeft>
      ),
      value: -1,
      key: -1
    }
  ];
  alertGroups?.forEach((item) => {
    alertGroupOptions.push({
      label: (
        <TagAlignLeft>
          {getAlertIcon(ALERT_TYPE.GROUP, 20)}
          {item.name}
        </TagAlignLeft>
      ),
      value: item.id,
      key: item.id
    });
  });
  return alertGroupOptions;
};

/**
 * 计算右侧 proform list 组件宽度
 * @param width
 */
export const calculatorWidth = (width: number) => {
  const resultWidth = width - 50; // 50 为右侧 proform list 组件的 删除按钮宽度
  return resultWidth > 0 ? resultWidth / 2 : 300;
};
