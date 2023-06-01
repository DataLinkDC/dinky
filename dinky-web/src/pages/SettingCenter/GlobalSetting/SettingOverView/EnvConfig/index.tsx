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


import {BaseConfigProperties} from '@/types/SettingCenter/data';
import { ProCard } from '@ant-design/pro-components';
import EnvList from "@/pages/SettingCenter/GlobalSetting/SettingOverView/EnvConfig/EnvList";

interface EnvConfigProps {
  data: BaseConfigProperties[]
}

export const EnvConfig = ({data}: EnvConfigProps) => {
  return <>
    <ProCard
      title="Dinky 环境配置"
      tooltip="主要以修改系统变量为主，保证基础功能稳定运行"
      size="small"
      headerBordered
      collapsible
      defaultCollapsed={false}
    >
      <EnvList data={data}/>
    </ProCard>
  </>
}
