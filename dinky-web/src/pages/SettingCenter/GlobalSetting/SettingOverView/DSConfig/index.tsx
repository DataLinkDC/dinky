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

import GeneralConfig from '@/pages/SettingCenter/GlobalSetting/SettingOverView/GeneralConfig';
import { BaseConfigProperties } from '@/types/SettingCenter/data';
import { l } from '@/utils/intl';
import { Tag } from 'antd';
import React from 'react';

interface DSConfigProps {
  data: BaseConfigProperties[];
  onSave: (data: BaseConfigProperties) => void;
  auth: string;
}

export const DSConfig = ({ data, onSave, auth }: DSConfigProps) => {
  const [loading, setLoading] = React.useState(false);

  const onSaveHandler = async (data: BaseConfigProperties) => {
    setLoading(true);
    await onSave(data);
    setLoading(false);
  };
  return (
    <>
      <GeneralConfig
        loading={loading}
        onSave={onSaveHandler}
        auth={auth}
        tag={
          <>
            <Tag color={'default'}>{l('sys.setting.tag.integration')}</Tag>
          </>
        }
        data={data}
      />
    </>
  );
};
