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


import React from 'react';
import FlinkConfigView from './components/flinkConfig';
import {connect} from "umi";
import {PageContainer} from "@ant-design/pro-layout";
import {SettingsStateType} from "@/pages/SettingCenter/FlinkSettings/model";
import {loadSettings} from "@/pages/SettingCenter/FlinkSettings/function";


type SettingsProps = {
  dispatch: any;
};

const Settings: React.FC<SettingsProps> = (props) => {

  const {dispatch} = props;
  loadSettings(dispatch);


  return (
    <PageContainer title={false}>
      <FlinkConfigView/>
    </PageContainer>
  );
};
export default connect(({Settings}: { Settings: SettingsStateType }) => ({
  sqlSubmitJarPath: Settings.sqlSubmitJarPath,
  sqlSubmitJarParas: Settings.sqlSubmitJarParas,
  sqlSubmitJarMainAppClass: Settings.sqlSubmitJarMainAppClass,
}))(Settings);
