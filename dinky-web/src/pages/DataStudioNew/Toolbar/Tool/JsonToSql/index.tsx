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

import CodeEdit from '@/components/CustomEditor/CodeEdit';
import useThemeValue from '@/hooks/useThemeValue';
import { Button, Flex, Space } from 'antd';
import React, { useState } from 'react';
import CodeShow from '@/components/CustomEditor/CodeShow';
import { jsonToSql } from '@/pages/DataStudioNew/Toolbar/Tool/JsonToSql/service';

const padding = 10;

export const JsonToSql: React.FC = () => {
  const themeValue = useThemeValue();
  const border = `1px solid ${themeValue.borderColor}`;
  const [jsonData, setJsonData] = useState<string>();
  const [sqlData, setSqlData] = useState<string>();
  return (
    <Flex vertical style={{ height: '100%' }}>
      <Space>
        <Button
          children={'Convert'}
          onClick={async () => {
            const schema = (await jsonToSql({ data: jsonData })) as string;
            setSqlData(`create table xxx(\n${schema})`);
          }}
        />
      </Space>

      <Flex style={{ paddingBlockStart: padding, height: '100%' }}>
        <div style={{ width: '50%', border }}>
          <CodeEdit
            height={'100%'}
            code={jsonData ?? ''}
            language={'json'}
            onChange={setJsonData}
          />
        </div>
        <div style={{ width: '50%' }}>
          <CodeShow
            height={'100%'}
            code={sqlData ?? ''}
            language={'sql'}
            style={{ border }}
            options={{ minimap: { enabled: true } }}
          />
        </div>
      </Flex>
    </Flex>
  );
};
