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

import SlowlyAppear from '@/components/Animation/SlowlyAppear';
import JobList from '@/pages/DevOps/JobList';
import JobOverview from '@/pages/DevOps/JobList/components/Overview';
import { PageContainer } from '@ant-design/pro-layout';
import { Space } from 'antd';
import { createContext, useState } from 'react';

export const DevopContext = createContext({});

export default () => {
  const [statusFilter, setStatusFilter] = useState();

  /**
   * breadcrumb={{style:{display:'none'}}} is used to hide the breadcrumb || 隐藏右上角的路由导航面包屑
   */
  return (
    <SlowlyAppear>
      <PageContainer title={false} breadcrumb={{ style: { display: 'none' } }}>
        <DevopContext.Provider value={{ statusFilter, setStatusFilter }}>
          <Space direction='vertical' size={10}>
            <JobOverview />
            <JobList />
          </Space>
        </DevopContext.Provider>
      </PageContainer>
    </SlowlyAppear>
  );
};
