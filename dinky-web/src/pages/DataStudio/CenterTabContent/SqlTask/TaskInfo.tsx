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
import { Descriptions } from 'antd';
import Paragraph from 'antd/es/typography/Paragraph';
import { TaskState } from '@/pages/DataStudio/type';
import { showFirstLevelOwner, showSecondLevelOwners } from '@/pages/DataStudio/function';
import { UserBaseInfo } from '@/types/AuthCenter/data';

export const TaskInfo = (props: { params: TaskState; users: UserBaseInfo.User[] }) => {
  const {
    params: { taskId, name, dialect, versionId, firstLevelOwner, secondLevelOwners },
    users
  } = props;

  return (
    <div style={{ paddingInline: 8 }}>
      <Descriptions bordered size='small' column={1}>
        <Descriptions.Item label={l('pages.datastudio.label.jobInfo.id')}>
          <Paragraph copyable>{taskId}</Paragraph>
        </Descriptions.Item>
        <Descriptions.Item label={l('pages.datastudio.label.jobInfo.name')}>
          {name}
        </Descriptions.Item>
        <Descriptions.Item label={l('pages.datastudio.label.jobInfo.dialect')}>
          {dialect}
        </Descriptions.Item>
        <Descriptions.Item label={l('pages.datastudio.label.jobInfo.versionId')}>
          {versionId}
        </Descriptions.Item>
        <Descriptions.Item label={l('pages.datastudio.label.jobInfo.firstLevelOwner')}>
          {showFirstLevelOwner(firstLevelOwner, users)}
        </Descriptions.Item>
        <Descriptions.Item label={l('pages.datastudio.label.jobInfo.secondLevelOwners')}>
          {showSecondLevelOwners(secondLevelOwners, users)}
        </Descriptions.Item>
      </Descriptions>
    </div>
  );
};
