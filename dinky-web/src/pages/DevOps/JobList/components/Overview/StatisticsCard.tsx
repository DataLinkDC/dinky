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

import { ProCard } from '@ant-design/pro-components';
import { Divider, Space, Typography } from 'antd';

const { Text, Link } = Typography;

type StatisticsCardParams = {
  title: string;
  value?: string | number;
  icon: any;
  link?: string;
  extra?: any;
  divider?: boolean;
  atClick?: () => void;
};
const StatisticsCard = (props: StatisticsCardParams) => {
  const { title, value, icon, extra = <></>, divider = true, link, atClick } = props;
  return (
    <>
      <ProCard layout={'center'} onClick={() => (atClick ? atClick() : {})} hoverable={true}>
        <Space size={20}>
          {icon}
          <Space direction='vertical'>
            <Text>{title}</Text>
            <Text style={{ fontSize: 30 }}>{value}</Text>
          </Space>
          {extra}
        </Space>
      </ProCard>
      {divider ? <Divider type={'vertical'} /> : <></>}
    </>
  );
};
export default StatisticsCard;
