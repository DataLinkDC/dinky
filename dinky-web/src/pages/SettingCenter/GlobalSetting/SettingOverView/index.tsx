/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import FadeIn from '@/components/Animation/FadeIn';
import {
  DinkyIcon,
  DSIcon,
  FlinkIcon,
  LDAPIcon,
  MavenIcon,
  MetricsIcon,
  ResourceIcon
} from '@/components/Icons/CustomIcons';
import { TagAlignCenter } from '@/components/StyledComponents';
import { SettingConfigKeyEnum } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/constants';
import { DSConfig } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/DSConfig';
import { EnvConfig } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/EnvConfig';
import { FlinkConfig } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/FlinkConfig';
import { LdapConfig } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/LdapConfig';
import { MavenConfig } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/MavenConfig';
import { MetricsConfig } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/MetricsConfig';
import { ResourcesConfig } from '@/pages/SettingCenter/GlobalSetting/SettingOverView/ResourcesConfig';
import { handleOption, queryDataByParams } from '@/services/BusinessCrud';
import { RESPONSE_CODE } from '@/services/constants';
import { API_CONSTANTS } from '@/services/endpoints';
import { BaseConfigProperties, Settings } from '@/types/SettingCenter/data';
import { l } from '@/utils/intl';
import { ProCard } from '@ant-design/pro-components';
import { memo, useEffect, useState } from 'react';

const imgSize = 25;

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
    await queryDataByParams<Settings>(API_CONSTANTS.SYSTEM_GET_ALL_CONFIG).then((res) => {
      if (res) {
        setData(res);
      }
    });
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSaveSubmit = async (dataConfig: BaseConfigProperties) => {
    const { code } =
      (await handleOption(
        API_CONSTANTS.SYSTEM_MODIFY_CONFIG,
        l('sys.setting.modify', '', { key: dataConfig.name }),
        dataConfig
      )) ?? {};

    if (code === RESPONSE_CODE.ERROR) {
      await fetchData();
    } else {
      // @ts-ignore
      for (const d of data[dataConfig.key.split('.')[1]]) {
        if (d.key === dataConfig.key) {
          d.value = dataConfig.value;
          break;
        }
      }
    }
  };

  const renderDataTag = () => {
    const {
      env: dinkyEnv,
      flink: flinkConfig,
      maven: mavenConfig,
      dolphinscheduler: dsConfig,
      ldap: ldapConfig,
      metrics: metricsConfig,
      resource: resourceConfig
    } = data;

    return [
      {
        key: SettingConfigKeyEnum.DINKY,
        label: (
          <TagAlignCenter>
            <DinkyIcon size={imgSize - 5} />
            {l('sys.setting.dinky')}
          </TagAlignCenter>
        ),
        children: <EnvConfig onSave={handleSaveSubmit} data={dinkyEnv} />,
        path: '/settings/globalsetting/dinky'
      },
      {
        key: SettingConfigKeyEnum.FLINK,
        label: (
          <TagAlignCenter>
            <FlinkIcon size={imgSize} />
            {l('sys.setting.flink')}
          </TagAlignCenter>
        ),
        children: <FlinkConfig onSave={handleSaveSubmit} data={flinkConfig} />,
        path: '/settings/globalsetting/flink'
      },
      {
        key: SettingConfigKeyEnum.MAVEN,
        label: (
          <TagAlignCenter>
            <MavenIcon size={imgSize} />
            {l('sys.setting.maven')}
          </TagAlignCenter>
        ),
        children: <MavenConfig onSave={handleSaveSubmit} data={mavenConfig} />,
        path: '/settings/globalsetting/maven'
      },
      {
        key: SettingConfigKeyEnum.DOLPHIN_SCHEDULER,
        label: (
          <TagAlignCenter>
            <DSIcon size={imgSize} />
            {l('sys.setting.ds')}
          </TagAlignCenter>
        ),
        children: <DSConfig onSave={handleSaveSubmit} data={dsConfig} />,
        path: '/settings/globalsetting/ds'
      },
      {
        key: SettingConfigKeyEnum.LDAP,
        label: (
          <TagAlignCenter>
            <LDAPIcon size={imgSize} />
            {l('sys.setting.ldap')}
          </TagAlignCenter>
        ),
        children: <LdapConfig onSave={handleSaveSubmit} data={ldapConfig} />,
        path: '/settings/globalsetting/ldap'
      },
      {
        key: SettingConfigKeyEnum.METRIC,
        label: (
          <TagAlignCenter>
            <MetricsIcon size={imgSize} />
            {l('sys.setting.metrics')}
          </TagAlignCenter>
        ),
        children: <MetricsConfig onSave={handleSaveSubmit} data={metricsConfig} />,
        path: '/settings/globalsetting/metrics'
      },
      {
        key: SettingConfigKeyEnum.RESOURCE,
        label: (
          <TagAlignCenter>
            <ResourceIcon size={imgSize} />
            {l('sys.setting.resource')}
          </TagAlignCenter>
        ),
        children: <ResourcesConfig onSave={handleSaveSubmit} data={resourceConfig} />,
        path: '/settings/globalsetting/resource'
      }
    ];
  };

  //
  // useEffect(() => {
  //
  //   const filter = renderDataTag().filter(
  //     (menu) => !!!menu.path || !!AuthorizedObject({path: menu.path, children: menu, access: {tags}}));
  //   setTags(filter as []);
  //   setActiveKey(filter[0]?.key ?? SettingConfigKeyEnum.DINKY);
  // }, [activeKey])

  return (
    <FadeIn>
      <div style={{ paddingBottom: '20px' }}>
        <ProCard
          ghost
          bodyStyle={{ height: '80vh' }}
          className={'schemaTree'}
          size='small'
          bordered
          tabs={{
            activeKey: activeKey,
            type: 'card',
            cardProps: {
              hoverable: true,
              bodyStyle: {
                height: 1000 - 155
              },
              boxShadow: true
            },
            animated: true,
            onChange: (key: any) => setActiveKey(key),
            // todo: 目前无法通过这种方式进行权限显示 多 Tag 的方式,待实现
            items: renderDataTag()
          }}
        />
      </div>
    </FadeIn>
  );
};

export default memo(SettingOverView);
