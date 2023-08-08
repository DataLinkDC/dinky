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

import {TagAlignLeft} from "@/components/StyledComponents";
import {Badge, Tag} from "antd";
import {Alert, Cluster} from "@/types/RegCenter/data";
import {PaperClipOutlined} from "@ant-design/icons";
import {l} from "@/utils/intl";
import {DefaultOptionType} from "antd/es/select";
import {RUN_MODE} from "@/services/constants";

/**
 * build job run model
 * @returns {DefaultOptionType[]}
 */
export const buildRunModelOptions = () => {
    let resultReturn: DefaultOptionType[] = [];
    resultReturn.push(
        {
            label: 'Local',
            value: RUN_MODE.LOCAL,
        },
        {
            label: 'Standalone',
            value: RUN_MODE.STANDALONE,
        },
        {
            label: 'Yarn Session',
            value: RUN_MODE.YARN_SESSION,
        },
        {
            label: 'Yarn Per-Job',
            value: RUN_MODE.YARN_PER_JOB,
        },
        {
            label: 'Yarn Application',
            value: RUN_MODE.YARN_APPLICATION,
        },
        {
            label: 'Kubernetes Session',
            value: RUN_MODE.KUBERNETES_SESSION,
        },
        {
            label: 'Kubernetes Application',
            value: RUN_MODE.KUBERNETES_APPLICATION,
        },
        {
            label: 'Kubernetes Operator Application',
            value: RUN_MODE.KUBERNETES_APPLICATION_OPERATOR,
        }
    );

    return resultReturn;
}


/**
 * build cluster options
 * @param {Cluster.Instance[]} sessionCluster
 * @returns {any[]}
 */
export const buildClusterOptions = (sessionCluster: Cluster.Instance[]) => {
    const sessionClusterOptions: DefaultOptionType[] = [];

    for (const item of sessionCluster) {
        const tag = (
            <TagAlignLeft>
                <Tag color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.name}
            </TagAlignLeft>
        );
        sessionClusterOptions.push({
            label: tag,
            value: item.id,
            key: item.id,
        })
    }
    return sessionClusterOptions;
};

/**
 *  build cluster config options
 * @param current
 * @param {Cluster.Config[]} clusterConfiguration
 * @returns {any[]}
 */
export const buildClusterConfigOptions = (current: any, clusterConfiguration: Cluster.Config[]) => {
    const clusterConfigOptions: DefaultOptionType[]  = [];
    for (const item of clusterConfiguration) {
        if (current.type.search(item.type.toLowerCase()) === -1) {
            continue;
        }
        const tag = (
            <TagAlignLeft>
                <Tag color={item.enabled ? "processing" : "error"}>{item.type}</Tag>{item.name}
            </TagAlignLeft>
        );
        clusterConfigOptions.push({
            label: tag,
            value: item.id,
            key: item.id,
        })
    }
    return clusterConfigOptions;
};

/**
 * build env options
 * @param {any[]} env
 * @returns {JSX.Element[]}
 */
export const buildEnvOptions = (env: any[]) => {
    const envList:DefaultOptionType[] = [
        {
            label: l('button.disable'),
            value: 0,
            key: 0,
        }
    ];
    for (const item of env) {
        const tag = (
            <>
                <TagAlignLeft>
                    {item.enabled ? <Badge status="success"/> : <Badge status="error"/>}{item.fragment ?
                    <PaperClipOutlined/> : undefined}{item.name}
                </TagAlignLeft>
            </>
        );
        envList.push({
            label: tag,
            value: item.id,
            key: item.id,
        })
    }
    return envList;
};

/**
 * build job alert groups
 * @param {Alert.AlertGroup[]} alertGroups
 * @returns {JSX.Element[]}
 */
export const buildAlertGroupOptions = (alertGroups: Alert.AlertGroup[]) => {
    const alertGroupOptions:DefaultOptionType[] = [{
        label: l('button.disable'),
        value: 0,
    }];
    for (const item of alertGroups) {
        alertGroupOptions.push({
            label: item.name,
            value: item.id,
            key: item.id,
        })
    }
    return alertGroupOptions;
};