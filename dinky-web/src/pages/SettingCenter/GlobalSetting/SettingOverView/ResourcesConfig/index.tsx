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
import { RadioChangeEvent, Tag } from 'antd';
import React, { useEffect, useState } from 'react';

interface ResourcesConfigProps {
  data: BaseConfigProperties[];
  onSave: (data: BaseConfigProperties) => void;
  auth: string;
}

const ModelType = {
  HDFS: 'HDFS',
  OSS: 'OSS'
};

type ResourceConfig = {
  base: BaseConfigProperties[];
  hdfs: BaseConfigProperties[];
  oss: BaseConfigProperties[];
};

export const ResourcesConfig = ({ data, onSave, auth }: ResourcesConfigProps) => {
  const [loading, setLoading] = React.useState(false);
  const [model, setModel] = React.useState('hdfs');
  const [filterData, setFilterData] = useState<ResourceConfig>({
    base: [],
    hdfs: [],
    oss: []
  });

  useEffect(() => {
    // 处理 data / 规则: 前缀为 sys.resource.settings.base 的为基础配置，其他的为 hdfs/oss 配置
    const base: BaseConfigProperties[] = data.filter((d) =>
      d.key.startsWith('sys.resource.settings.base')
    );
    const hdfs: BaseConfigProperties[] = data.filter((d) =>
      d.key.startsWith('sys.resource.settings.hdfs')
    );
    const oss: BaseConfigProperties[] = data.filter((d) =>
      d.key.startsWith('sys.resource.settings.oss')
    );
    setFilterData({ base, hdfs, oss });
    // 获取当前的 model
    const currentModel = base.find((d) => d.key === 'sys.resource.settings.base.model')?.value;
    if (currentModel) {
      setModel(currentModel);
    }
  }, [data]);

  const modelKey: string = 'sys.resource.settings.base.model';

  const onSaveHandler = async (data: BaseConfigProperties) => {
    setLoading(true);
    await onSave(data);
    setLoading(false);
  };
  const selectChange = async (e: RadioChangeEvent) => {
    const { value } = e.target;
    setModel(value);
    await onSaveHandler({
      name: '',
      example: [],
      frontType: '',
      key: modelKey,
      note: '',
      value: value.toString().toLocaleUpperCase()
    });
  };
  return (
    <>
      <GeneralConfig
        loading={loading}
        auth={auth}
        onSave={onSaveHandler}
        tag={<Tag color={'default'}>{l('sys.setting.tag.integration')}</Tag>}
        data={filterData.base}
        selectChanges={selectChange}
      />
      {model.toLocaleUpperCase() === ModelType.HDFS && (
        <GeneralConfig
          loading={loading}
          auth={auth}
          onSave={onSaveHandler}
          tag={<Tag color={'default'}>{l('sys.setting.tag.integration')}</Tag>}
          data={filterData.hdfs}
        />
      )}
      {model.toLocaleUpperCase() === ModelType.OSS && (
        <GeneralConfig
          loading={loading}
          auth={auth}
          onSave={onSaveHandler}
          tag={<Tag color={'default'}>{l('sys.setting.tag.integration')}</Tag>}
          data={filterData.oss}
        />
      )}
    </>
  );
};
