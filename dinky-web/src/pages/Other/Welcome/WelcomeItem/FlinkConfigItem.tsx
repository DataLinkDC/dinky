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

import { Flex, Input, Space, Switch, Typography } from 'antd';

import { WelcomePic1 } from '@/components/Icons/WelcomeIcons';
import FormItem from 'antd/es/form/FormItem';
import { WelcomeProps } from 'src/pages/Other/Welcome';
import { LoadingBtn } from '@/components/CallBackButton/LoadingBtn';
import { l } from '@/utils/intl';

const { Title, Text, Link } = Typography;

const FlinkConfigItem = (prop: WelcomeProps) => {
  return (
    <Flex>
      <div>
        <Space>
          <Title>{l('welcome.flink.config.title')}</Title>
        </Space>
        <br />
        <Text type={'success'}>{l('welcome.tips')}</Text>
        <br />
        <br />

        <FormItem label={l('welcome.flink.config.jobwait.title')}>
          <Text type={'secondary'}>{l('welcome.flink.config.jobwait')}</Text>
          <FormItem name='sys.flink.settings.jobIdWait'>
            <Input type={'number'} />
          </FormItem>
        </FormItem>

        <FormItem label={l('welcome.flink.config.useHistoryServer.title')}>
          <Text type={'secondary'}>{l('welcome.flink.config.useHistoryServer')}</Text>
          <br />
          <FormItem name='sys.flink.settings.useFlinkHistoryServer'>
            <Switch />
          </FormItem>
        </FormItem>

        <FormItem label={l('welcome.flink.config.historyPort.title')}>
          <Text type={'secondary'}>{l('welcome.flink.config.historyPort')}</Text>
          <FormItem name='sys.flink.settings.flinkHistoryServerPort'>
            <Input type={'number'} />
          </FormItem>
        </FormItem>

        <LoadingBtn
          props={{
            type: 'primary',
            size: 'large'
          }}
          title={l('welcome.submit')}
          click={async () => await prop.onSubmit?.()}
        />

        <Link onClick={prop.onPrev}> {l('welcome.prev')}</Link>
      </div>
      <WelcomePic1 size={500} />
    </Flex>
  );
};
export default FlinkConfigItem;
