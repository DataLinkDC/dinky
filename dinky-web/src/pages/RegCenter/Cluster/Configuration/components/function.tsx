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

import {Cluster} from "@/types/RegCenter/data";


/**
 * parse values to Cluster.Config
 * @param {Cluster.Config} values
 * @returns {{type: string} | {[p: string]: any, [p: number]: any, [p: symbol]: any, hadoopConfigPath: any, flinkConfigPath: any, flinkConfigList: any, type: string, flinkVersion: any, enabled: boolean, flinkConfig: any, hadoopConfigList: any, dockerConfig: any, flinkLibPath: any, kubernetesConfig: any, hadoopConfig: any, name: string, id: number, userJarPath: any}}
 */
export function parseConfigJsonToValues(values: Cluster.Config) {

    if (!values.id) {
        return {type: values.type};
    }
    // base info
    const {type, id, name, enabled, configJson} = values;

    // parse configJson
    let config = JSON.parse(configJson);
    // extract configJson from config
    const {
        hadoopConfigPath, flinkLibPath, flinkConfigPath, flinkVersion,
        hadoopConfig, userJarPath, kubernetesConfig, dockerConfig,
        flinkConfig, hadoopConfigList, flinkConfigList, ...rest
    } = config;

    // return values
    return {
        id,
        name,
        enabled,
        type,
        ...rest, // ...rest 代表剩余参数
        hadoopConfigPath,
        flinkLibPath,
        flinkConfigPath,
        flinkVersion,
        hadoopConfigList, // 使用此种方式 + antd 的 ProFormList 组件，可以实现动态表单 自动渲染该数组
        flinkConfigList, // 使用此种方式 + antd 的 ProFormList 组件，可以实现动态表单 自动渲染该数组
        hadoopConfig,
        kubernetesConfig,
        dockerConfig,
        userJarPath,
        flinkConfig,
    }
}


/**
 * build Cluster.Config from values
 * @param values
 * @returns {Cluster.Config}
 */
export function buildClusterConfig(values: any): Cluster.Config {

    // extract values
    const {
        type, name, enabled, note,
        hadoopConfigPath, flinkLibPath, flinkConfigPath, flinkVersion,
        hadoopConfig, userJarPath, kubernetesConfig, dockerConfig,
        flinkConfig, hadoopConfigList, flinkConfigList, ...rest
    } = values;

    // build configJson
    const configJson = {
        hadoopConfigPath, flinkLibPath, flinkConfigPath, flinkVersion,
        hadoopConfig, userJarPath, kubernetesConfig, dockerConfig,
        flinkConfig, hadoopConfigList, flinkConfigList, ...rest // ...rest 代表剩余参数
    };

    // deep copy configJson
    const buildConfigJsonResult = JSON.stringify(configJson);


    // return values
    return {
        name,
        enabled,
        note,
        type,
        configJson: buildConfigJsonResult,
    } as Cluster.Config;
}

