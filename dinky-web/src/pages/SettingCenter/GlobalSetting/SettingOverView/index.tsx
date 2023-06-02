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

import React, {memo, useEffect} from 'react';
import {API_CONSTANTS, RESPONSE_CODE} from '@/services/constants';
import {handleOption, queryDataByParams} from '@/services/BusinessCrud';
import {BaseConfigProperties, Settings} from '@/types/SettingCenter/data';
import {EnvConfig} from '@/pages/SettingCenter/GlobalSetting/SettingOverView/EnvConfig';
import {FlinkConfig} from '@/pages/SettingCenter/GlobalSetting/SettingOverView/FlinkConfig';
import {MavenConfig} from '@/pages/SettingCenter/GlobalSetting/SettingOverView/MavenConfig';
import {DSConfig} from '@/pages/SettingCenter/GlobalSetting/SettingOverView/DSConfig';
import {l} from '@/utils/intl';

const SettingOverView = () => {

  const [data, setData] = React.useState<Settings>({dolphinscheduler: [], env: [], flink: [], maven: []});


  const fetchData = async () => {
    await queryDataByParams(API_CONSTANTS.SYSTEM_GET_ALL_CONFIG).then((res) => {
      setData(res);
    });
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSaveSubmit = async (data: BaseConfigProperties) => {
    // todo: save data of update data
    const {code} = await handleOption(API_CONSTANTS.SYSTEM_MODIFY_CONFIG, l('sys.setting.modify', '', {key: data.key}), data);
    if (code === RESPONSE_CODE.SUCCESS) await fetchData();
  };

  const renderData = () => {
    if (data) {
      const {env: dinkyEnv, flink: flinkConfig, maven: mavenConfig, dolphinscheduler: dsConfig} = data;
      return <>
        <EnvConfig onSave={handleSaveSubmit} data={dinkyEnv}/>
        <FlinkConfig onSave={handleSaveSubmit} data={flinkConfig}/>
        <MavenConfig onSave={handleSaveSubmit} data={mavenConfig}/>
        <DSConfig onSave={handleSaveSubmit} data={dsConfig}/>
      </>;
    }
  };


  return <>
    {renderData()}
  </>;
};

export default memo(SettingOverView);
