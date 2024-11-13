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

import { Button, Col, Flex, Row, Space, Tooltip } from 'antd';
import { l } from '@/utils/intl';
import { QuestionCircleOutlined, SwapOutlined } from '@ant-design/icons';
import { ProFormGroup, ProFormList, ProFormText } from '@ant-design/pro-components';
import FlinkOptionsSelect from '@/components/Flink/OptionsSelect';
import React, { useState } from 'react';
import { DefaultOptionType } from 'antd/es/select';
import CodeShow from '@/components/CustomEditor/CodeShow';
import { calculatorWidth } from '@/pages/DataStudioNew/CenterTabContent/SqlTask/TaskConfig/function';

export const ProFormFlinkConfig = (props: {
  containerWidth: number;
  flinkConfigOptions: DefaultOptionType[];
  getCode: () => string;
}) => {
  const { containerWidth, flinkConfigOptions, getCode } = props;
  const [showDesc, setShowDesc] = useState(false);

  const renderFlinkConfig = () => {
    if (!showDesc) {
      return (
        <ProFormList
          name={['configJson', 'customConfig']}
          copyIconProps={false}
          creatorButtonProps={{
            style: { width: '100%' },
            creatorButtonText: l('pages.datastudio.label.jobConfig.addConfig')
          }}
        >
          <ProFormGroup>
            <Space key={'config'} align='baseline'>
              {/* bugfix 这个不采用分组的形式，有bug*/}
              <FlinkOptionsSelect
                name='key'
                width={calculatorWidth(containerWidth) + 50}
                mode={'single'}
                allowClear
                showSearch
                placeholder={l('pages.datastudio.label.jobConfig.addConfig.params')}
                options={removeDuplicates(
                  flinkConfigOptions.flatMap((x) => x.children!!),
                  'value'
                )}
              />
              <ProFormText
                name={'value'}
                width={calculatorWidth(containerWidth) - 30}
                placeholder={l('pages.datastudio.label.jobConfig.addConfig.value')}
              />
            </Space>
          </ProFormGroup>
        </ProFormList>
      );
    } else {
      return <CodeShow code={getCode()} />;
    }
  };
  return (
    <Col>
      <Flex justify={'space-between'}>
        <Row style={{ gap: 6 }} align={'middle'}>
          <span>{l('pages.datastudio.label.jobConfig.other')}</span>
          <Tooltip title={l('pages.datastudio.label.jobConfig.other.tip')}>
            <QuestionCircleOutlined />
          </Tooltip>
        </Row>

        <Button
          icon={<SwapOutlined />}
          type={'text'}
          variant={'text'}
          onClick={() => {
            setShowDesc(!showDesc);
          }}
        ></Button>
      </Flex>
      {renderFlinkConfig()}
    </Col>
  );
};
function removeDuplicates(arr: DefaultOptionType[], key: string): DefaultOptionType[] {
  const seen = new Map();
  return arr.filter((item) => {
    const keyValue = item[key];
    if (seen.has(keyValue)) {
      return false;
    }
    seen.set(keyValue, true);
    return true;
  });
}
