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
import { queryDataByParams } from '@/services/BusinessCrud';
import { API_CONSTANTS } from '@/services/endpoints';
import { useEffect, useState } from 'react';

/**
 * code edit props
 */
const CodeEditProps = {
  height: '82vh',
  width: '100%',
  lineNumbers: 'on',
  language: 'javalog',
  autoWrap: 'off'
};
const RootLogs = () => {
  const [code, setCode] = useState<string>('');

  const queryLogs = async () => {
    const result = await queryDataByParams(API_CONSTANTS.SYSTEM_ROOT_LOG);
    setCode(result as string);
  };

  useEffect(() => {
    queryLogs();
  }, []);

  const restRootLogProps = {
    code: code,
    showFloatButton: true,
    refreshLogCallback: queryLogs
  };

  return (
    <>
      <CodeShow {...restRootLogProps} {...CodeEditProps} />
    </>
  );
};

export default RootLogs;
