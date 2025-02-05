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

import React, { useState } from 'react';
import { Alert, Space } from 'antd';
import { l } from '@/utils/intl';
import useHookRequest from '@/hooks/useHookRequest';
import { getAllConfig } from '@/pages/Metrics/service';
import SlowlyAppear from '@/components/Animation/SlowlyAppear';
import { PageContainer, ProCard } from '@ant-design/pro-components';
import ApprovalTable from '@/pages/AuthCenter/Approval/components/ApprovalTable';

const ApprovalFormList: React.FC = () => {
  const [activeKey, setActiveKey] = useState('reviewList');
  const [tabs, setTabs] = useState([]);

  const tabList = [
    {
      key: 'reviewList',
      label: <Space>{l('approval.reviewList')}</Space>,
      children: <ApprovalTable tableType={'review'} />
    },
    {
      key: 'submitList',
      label: <Space>{l('approval.submitList')}</Space>,
      children: <ApprovalTable tableType={'submit'} />
    }
  ];

  const showServer = useHookRequest(getAllConfig, {
    defaultParams: [],
    onSuccess: (res: any) => {
      for (const config of res.approval) {
        if (config.key === 'sys.approval.settings.enableTaskSubmitReview') {
          if (config.value) {
            setTabs(tabList as []);
            return true;
          }
        }
      }
      return false;
    }
  });

  /**
   * render
   */
  return (
    <SlowlyAppear>
      <PageContainer
        loading={showServer.loading}
        subTitle={
          !showServer.data && (
            <Alert message={l('approval.dinky.not.open')} type={'warning'} banner showIcon />
          )
        }
        title={false}
      >
        <ProCard
          ghost
          className={'schemeTree'}
          size={'small'}
          bordered
          tabs={{
            size: 'small',
            activeKey: activeKey,
            type: 'card',
            animated: true,
            onChange: (key: string) => setActiveKey(key),
            items: tabs
          }}
        />
      </PageContainer>
    </SlowlyAppear>
  );
};

export default ApprovalFormList;
