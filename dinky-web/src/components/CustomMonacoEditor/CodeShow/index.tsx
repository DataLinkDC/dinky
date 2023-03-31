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
import {ConvertCodeEditTheme} from "@/utils/function";
import {MonacoEditorOptions} from "@/types/Public/data";

export type CodeShowFormProps = {
  height?: string;
  width?: string;
  language?: string;
  options?: any;
  code: string;
  lineNumbers?: string;
  theme?: string;
  autoWrap?: string;
};

const CodeShow = (props: CodeShowFormProps) => {

  /**
   * 1. height: edit height
   * 2. width: edit width
   * 3. language: edit language
   * 4. options: edit options
   * 5. code: content
   * 6. readOnly: is readOnly, value: true | false
   * 7. lineNumbers: is show lineNumbers, value: on | off | relative | interval
   * 8. theme: edit theme , value: vs-dark | vs | hc-black
   * 9. autoWrap: is auto wrap, value: on | off | wordWrapColumn | bounded
   */
  const {
    height = '30vh', // if null or undefined, set default value
    width = '100%', // if null or undefined, set default value
    language,
    options = {
      ...MonacoEditorOptions, // set default options
    },
    code, // content
    lineNumbers, // show lineNumbers
    theme, // edit theme
    autoWrap = 'on', //  auto wrap
  } = props;


  return (<>
    <MonacoEditor
      width={width}
      height={height}
      value={code}
      options={{
        ...options,
        readOnly: true,
        wordWrap: autoWrap,
        lineNumbers,
        language
      }}
      theme={theme ? theme : ConvertCodeEditTheme()}
    />
  </>)
};

export default CodeShow;
