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

import CodeEdit from '@/components/CustomEditor/CodeEdit';
import React, {useState} from 'react';

const CodeEditProps = {
  height: '35vh',
  width: '100%',
  lineNumbers: 'on',
  language: 'sql',
  // autoRange: true, // todo: 使用此配置项,后续可以自动识别光标位置,根据;分割,得到 sql 片段区间, 在左侧自动添加 执行按钮 和 区间选中效果, 规划内,暂未实现
};

type SQLConsoleProps = {
  querySQL: string,
}

const SQLConsole:React.FC<SQLConsoleProps> = (props) => {
  const {querySQL} = props;

  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = useState<boolean>(false);

  const handleInputChange = (value: string) => {
    setInputValue(value);
  }

  const execSql = async () => {
    setLoading(true);
    // todo: exec sql callback
    setLoading(false);
  }

  return <>
    <CodeEdit {...CodeEditProps} onChange={handleInputChange} code={inputValue}/>
  </>
}

export default SQLConsole;
