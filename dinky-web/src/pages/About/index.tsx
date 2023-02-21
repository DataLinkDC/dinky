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

import { VERSION } from '@/components/Version/Version';
import { l } from '@/utils/intl';
import { HeartOutlined, LinkOutlined, SmileOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { Alert, Card, Image, Typography } from 'antd';
import React from 'react';

const { Paragraph } = Typography;

const About: React.FC = () => {
  return (
    <PageContainer title={false}>
      <Card>
        <Alert
          message={l('pages.about.Community', '', { version: VERSION })}
          type="success"
          icon={<SmileOutlined height={30} width={30} />}
          showIcon
          banner
          style={{
            margin: -12,
            marginBottom: 24,
          }}
        />
        <Paragraph style={{ marginRight: 30 }}>
          <Typography.Text
            title={l('pages.about.QQcode')}
            style={{ marginRight: 80 }}
            strong
            ellipsis={true}
          >
            <Image
              title={l('pages.about.QQcode')}
              height={300}
              width={250}
              src="community/qq.png"
            />
          </Typography.Text>

          <Typography.Text style={{ marginRight: 80 }} strong ellipsis={true}>
            <Image
              title={l('pages.about.wechatCode')}
              height={300}
              width={250}
              src="community/wechat.jpg"
            />
          </Typography.Text>

          <Typography.Text strong ellipsis={true}>
            <Image
              title={l('pages.about.dingTalkCode')}
              height={300}
              width={250}
              src="community/dingtalk.jpg"
            />
          </Typography.Text>
        </Paragraph>
        <br />

        <Alert
          message={l('pages.about.precautions')}
          description={l('pages.about.wechatApply')}
          type="warning"
          showIcon
          banner
          style={{
            margin: -12,
            marginBottom: 24,
          }}
        />
        <br />
        <br />

        <Typography.Text strong>
          <Alert
            message={l('pages.about.usingHelp')}
            icon={<SmileOutlined />}
            type="info"
            showIcon
            banner
            style={{
              margin: -12,
              marginBottom: 24,
            }}
          />
          <Paragraph style={{ marginRight: 30 }}>
            <Typography.Text ellipsis strong>
              <LinkOutlined /> GitHub：
              <a href={'https://github.com/DataLinkDC/dinky'} target={'_blank'}>
                https://github.com/DataLinkDC/dinky
              </a>
            </Typography.Text>
            <br />
            <Typography.Text ellipsis strong>
              <LinkOutlined /> Gitee:{' '}
              <a href={'https://gitee.com/DataLinkDC/Dinky'} target={'_blank'}>
                https://gitee.com/DataLinkDC/Dinky
              </a>
            </Typography.Text>
            <br />
            <Typography.Text ellipsis strong>
              <LinkOutlined /> Document:{' '}
              <a href={'http://www.dlink.top'} target={'_blank'}>
                http://www.dlink.top
              </a>
            </Typography.Text>
            <br />
            <Typography.Text ellipsis strong>
              <LinkOutlined /> bilibili:{' '}
              <a href={'https://space.bilibili.com/366484959/video'} target={'_blank'}>
                https://space.bilibili.com/366484959/video
              </a>
            </Typography.Text>
          </Paragraph>
          <br />
          <Alert
            message={l('pages.about.communityRules')}
            type="success"
            showIcon
            icon={<HeartOutlined style={{ color: 'red' }} />}
            banner
            style={{
              margin: -12,
              marginBottom: 24,
            }}
          />
          <ul>
            <li>{l('pages.about.communityRules.1')}</li>
            <li>{l('pages.about.communityRules.2')}</li>
            <li>{l('pages.about.communityRules.3')}</li>
            <li>{l('pages.about.communityRules.4')}</li>
            <li>
              <a href={'https://github.com/DataLinkDC/dinky/issues/66'} target={'_blank'}>
                Issue
              </a>
              {l('pages.about.communityRules.5')}
            </li>
          </ul>
        </Typography.Text>
      </Card>
    </PageContainer>
  );
};

export default About;
