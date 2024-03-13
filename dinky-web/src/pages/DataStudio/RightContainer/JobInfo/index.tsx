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

import { getCurrentData } from '@/pages/DataStudio/function';
import { StateType } from '@/pages/DataStudio/model';
import { l } from '@/utils/intl';
import { connect } from '@umijs/max';
import { Descriptions } from 'antd';
import Paragraph from 'antd/es/typography/Paragraph';

const JobInfo = (props: any) => {
  const {
    tabs: { panes, activeKey }
  } = props;

  const currentInfo = getCurrentData(panes, activeKey);

  return (
    <div style={{ paddingInline: 8 }}>
      <Descriptions bordered size='small' column={1}>
        <Descriptions.Item label={l('pages.datastudio.label.jobInfo.id')}>
          <Paragraph copyable>{currentInfo?.id}</Paragraph>
        </Descriptions.Item>
        <Descriptions.Item label={l('pages.datastudio.label.jobInfo.name')}>
          {currentInfo?.name}
        </Descriptions.Item>
        <Descriptions.Item label={l('pages.datastudio.label.jobInfo.dialect')}>
          {currentInfo?.dialect}
        </Descriptions.Item>
        <Descriptions.Item label={l('pages.datastudio.label.jobInfo.versionId')}>
          {currentInfo?.versionId}
        </Descriptions.Item>
      </Descriptions>
    </div>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  tabs: Studio.tabs
}))(JobInfo);
