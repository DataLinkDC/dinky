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

import { Button, Flex, Input, Space, Typography } from 'antd';
import { Congratulations, WelcomePic1 } from '@/components/Icons/WelcomeIcons';
import FormItem from 'antd/es/form/FormItem';
import { WelcomeProps } from 'src/pages/Other/Welcome';
import { l } from '@/utils/intl';

const { Title, Text, Link } = Typography;

const WelcomeItem = (prop: WelcomeProps) => {
  return (
    <Flex>
      <div>
        <Space>
          <Congratulations size={60} />
          <Title>{l('welcome.welcome')}</Title>
        </Space>
        <br />
        <Text type={'secondary'}>{l('welcome.welcome.content')}</Text>
        <br />
        <br />
        <Text>{l('welcome.welcome.content.tip1')}</Text>
        <br />
        <Text>{l('welcome.welcome.content.tip2')}</Text>
        <Flex
          style={{ width: '80%', height: '80%' }}
          justify={'center'}
          align={'flex-start'}
          gap={'middle'}
          vertical
        >
          <Title level={4}>{l('welcome.welcome.setPwd.tip')}</Title>

          <FormItem name='password' style={{ width: '100%', margin: '0px' }}>
            <Input placeholder='password' type={'password'} size={'large'} />
          </FormItem>

          <Button type={'primary'} style={{ width: '100%' }} size={'large'} onClick={prop.onNext}>
            {l('welcome.welcome.setPwd')}
          </Button>
          <Link onClick={() => prop.onNext()}>{l('welcome.welcome.skip')}</Link>
        </Flex>
      </div>
      <WelcomePic1 size={500} />
    </Flex>
  );
};
export default WelcomeItem;
