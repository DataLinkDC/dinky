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
import CodeShow from '@/components/CustomEditor/CodeShow';
import useThemeValue from '@/hooks/useThemeValue';
import { jsonToSql } from '@/pages/DataStudio/BottomContainer/Tools/JsonToSql/service';
import { StateType } from '@/pages/DataStudio/model';
import { convertCodeEditTheme } from '@/utils/function';
import { connect } from '@@/exports';
import { Button, Space } from 'antd';
import React, { useState } from 'react';

const padding = 10;

const JsonToSql: React.FC = (props: connect) => {
  const { height } = props;
  const maxHeight = height - 36 - padding * 2 - 32 - 12;
  const themeValue = useThemeValue();
  const border = `1px solid ${themeValue.borderColor}`;
  const [jsonData, setJsonData] = useState('');
  const [sqlData, setSqlData] = useState('');
  return (
    <div style={{ padding: padding }}>
      <Space>
        <Button
          children={'Convert'}
          onClick={() => {
            jsonToSql({ data: jsonData }).then(setSqlData);
          }}
        />
      </Space>

      <div style={{ display: 'flex', paddingBlockStart: padding }}>
        <div style={{ width: '50%', border }}>
          <CodeEdit
            height={maxHeight + 'px'}
            code={jsonData}
            language={'json'}
            theme={convertCodeEditTheme()}
            onChange={(x) => {
              setJsonData(x ?? '');
            }}
          />
        </div>
        <div style={{ width: '50%' }}>
          <CodeShow
            height={maxHeight}
            code={sqlData}
            language={'json'}
            style={{ border }}
            options={{ minimap: true }}
          />
        </div>
      </div>
    </div>
  );
};
export default connect(({ Studio }: { Studio: StateType }) => ({
  height: Studio.bottomContainer.height
}))(JsonToSql);
