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

import FlinkOptionsSelect from '@/components/Flink/OptionsSelect';
import { StateType } from '@/pages/DataStudio/model';
import { l } from '@/utils/intl';
import { connect } from '@@/exports';
import { ProCard, ProFormGroup, ProFormList, ProFormText } from '@ant-design/pro-components';
import { Col, Divider, Row, Space } from 'antd';
import { DefaultOptionType } from 'antd/es/select';

const YarnConfig = (props: { flinkConfigOptions: DefaultOptionType[] }) => {
  const { flinkConfigOptions } = props;
  return (
    <>
      <Row gutter={[16, 16]}>
        <Col span={10}>
          <Divider>{l('rc.cc.hadoopConfig')}</Divider>
          <ProFormGroup>
            <ProFormText
              name={['config', 'clusterConfig', 'hadoopConfigPath']}
              label={l('rc.cc.hadoopConfigPath')}
              width='md'
              rules={[
                {
                  required: true,
                  message: l('rc.cc.hadoopConfigPathPlaceholder')
                }
              ]}
              placeholder={l('rc.cc.hadoopConfigPathPlaceholder')}
              tooltip={l('rc.cc.hadoopConfigPathHelp')}
            />
          </ProFormGroup>
          <Divider>{l('rc.cc.hadoop.defineConfig')}</Divider>
          <ProFormList
            name={['config', 'clusterConfig', 'hadoopConfigList']}
            copyIconProps={false}
            deleteIconProps={{
              tooltipText: l('rc.cc.deleteConfig')
            }}
            creatorButtonProps={{
              style: { width: '100%' },
              creatorButtonText: l('rc.cc.addConfig')
            }}
          >
            <ProFormGroup key='hadoopGroup' style={{ width: '100%' }}>
              <Space key={'config'} style={{ width: '100%' }} align='baseline'>
                <ProFormText width={'md'} name='name' placeholder={l('rc.cc.key')} />
                <ProFormText width={'sm'} name='value' placeholder={l('rc.cc.value')} />
              </Space>
            </ProFormGroup>
          </ProFormList>
        </Col>

        <ProCard.Divider type={'vertical'} />

        <Col span={13}>
          <Divider>{l('rc.cc.flinkConfig')}</Divider>
          <ProFormGroup>
            <ProFormText
              name={['config', 'clusterConfig', 'flinkLibPath']}
              label={l('rc.cc.libPath')}
              width='md'
              rules={[{ required: true, message: l('rc.cc.libPathPlaceholder') }]}
              placeholder={l('rc.cc.libPathPlaceholder')}
              tooltip={l('rc.cc.libPathHelp')}
            />

            <ProFormText
              name={['config', 'clusterConfig', 'flinkConfigPath']}
              label={l('rc.cc.flinkConfigPath')}
              width='md'
              rules={[
                {
                  required: true,
                  message: l('rc.cc.flinkConfigPathPlaceholder')
                }
              ]}
              placeholder={l('rc.cc.flinkConfigPathPlaceholder')}
              tooltip={l('rc.cc.flinkConfigPathHelp')}
            />
          </ProFormGroup>

          <Divider>{l('rc.cc.flink.defineConfig')}</Divider>
          <ProFormList
            name={['config', 'flinkConfig', 'flinkConfigList']}
            copyIconProps={false}
            deleteIconProps={{
              tooltipText: l('rc.cc.deleteConfig')
            }}
            creatorButtonProps={{
              style: { width: '100%' },
              creatorButtonText: l('rc.cc.addConfig')
            }}
          >
            <ProFormGroup key='flinkGroup' style={{ display: 'flex', width: '100%' }}>
              <Space key={'config'} style={{ display: 'flex' }} align='baseline'>
                <FlinkOptionsSelect
                  name='name'
                  width={'md'}
                  mode={'single'}
                  allowClear
                  showSearch
                  placeholder={l('rc.cc.key')}
                  options={flinkConfigOptions}
                />
                <ProFormText width={'sm'} name='value' placeholder={l('rc.cc.value')} />
              </Space>
            </ProFormGroup>
          </ProFormList>
        </Col>
      </Row>
    </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  flinkConfigOptions: Studio.flinkConfigOptions
}))(YarnConfig);
