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
import {ProFormText} from "@ant-design/pro-components";
import React from "react";

const FlinkKubernetesNative = () => {

    return <>
        <Divider>{l('rc.cc.flinkKubernetesNativeConfig')}</Divider>
        <ProFormText
            name="flinkKubernetesNativeConfigPath"
            label={l('rc.cc.flinkKubernetesNativeConfigPath')}
            width="md"
            rules={[{required: true, message: l('rc.cc.flinkKubernetesNativeConfigPathPlaceholder')}]}
            placeholder={l('rc.cc.flinkKubernetesNativeConfigPathPlaceholder')}
            help={l('rc.cc.flinkKubernetesNativeConfigPathHelp')}
        />
    </>;

}


export default FlinkKubernetesNative;