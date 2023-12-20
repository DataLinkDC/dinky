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
import { UploadOutlined } from '@ant-design/icons';
import {
  ProCard,
  ProFormGroup,
  ProFormItem,
  ProFormList,
  ProFormSelect,
  ProFormText
} from '@ant-design/pro-components';
import { Button, Col, Divider, Row, Space, Typography, Upload, UploadProps } from 'antd';
import { FormInstance } from 'antd/es/form/hooks/useForm';
import { RcFile } from 'antd/es/upload/interface';
import { Values } from 'async-validator';
import { useState } from 'react';

const { Text } = Typography;

const CodeEditProps = {
  height: '30vh',
  width: '90vh',
  lineNumbers: 'on',
  language: 'yaml'
};

const FlinkK8s = (props: { type: string; value: any; form: FormInstance<Values> } & connect) => {
  const { type, value, form, flinkConfigOptions } = props;
  const k8sConfig = value.config?.kubernetesConfig;

  const [kubeConfig, setKubeConfig] = useState<string>(k8sConfig?.kubeConfig);
  const [podTemplate, setPodTemplate] = useState<string>(k8sConfig?.podTemplate);
  const [jmPodTemplate, setJmPodTemplate] = useState<string>(k8sConfig?.jmPodTemplate);
  const [tmPodTemplate, setTmPodTemplate] = useState<string>(k8sConfig?.tmPodTemplate);

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
            rules={item.rules ?? []}
            placeholder={item.placeholder}
          />
        ))}
      </>
    );
  };

  const renderEdit = (
    name: string[],
    onChange: (value: string) => void,
    key: string,
    value?: string,
    tips?: string
  ) => {
    const uploadProp: UploadProps = {
      beforeUpload: (file: RcFile) => {
        const reader = new FileReader();
        reader.readAsText(file);
        reader.onload = () => {
          form.setFieldValue(name, reader.result as string);
          onChange(reader.result as string);
        };
      },
      showUploadList: false
    };
    return (
      <Space direction={'vertical'}>
        <Upload {...uploadProp}>
          <Button icon={<UploadOutlined />}>{l('rc.cc.loadFromLocal')}</Button>
          <Text type={'secondary'}> {tips}</Text>
        </Upload>
        <ProFormItem key={key} name={name}>
          <CodeEdit {...CodeEditProps} code={value ?? ''} />
        </ProFormItem>
      </Space>
    );
  };

  const configTags = [
    {
      key: 'kubeConfig',
      forceRender: true,
      label: <TagAlignCenter>K8s KubeConfig</TagAlignCenter>,
      children: renderEdit(
        ['config', 'kubernetesConfig', 'kubeConfig'],
        (value) => setKubeConfig(value),
        'k8s-kubeconfig-item',
        kubeConfig,
        l('rc.cc.k8s.defaultKubeConfigHelp')
      )
    },
    {
      key: 'defaultPodTemplate',
      forceRender: true,
      label: <TagAlignCenter>Default Pod Template</TagAlignCenter>,
      children: renderEdit(
        ['config', 'kubernetesConfig', 'podTemplate'],
        (value) => setPodTemplate(value),
        'k8s-podTemplate-item',
        podTemplate
      )
    },
    {
      key: 'JMPodTemplate',
      forceRender: true,
      label: <TagAlignCenter>JM Pod Template</TagAlignCenter>,
      children: renderEdit(
        ['config', 'kubernetesConfig', 'jmPodTemplate'],
        (value) => setJmPodTemplate(value),
        'k8s-jmPodTemplate-item',
        jmPodTemplate
      )
    },
    {
      key: 'TMPodTemplate',
      forceRender: true,
      label: <TagAlignCenter>TM Pod Template</TagAlignCenter>,
      children: renderEdit(
        ['config', 'kubernetesConfig', 'tmPodTemplate'],
        (value) => setTmPodTemplate(value),
        'k8s-tmPodTemplate-item',
        tmPodTemplate
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
                rules={[{ required: true }]}
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
                rules={[{ required: true }]}
              />
            )}
            {renderK8sConfig()}
            <ProFormText
              name={['config', 'clusterConfig', 'flinkConfigPath']}
              label={l('rc.cc.flinkConfigPath')}
              placeholder={l('rc.cc.flinkConfigPathPlaceholder')}
              tooltip={l('rc.cc.flinkConfigPathHelp')}
            />
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
