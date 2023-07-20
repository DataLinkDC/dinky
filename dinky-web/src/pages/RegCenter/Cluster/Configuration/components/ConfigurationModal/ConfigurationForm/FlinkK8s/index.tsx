/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import {Col, Divider, Row} from "antd";
import {ProCard, ProFormGroup, ProFormItem, ProFormList, ProFormSelect, ProFormText} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import React from "react";
import CodeEdit from "@/components/CustomEditor/CodeEdit";
import {ClusterType} from "@/pages/RegCenter/Cluster/constants";
import {TagAlignCenter} from "@/components/StyledComponents";
import {
  ExposedTypeOptions, versionOptions
} from "@/pages/RegCenter/Cluster/Configuration/components/ConfigurationModal/ConfigurationForm/FlinkK8s/contants";
import {KUBERNETES_CONFIG_LIST} from "@/pages/RegCenter/Cluster/Configuration/components/contants";


const CodeEditProps = {
  height: '40vh',
  width: '90vh',
  lineNumbers: 'on',
  language: 'yaml',
};

const FlinkK8s = (props: { type: string, value: any }) => {

  const {type, value} = props;

  const renderK8sConfig = () => {
    return <>
      {KUBERNETES_CONFIG_LIST
        .map(item =>
          <ProFormText
            tooltip={item.tooltip}
            key={item.name}
            name={['configJson', 'kubernetesConfig','configuration', item.name]}
            label={item.label}
            width={250}
            placeholder={item.placeholder}/>
        )}
      <ProFormList
        name={['configJson','flinkConfig', 'flinkConfigList']}
        copyIconProps={false}
        deleteIconProps={{tooltipText: l('rc.cc.deleteConfig'),}}
        creatorButtonProps={{style: {width: '32vw'}, creatorButtonText: l('rc.cc.addConfig'),}}
      >
        <ProFormGroup key="flinkGroup">
          <ProFormText width={'md'} name="name" label={l('rc.cc.key')}/>
          <ProFormText width={'sm'} name="value" label={l('rc.cc.value')}/>
        </ProFormGroup>
      </ProFormList>
    </>
  };

  const configTags = [
    {
      key: "defaultPodTemplate",
      label: <TagAlignCenter>Default Pod Template</TagAlignCenter>,
      children:
        <ProFormItem key="dpe" name={['configJson', 'kubernetesConfig', 'podTemplate']}>
          <CodeEdit {...CodeEditProps} code={value.configJson.kubernetesConfig.podTemplate}/>
        </ProFormItem>
    },
    {
      key: "JMPodTemplate",
      label: <TagAlignCenter>JM Pod Template</TagAlignCenter>,
      children:
        <ProFormItem key="jmdpe" name={['configJson', 'kubernetesConfig', 'jmPodTemplate']}>
          <CodeEdit {...CodeEditProps} code={value.configJson.kubernetesConfig.jmPodTemplate}/>
        </ProFormItem>
    },
    {
      key: "TMPodTemplate",
      label: <TagAlignCenter>TM Pod Template</TagAlignCenter>,
      children:
        <ProFormItem key="tmdpe" name={['configJson', 'kubernetesConfig', 'tmPodTemplate']}>
          <CodeEdit {...CodeEditProps} code={value.configJson.kubernetesConfig.tmPodTemplate}/>
        </ProFormItem>
    },
  ];

  return <>
    <Divider>{l('rc.cc.k8sConfig')}</Divider>
    <Row gutter={[16, 16]}>
      <Col span={10}>
        <ProFormGroup>
          {(type && type === ClusterType.KUBERNETES_NATIVE) &&
            <ProFormSelect
              name={['configJson', 'kubernetesConfig','configuration', 'kubernetes.rest-service.exposed.type']}
              label={l('rc.cc.k8s.exposed')}
              tooltip={l('rc.cc.k8s.exposedHelp')}
              placeholder={l('rc.cc.k8s.exposedHelp')}
              options={ExposedTypeOptions}
              width={250}
            />}
          {(type && type === ClusterType.KUBERNETES_OPERATOR) &&
            <ProFormSelect
              name={['configJson','flinkConfig', 'flinkVersion']}
              label={l('rc.cc.k8sOp.version')}
              width={250}
              placeholder={l('rc.cc.k8sOp.versionHelp')}
              options={versionOptions}
            />}
          {renderK8sConfig()}
        </ProFormGroup>
      </Col>
      <ProCard.Divider type={'vertical'}/>
      <Col span={12}>
        <ProCard size="small" tabs={{type: 'card', items: configTags}}/>
      </Col>
    </Row>
  </>
}

export default FlinkK8s;
