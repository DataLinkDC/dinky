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


import MonacoEditor from "react-monaco-editor";
import {useState} from "react";

export type CodeEditFormProps = {
  height?: string;
  width?: string;
  language?: string;
  options?: any;
  onChange: (val: string) => void;
  code: string;
};

const CodeEdit = (props: CodeEditFormProps) => {

  const {
    height = '100%',
    width = '100%',
    language = 'sql',
    options = {
      selectOnLineNumbers: true,
      renderSideBySide: false,
      autoIndent: 'None',
      automaticLayout: true,
    },
    onChange,
    code,
  } = props;

  return (<>
    <MonacoEditor
      width={width}
      height={height}
      language={language}
      value={code}
      options={options}
      onChange={onChange}
      theme='vs-dark'
    />
  </>)
};

export default CodeEdit;
