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

import { LoginLog } from '@/types/AuthCenter/data';
import { l } from '@/utils/intl';
import { Space, Typography } from 'antd';
import { TimelineItemProps } from 'antd/es/timeline/TimelineItem';
import moment from 'moment';

const { Text } = Typography;

export const renderTimeLineItems = (loginRecord: LoginLog[]): TimelineItemProps[] => {
  return loginRecord
    .filter((item) => !item.isDeleted)
    .map((item) => {
      const renderTitle = (
        <Space direction='vertical'>
          <Text>
            {l('user.username')}: {item.username}
          </Text>
          <Text type={item.status === 10004 ? 'success' : 'danger'}>
            {l('user.login.status.code')}: {item.status}
          </Text>
          <Text type={item.status === 10004 ? 'success' : 'danger'}>
            {l('user.login.status.msg')}: {item.msg}
          </Text>
        </Space>
      );

      const renderLabel = (
        <>
          <Space direction='vertical'>
            <Text>
              {l('user.login.accesstime')}: {moment(item.accessTime).format('YYYY-MM-DD HH:mm:ss')}
            </Text>
            <Text>
              {l('user.login.ip')}: {item.ip}
            </Text>
          </Space>
        </>
      );

      return {
        key: item.id,
        color: item.status === 10004 ? 'green' : 'red',
        label: renderLabel,
        children: renderTitle
      };
    });
};
