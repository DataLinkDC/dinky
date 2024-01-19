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

import { Height80VHDiv } from '@/components/StyledComponents';
import {
  tempColumns,
  tempData
} from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SQLConsole/data';
import DataList from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SQLConsole/DataList';
import Editor from '@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter/SQLConsole/Editor';
import { l } from '@/utils/intl';
import { PageLoading } from '@ant-design/pro-components';
import { Alert, Result } from 'antd';
import React, { useState } from 'react';

const SQLConsole: React.FC = () => {
  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = useState<boolean>(false);

  const handleInputChange = (value: string) => {
    setInputValue(value);
  };

  const execSql = async () => {
    setLoading(true);
    // todo: exec sql callback
    setTimeout(() => {
      setLoading(false);
    }, 3000);
  };

  const renderAlertMsg = (flag: boolean, msg: string) => {
    if (!flag) {
      return (
        <Alert
          style={{ margin: 0, height: '2vw', alignItems: 'center' }}
          message={msg}
          type='error'
          showIcon
        />
      );
    } else {
      return (
        <Alert
          style={{ margin: 0, height: '2vw', alignItems: 'center' }}
          message={msg}
          type='success'
          showIcon
        />
      );
    }
  };

  return (
    <Height80VHDiv>
      <Editor
        inputValue={inputValue}
        loading={loading}
        execCallback={execSql}
        handleInputChange={handleInputChange}
      />
      {/*{renderAlertMsg(false, '执行成功')}*/}
      {loading ? (
        <Result icon={<PageLoading spin={loading} />} title={l('rc.ds.console.running')} />
      ) : (
        <DataList columns={tempColumns} data={tempData} />
      )}
    </Height80VHDiv>
  );
};

export default SQLConsole;
