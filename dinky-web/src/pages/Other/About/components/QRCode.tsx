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

import { AlertRestProps, ImageRestProps, TypographyRestProps } from '@/pages/Other/About';
import { VERSION } from '@/services/constants';
import { l } from '@/utils/intl';
import { SmileOutlined } from '@ant-design/icons';
import { Alert, Image, Typography } from 'antd';

const { Paragraph } = Typography;

export const QRCode = () => {
  return (
    <>
      <Alert
        message={l('about.Community', '', { version: VERSION })}
        type='success'
        icon={<SmileOutlined />}
        {...AlertRestProps}
      />
      <Paragraph>
        <Typography.Text title={l('about.QQcode')} {...TypographyRestProps}>
          <Image title={l('about.QQcode')} {...ImageRestProps} src='community/qq.png' />
        </Typography.Text>

        <Typography.Text {...TypographyRestProps}>
          <Image title={l('about.wechatCode')} {...ImageRestProps} src='community/wechat.jpg' />
        </Typography.Text>

        <Typography.Text {...TypographyRestProps}>
          <Image title={l('about.dingTalkCode')} {...ImageRestProps} src='community/dingtalk.jpg' />
        </Typography.Text>
      </Paragraph>
    </>
  );
};
