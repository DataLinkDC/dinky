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


import {Col, Divider, FormListFieldData, Row} from "antd";
import {l} from "@/utils/intl";
import {ProCard, ProFormGroup, ProFormList, ProFormText} from "@ant-design/pro-components";
import React from "react";
import {UploadOutlined} from "@ant-design/icons";
import {FLINK_CONFIG_LIST} from "@/pages/RegCenter/Cluster/Configuration/components/contants";

const HadoopConfig = () => {
    return <>
        <Row gutter={[16, 16]}>
            <Col span={12}>
                <Divider>{l('rc.cc.hadoopConfig')}</Divider>
                <ProFormGroup>
                    <ProFormText
                        name="hadoopConfigPath"
                        label={l('rc.cc.hadoopConfigPath')}
                        width="xl"
                        rules={[{required: true, message: l('rc.cc.hadoopConfigPathPlaceholder')}]}
                        placeholder={l('rc.cc.hadoopConfigPathPlaceholder')}
                        tooltip={l('rc.cc.hadoopConfigPathHelp')}
                        addonAfter={<UploadOutlined/>}
                    />

                    <ProFormText
                        name="ha.zookeeper.quorum"
                        label={l('rc.cc.zkHost')}
                        width="xl"
                        placeholder={l('rc.cc.zkHostPlaceholder')}
                        tooltip={l('rc.cc.zkHostHelp')}
                    />
                </ProFormGroup>
                <Divider>{l('rc.cc.hadoop.defineConfig')}</Divider>
                <ProFormList
                    name="hadoopConfigList"
                    label={l('rc.cc.otherConfig')}
                    copyIconProps={false}
                    deleteIconProps={{
                        tooltipText: l('rc.cc.deleteConfig'),
                    }}
                    creatorButtonProps={{
                        style: {width: '32vw'},
                        creatorButtonText: l('rc.cc.addConfig'),
                    }}
                >
                    <ProFormGroup key="hadoopGroup">
                        <ProFormText width={'md'} name="name" label={l('rc.cc.key')}/>
                        <ProFormText width={'sm'} name="value" label={l('rc.cc.value')}/>
                    </ProFormGroup>
                </ProFormList>
            </Col>

            <ProCard.Divider type={'vertical'}/>

            <Col span={11}>
                <Divider>{l('rc.cc.flinkConfig')}</Divider>
                <ProFormGroup>
                    <ProFormText
                        name="flinkLibPath"
                        label={l('rc.cc.libPath')}
                        width="xl"
                        rules={[{required: true, message: l('rc.cc.libPathPlaceholder')}]}
                        placeholder={l('rc.cc.libPathPlaceholder')}
                        tooltip={l('rc.cc.libPathHelp')}
                        addonAfter={<UploadOutlined/>}
                    />

                    <ProFormText
                        name="flinkConfigPath"
                        label={l('rc.cc.flinkConfigPath')}
                        width="xl"
                        rules={[{required: true, message: l('rc.cc.flinkConfigPathPlaceholder')}]}
                        placeholder={l('rc.cc.flinkConfigPathPlaceholder')}
                        tooltip={l('rc.cc.flinkConfigPathHelp')}
                        addonAfter={<UploadOutlined/>}
                    />
                </ProFormGroup>

                <Divider>{l('rc.cc.flink.defineConfig')}</Divider>
                <ProFormList
                    name="flinkConfigList"
                    label={l('rc.cc.otherConfig')}
                    copyIconProps={false}
                    deleteIconProps={{
                        tooltipText: l('rc.cc.deleteConfig'),
                    }}
                    creatorButtonProps={{
                        style: {width: '32vw'},
                        creatorButtonText: l('rc.cc.addConfig'),
                    }}
                >
                    <ProFormGroup key="flinkGroup">
                        <ProFormText width={'md'} name="name" label={l('rc.cc.key')}/>
                        <ProFormText width={'sm'} name="value" label={l('rc.cc.value')}/>
                    </ProFormGroup>
                </ProFormList>
            </Col>
        </Row>


    </>
}

export default HadoopConfig;