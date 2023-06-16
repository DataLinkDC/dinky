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


import {Divider} from "antd";
import {l} from "@/utils/intl";
import {ProFormGroup, ProFormText} from "@ant-design/pro-components";
import React from "react";
import {UploadOutlined} from "@ant-design/icons";

const HadoopConfig = () => {
    return <>
        <Divider>{l('rc.cc.hadoopConfig')}</Divider>
        <ProFormGroup>
            <ProFormText
                name="hadoopConfigPath"
                label={l('rc.cc.hadoopConfigPath')}
                width="lg"
                rules={[{required: true, message: l('rc.cc.hadoopConfigPathPlaceholder')}]}
                placeholder={l('rc.cc.hadoopConfigPathPlaceholder')}
                tooltip={l('rc.cc.hadoopConfigPathHelp')}
                addonAfter={<UploadOutlined/>}
            />

            <ProFormText
                name="ha.zookeeper.quorum"
                label={'ha.zookeeper.quorum'}
                width="lg"
                rules={[{required: true, message: l('rc.cc.hadoopConfigPathPlaceholder')}]}
                placeholder={'192.168.123.1:2181,192.168.123.2:2181,192.168.123.3:2181'}
                tooltip={l('Zk quorum config')}
            />


        </ProFormGroup>
    </>
}

export default HadoopConfig;