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

import { l } from '@/utils/intl';
import { Avatar, Descriptions, Divider, Tag } from 'antd';

const BaseInfo = (props: any) => {
  const { user, tenant } = props;
  return (
    <>
      <Avatar
        style={{ alignSelf: 'center', marginBottom: 50 }}
        draggable
        size={200}
        src={user.avatar}
      />

      <Divider orientation={'left'} plain>
        {l('user.info')}
      </Divider>
      <Descriptions size={'small'} column={2}>
        <Descriptions.Item label={l('user.username')}>{user.username}</Descriptions.Item>
        <Descriptions.Item label={l('user.nickname')}>{user.nickname}</Descriptions.Item>
        <Descriptions.Item label={l('user.phone')}>{user.mobile}</Descriptions.Item>
        <Descriptions.Item label={l('user.jobnumber')}>{user.worknum}</Descriptions.Item>
        <Descriptions.Item label={l('user.current.tenant')}>
          <Tag color={'processing'}>{tenant?.tenantCode}</Tag>
        </Descriptions.Item>
      </Descriptions>
    </>
  );
};

export default BaseInfo;
