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
import {ProCard} from "@ant-design/pro-components";
import DSConfigList from "@/pages/SettingCenter/GlobalSetting/SettingOverView/DSConfig/DSConfigList";

interface DSConfigProps {
  data: BaseConfigProperties[]
}

export const DSConfig = ({data}: DSConfigProps) => {

  return <>
    <ProCard
        title="DolphinSchuder 配置"
        tooltip="海豚调度插件设置，可以让您把dinky任务丝滑推到海豚调度器工作流上，提高您的工作效率 "
        size="small"
        headerBordered
        collapsible
        defaultCollapsed={false}
    >
      <DSConfigList data={data}/>
    </ProCard>
  </>
}
