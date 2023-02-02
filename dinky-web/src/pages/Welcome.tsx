/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {VERSION} from '@/components/Version/Version';
import {l} from '@/utils/intl';
import {PageContainer} from '@ant-design/pro-components';
import {Alert, Card, Image, Typography} from 'antd';
import React from 'react';

const { Paragraph} = Typography;

const Welcome: React.FC = () => {
  return (
    <PageContainer title={false} >
      <Card>
        <Alert
          message={l('pages.welcome.alertMessage', '', {version: VERSION})}
          type="success"
          showIcon
          banner
          style={{
            margin: -12,
            marginBottom: 24,
          }}
        />
        <Typography.Text strong>
          {l('pages.welcome.Community')}
        </Typography.Text>
        <br/><br/>
        <Paragraph style={{marginRight: 30}}>
          <Typography.Text title={l('pages.welcome.QQcode')} style={{marginRight: 80}} strong ellipsis={true}>
            <Image title={l('pages.welcome.QQcode')} height={300} width={250} src="community/qq.png"/>
          </Typography.Text>

          <Typography.Text style={{marginRight: 80}} strong ellipsis={true}>
            <Image title={l('pages.welcome.wechatCode')} height={300} width={250} src="community/wechat.jpg"/>
          </Typography.Text>

          <Typography.Text strong ellipsis={true}>
            <Image title={l('pages.welcome.dingTalkCode')} height={300} width={250} src="community/dingtalk.jpg"/>
          </Typography.Text>

        </Paragraph>

      </Card>
    </PageContainer>
  );
};

export default Welcome;



