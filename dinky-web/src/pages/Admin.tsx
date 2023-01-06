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


import React from 'react';
import {HeartTwoTone, SmileTwoTone} from '@ant-design/icons';
import {Alert, Card, Typography} from 'antd';
import {PageHeaderWrapper} from '@ant-design/pro-layout';
import {l} from "@/utils/intl";
import {VERSION} from "@/components/Common/Version";

export default (): React.ReactNode => {

  return (
    <PageHeaderWrapper
      content={l('pages.admin.subPage.title')}
    >
      <Card>
        <Alert
          message={l('pages.welcome.alertMessage', '', {version: VERSION})}
          type="success"
          showIcon
          banner
          style={{
            margin: -12,
            marginBottom: 48,
          }}
        />
        <Typography.Title level={2} style={{textAlign: 'center'}}>
          <SmileTwoTone/> Ant Design Pro <HeartTwoTone twoToneColor="#eb2f96"/> You
        </Typography.Title>
      </Card>
      <p style={{textAlign: 'center', marginTop: 24}}>
        Want to add more pages? Please refer to{' '}
        <a href="https://pro.ant.design/docs/block-cn" target="_blank" rel="noopener noreferrer">
          use block
        </a>
        。
      </p>
    </PageHeaderWrapper>
  );
};
