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

import CodeEdit from '@/components/CustomEditor/CodeEdit';
import { TagAlignCenter } from '@/components/StyledComponents';
import { StateType } from '@/pages/DataStudio/model';
import {
  ExposedTypeOptions,
  versionOptions
} from '@/pages/RegCenter/Cluster/Configuration/components/ConfigurationModal/ConfigurationForm/FlinkK8s/contants';
import { KUBERNETES_CONFIG_LIST } from '@/pages/RegCenter/Cluster/Configuration/components/contants';
import { ClusterType } from '@/pages/RegCenter/Cluster/constants';
import { l } from '@/utils/intl';
import { connect } from '@@/exports';
import {
  ProCard,
  ProFormGroup,
  ProFormItem,
  ProFormList,
  ProFormSelect,
  ProFormText
} from '@ant-design/pro-components';
import { Col, Divider, Row, Space } from 'antd';

const CodeEditProps = {
  height: '40vh',
  width: '90vh',
  lineNumbers: 'on',
  language: 'yaml'
};

const FlinkK8s = (props: { type: string; value: any } & connect) => {
  const { type, value, flinkConfigOptions } = props;

  const renderK8sConfig = () => {
    return (
      <>
        {KUBERNETES_CONFIG_LIST.map((item) => (
          <ProFormText
            tooltip={item.tooltip}
            key={item.name}
            name={['config', 'kubernetesConfig', 'configuration', item.name]}
            label={item.label}
            width={260}
            placeholder={item.placeholder}
          />
        ))}
      </>
    );
  };

  const configTags = [
    {
      key: 'defaultPodTemplate',
      label: <TagAlignCenter>Default Pod Template</TagAlignCenter>,
      children: (
        <ProFormItem key='dpe' name={['config', 'kubernetesConfig', 'podTemplate']}>
          <CodeEdit {...CodeEditProps} code={value.config?.kubernetesConfig?.podTemplate ?? ''} />
        </ProFormItem>
      )
    },
    {
      key: 'JMPodTemplate',
      label: <TagAlignCenter>JM Pod Template</TagAlignCenter>,
      children: (
        <ProFormItem key='jmdpe' name={['config', 'kubernetesConfig', 'jmPodTemplate']}>
          <CodeEdit {...CodeEditProps} code={value.config?.kubernetesConfig?.jmPodTemplate ?? ''} />
        </ProFormItem>
      )
    },
    {
      key: 'TMPodTemplate',
      label: <TagAlignCenter>TM Pod Template</TagAlignCenter>,
      children: (
        <ProFormItem key='tmdpe' name={['config', 'kubernetesConfig', 'tmPodTemplate']}>
          <CodeEdit {...CodeEditProps} code={value.config?.kubernetesConfig?.tmPodTemplate ?? ''} />
        </ProFormItem>
      )
    }
  ];

  return (
    <>
      <Divider>{l('rc.cc.k8sConfig')}</Divider>
      <Row gutter={[16, 16]}>
        <Col span={10}>
          <ProFormGroup>
            {type && type === ClusterType.KUBERNETES_NATIVE && (
              <ProFormSelect
                name={[
                  'config',
                  'kubernetesConfig',
                  'configuration',
                  'kubernetes.rest-service.exposed.type'
                ]}
                label={l('rc.cc.k8s.exposed')}
                tooltip={l('rc.cc.k8s.exposedHelp')}
                placeholder={l('rc.cc.k8s.exposedHelp')}
                options={ExposedTypeOptions}
                width={250}
              />
            )}
            {type && type === ClusterType.KUBERNETES_OPERATOR && (
              <ProFormSelect
                name={['config', 'flinkConfig', 'flinkVersion']}
                label={l('rc.cc.k8sOp.version')}
                width={250}
                placeholder={l('rc.cc.k8sOp.versionHelp')}
                options={versionOptions}
              />
            )}
            {renderK8sConfig()}
          </ProFormGroup>
          <ProFormList
            name={['config', 'flinkConfig', 'flinkConfigList']}
            copyIconProps={false}
            deleteIconProps={{ tooltipText: l('rc.cc.deleteConfig') }}
            creatorButtonProps={{
              style: { width: '100%' },
              creatorButtonText: l('rc.cc.addConfig')
            }}
          >
            <ProFormGroup key='flinkGroup'>
              <Space key={'config'} style={{ display: 'flex' }} align='baseline'>
                <ProFormSelect
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
        <ProCard.Divider type={'vertical'} />
        <Col span={12}>
          <ProCard size='small' tabs={{ type: 'card', items: configTags }} />
        </Col>
      </Row>
    </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  flinkConfigOptions: Studio.flinkConfigOptions
}))(FlinkK8s);
