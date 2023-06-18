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
import {l} from "@/utils/intl";
import React from "react";
import {KUBERNETES_CONFIG_LIST} from "@/pages/RegCenter/Cluster/Configuration/components/contants";
import {ProCard, ProFormGroup, ProFormList, ProFormText} from "@ant-design/pro-components";
import {ClusterType} from "@/pages/RegCenter/Cluster/constants";

const K8sConfig = () => {

    const renderK8sConfig = () => {
        return KUBERNETES_CONFIG_LIST
            .filter(item => item.showOnSubmitType !== ClusterType.KUBERNETES_OPERATOR)
            .map(item => <>
                    <ProFormText
                        width={250}
                        key={item.name}
                        name={item.name}
                        label={item.label}
                        tooltip={item.tooltip}
                        placeholder={item.placeholder}
                    />
                </>
            );
    }


    return <>
        <Divider>{l('rc.cc.k8sConfig')}</Divider>

        <Row gutter={[16, 16]}>
            <Col span={10}>
                <Divider>{l('rc.cc.k8s.preConfig')}</Divider>
                <ProFormGroup>
                    {renderK8sConfig()}
                </ProFormGroup>
            </Col>
            <ProCard.Divider type={'vertical'}/>

            <Col span={12}>
                <Divider>{l('rc.cc.k8s.defineConfig')}</Divider>

                <ProFormList
                    name="kubernetesConfigList"
                    copyIconProps={false}
                    deleteIconProps={{
                        tooltipText: l('rc.cc.deleteConfig'),
                    }}
                    creatorButtonProps={{
                        style: {width: '32vw'},
                        creatorButtonText: l('rc.cc.addConfig'),
                    }}
                >
                    <ProFormGroup key="flinkK8sNativeGroup">
                        <ProFormText width={'md'} name="name" label={l('rc.cc.key')}/>
                        <ProFormText width={'sm'} name="value" label={l('rc.cc.value')}/>
                    </ProFormGroup>
                </ProFormList>
            </Col>
        </Row>
    </>;
}

export default K8sConfig;