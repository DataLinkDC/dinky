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

import {DOCKER_CONFIG_LIST} from "@/pages/RegCenter/Cluster/Configuration/components/contants";
import {ProFormGroup, ProFormText} from "@ant-design/pro-components";
import {Divider} from "antd";
import {l} from "@/utils/intl";
import React from "react";

const DockerConfig = () => {

    const renderDockerConfigForm = () => {
        return DOCKER_CONFIG_LIST.map(item => <>
                <ProFormText
                    width={300}
                    key={item.name}
                    name={item.name}
                    label={item.label}
                    tooltip={item.tooltip}
                    placeholder={item.placeholder}
                    initialValue={item.defaultValue}
                />
            </>
        );
    }

    return <>
        <Divider>{l('rc.cc.dockerConfig')}</Divider>
        <ProFormGroup>
            {renderDockerConfigForm()}
        </ProFormGroup>

    </>
};


export default DockerConfig