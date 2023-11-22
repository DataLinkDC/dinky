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

import CodeShow, { CodeShowFormProps } from '@/components/CustomEditor/CodeShow';
import { l } from '@/utils/intl';
import { Empty } from 'antd';
import React from 'react';

const CodeEditProps: any = {
  width: '100%',
  lineNumbers: 'on',
  language: 'javalog'
};

type LogsShowProps = {
  code: string;
  refreshLogCallback: () => void;
};

const LogsShow: React.FC<LogsShowProps> = (props) => {
  const { code, refreshLogCallback } = props;

  const restLogsShowProps: CodeShowFormProps = {
    showFloatButton: true,
    code,
    refreshLogCallback
  };

  return (
    <>
      {code ? (
        <CodeShow
          {...{ ...restLogsShowProps, height: parent.innerHeight - 210 }}
          {...CodeEditProps}
        />
      ) : (
        <Empty className={'code-content-empty'} description={l('sys.info.logList.tips')} />
      )}
    </>
  );
};

export default LogsShow;
