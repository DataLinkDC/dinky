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
import {ProCard, ProFormGroup, ProFormItem, ProFormSelect, ProFormText} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import React from "react";
import {KUBERNETES_CONFIG_LIST} from "@/pages/RegCenter/Cluster/Configuration/components/contants";
import CodeEdit from "@/components/CustomEditor/CodeEdit";
import {ClusterType} from "@/pages/RegCenter/Cluster/constants";


const CodeEditProps = {
    height: '30vh',
    width: '60vw',
    lineNumbers: 'on',
    language: 'yaml',
};

const FlinkK8sOperator = (props: any) => {


    const renderFlinkK8sOperatorConfig = () => {
        return KUBERNETES_CONFIG_LIST
            .filter(item => item.showOnSubmitType !== ClusterType.KUBERNETES_NATIVE && item.showType !== 'code')
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
    };

    const renderFlinkK8sOperatorConfigCode = () => {
        return KUBERNETES_CONFIG_LIST
            .filter(item => item.showType === 'code')
            .map(item => <>
                    <ProFormItem
                        key={item.name}
                        name={item.name}
                        label={item.label}
                        tooltip={item.tooltip}
                    >
                        <CodeEdit {...CodeEditProps} onChange={() => {
                        }} code={props.code}/>
                    </ProFormItem>
                </>
            );
    }


    return <>
        <Divider>{l('rc.cc.k8sConfig')}</Divider>
        <Row gutter={[16, 16]}>
            <Col span={10}>
                <ProFormGroup>
                    <ProFormSelect
                        name={'flinkVersion'}
                        label={l('rc.cc.k8sOp.version')}
                        width={250}
                        placeholder={l('rc.cc.k8sOp.versionHelp')}
                        options={[
                            {label: '1.13' + l('rc.cc.k8sOp.unSupportBatch'), value: 'v1_13'},
                            {label: '1.14' + l('rc.cc.k8sOp.unSupportBatch'), value: 'v1_14'},
                            {label: '1.15', value: 'v1_15'},
                            {label: '1.16', value: 'v1_16'},
                            {label: '1.17', value: 'v1_17'},
                            {label: '1.18', value: 'v1_18', disabled: true},
                        ]}
                    />
                    {renderFlinkK8sOperatorConfig()}
                </ProFormGroup>
            </Col>
            <ProCard.Divider type={'vertical'}/>
            <Col span={12}>
                {renderFlinkK8sOperatorConfigCode()}
            </Col>
        </Row>

    </>
}


export default FlinkK8sOperator;