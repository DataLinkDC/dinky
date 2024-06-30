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

import CodeShow from '@/components/CustomEditor/CodeShow';
import { queryClassLoaderJars } from '@/pages/SettingCenter/ClassLoaderJars/service';
import { l } from '@/utils/intl';
import { Alert, Space, Tabs } from 'antd';
import { useEffect, useState } from 'react';

export default () => {
  const [data, setData] = useState<Record<string, string[]>>();

  useEffect(() => {
    queryClassLoaderJars().then((res) => {
      if (res) setData(res);
    });
  }, []);

  return (
    <Space size={'large'} direction={'vertical'}>
      <Alert message={l('sys.classLoaderJars.tips')} type='info' showIcon />
      {data && (
        <Tabs
          defaultActiveKey='1'
          items={Object.keys(data).map((key) => {
            return {
              key: key,
              label: key,
              children: (
                <CodeShow
                  showFloatButton
                  enableMiniMap
                  height={'75vh'}
                  code={data[key].join('\n')}
                  language={'java'}
                />
              )
            };
          })}
        />
      )}
    </Space>
  );
};
