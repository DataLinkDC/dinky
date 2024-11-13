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

import { Alert, ALERT_TYPE, Cluster } from '@/types/RegCenter/data.d';
import { DefaultOptionType } from 'antd/es/select';
import { TagAlignLeft } from '@/components/StyledComponents';
import { getAlertIcon } from '@/pages/RegCenter/Alert/AlertInstance/function';
import { l } from '@/utils/intl';

export const calculatorWidth = (width: number) => {
  const resultWidth = width - 50; // 50 为右侧 proform list 组件的 删除按钮宽度
  return resultWidth > 0 ? resultWidth / 2 : 300;
};

/**
 * build job alert groups
 */
export const buildAlertGroupOptions = (alertGroups: Alert.AlertGroup[] = []) => {
  const alertGroupOptions: DefaultOptionType[] = [
    {
      label: (
        <TagAlignLeft>
          {getAlertIcon(ALERT_TYPE.GROUP, 20)}
          {l('button.disable')}
        </TagAlignLeft>
      ),
      title: l('button.disable'),
      value: -1,
      key: -1
    }
  ];
  alertGroups.forEach((item) => {
    alertGroupOptions.push({
      label: (
        <TagAlignLeft>
          {getAlertIcon(ALERT_TYPE.GROUP, 20)}
          {item.name}
        </TagAlignLeft>
      ),
      value: item.id,
      title: item.name,
      key: item.id
    });
  });
  return alertGroupOptions;
};
