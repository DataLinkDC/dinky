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

import { AlertRestProps, TypographyRestProps } from '@/pages/Other/About';
import { l } from '@/utils/intl';
import { LinkOutlined, SmileOutlined } from '@ant-design/icons';
import { Alert, Typography } from 'antd';

const { Paragraph } = Typography;

export const UsingHelp = () => {
  return (
    <>
      <Alert
        message={l('about.usingHelp')}
        icon={<SmileOutlined />}
        type='info'
        {...AlertRestProps}
      />
      <Paragraph>
        <Typography.Text {...TypographyRestProps}>
          <LinkOutlined /> GitHub：
          <a href={'https://github.com/DataLinkDC/dinky'} target={'_blank'} rel='noreferrer'>
            https://github.com/DataLinkDC/dinky
          </a>
        </Typography.Text>
        <br />
        <Typography.Text {...TypographyRestProps}>
          <LinkOutlined /> Gitee:{' '}
          <a href={'https://gitee.com/DataLinkDC/Dinky'} target={'_blank'} rel='noreferrer'>
            https://gitee.com/DataLinkDC/Dinky
          </a>
        </Typography.Text>
        <br />
        <Typography.Text {...TypographyRestProps}>
          <LinkOutlined /> Document:{' '}
          <a href={'http://www.dlink.top'} target={'_blank'} rel='noreferrer'>
            http://www.dlink.top
          </a>
        </Typography.Text>
        <br />
        <Typography.Text {...TypographyRestProps}>
          <LinkOutlined /> bilibili:{' '}
          <a href={'https://space.bilibili.com/366484959/video'} target={'_blank'} rel='noreferrer'>
            https://space.bilibili.com/366484959/video
          </a>
        </Typography.Text>
      </Paragraph>
    </>
  );
};
