/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, {memo, useEffect, useState} from 'react';
import {API_CONSTANTS, RESPONSE_CODE} from '@/services/constants';
import {handleOption, queryDataByParams} from '@/services/BusinessCrud';
import {BaseConfigProperties, Settings} from '@/types/SettingCenter/data';
import {EnvConfig} from '@/pages/SettingCenter/GlobalSetting/SettingOverView/EnvConfig';
import {FlinkConfig} from '@/pages/SettingCenter/GlobalSetting/SettingOverView/FlinkConfig';
import {MavenConfig} from '@/pages/SettingCenter/GlobalSetting/SettingOverView/MavenConfig';
import {DSConfig} from '@/pages/SettingCenter/GlobalSetting/SettingOverView/DSConfig';
import {LdapConfig} from "@/pages/SettingCenter/GlobalSetting/SettingOverView/LdapConfig";
import {l} from '@/utils/intl';
import FadeIn from "@/components/Animation/FadeIn";
import {ResourcesConfig} from "@/pages/SettingCenter/GlobalSetting/SettingOverView/ResourcesConfig";
import {Space} from "antd";
import {LogSvg} from "@/components/Icons/CodeLanguageIcon";
import {ProCard} from "@ant-design/pro-components";
import {SettingConfigKeyEnum} from "@/pages/SettingCenter/GlobalSetting/SettingOverView/constants";

const SettingOverView = () => {

    const [activeKey, setActiveKey] = useState(SettingConfigKeyEnum.DINKY);

    const [data, setData] = useState<Settings>({
        dolphinscheduler: [],
        env: [],
        flink: [],
        maven: [],
        ldap: [],
        metrics: [],
        resource: []
    });


    const fetchData = async () => {
        await queryDataByParams(API_CONSTANTS.SYSTEM_GET_ALL_CONFIG).then((res) => {
            setData(res);
        });
    };

    useEffect(() => {
        fetchData();
    }, []);

    const handleSaveSubmit = async (dataConfig: BaseConfigProperties) => {
        const {code} = await handleOption(API_CONSTANTS.SYSTEM_MODIFY_CONFIG, l('sys.setting.modify', '', {key: l(`sys.${dataConfig.key}`)}), dataConfig);

        if (code === RESPONSE_CODE.ERROR) {
            await fetchData()
        } else {
            // @ts-ignore
            for (const d of data[dataConfig.key.split('.')[0]]) {
                if (d.key === dataConfig.key) {
                    d.value = dataConfig.value
                    break
                }
            }
        }
    };

    const renderData = () => {
        if (data) {
            const {
                env: dinkyEnv, flink: flinkConfig, maven: mavenConfig,
                dolphinscheduler: dsConfig, ldap: ldapConfig, metrics: metricsConfig,
                resource: resourceConfig,
            } = data;

            const configTags = [
                {
                    key: SettingConfigKeyEnum.DINKY,
                    label: <><LogSvg/>{l('sys.setting.dinky')}</>,
                    children: <EnvConfig onSave={handleSaveSubmit} data={dinkyEnv}/>,
                },
                {
                    key: SettingConfigKeyEnum.FLINK,
                    label: <><LogSvg/>{l('sys.setting.flink')}</>,
                    children: <FlinkConfig onSave={handleSaveSubmit} data={flinkConfig}/>,
                },
                {
                    key: SettingConfigKeyEnum.MAVEN,
                    label: <><LogSvg/>{l('sys.setting.maven')}</>,
                    children: <MavenConfig onSave={handleSaveSubmit} data={mavenConfig}/>,
                },
                {
                    key: SettingConfigKeyEnum.DOLPHIN_SCHEDULER,
                    label: <><LogSvg/>{l('sys.setting.ds')}</>,
                    children: <DSConfig onSave={handleSaveSubmit} data={dsConfig}/>,
                },
                {
                    key: SettingConfigKeyEnum.LDAP,
                    label: <><LogSvg/>{l('sys.setting.ldap')}</>,
                    children: <LdapConfig onSave={handleSaveSubmit} data={ldapConfig}/>,
                },
                {
                    key: SettingConfigKeyEnum.METRIC,
                    label: <><LogSvg/>{l('sys.setting.metrics')}</>,
                    children: <DSConfig onSave={handleSaveSubmit} data={metricsConfig}/>,
                },
                {
                    key: SettingConfigKeyEnum.RESOURCE,
                    label: <><LogSvg/>{l('sys.setting.resource')}</>,
                    children: <ResourcesConfig onSave={handleSaveSubmit} data={resourceConfig}/>,
                },
            ];


            return <div style={{paddingBottom: "20px"}}>
                <ProCard
                    ghost
                    className={'schemaTree'}
                    size="small"
                    bordered
                    tabs={{
                        activeKey: activeKey,
                        type: 'card',
                        animated: true,
                        onChange: (key: any) => setActiveKey(key),
                        items: configTags,
                    }}
                />
            </div>;
        }
    };


    return <FadeIn>
        {renderData()}
    </FadeIn>;
};

export default memo(SettingOverView);
