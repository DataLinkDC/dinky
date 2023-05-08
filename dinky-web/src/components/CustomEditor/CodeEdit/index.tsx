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

import MonacoEditor, {monaco} from "react-monaco-editor";
import {MonacoEditorOptions} from "@/types/Public/data";
import {convertCodeEditTheme} from "@/utils/function";
import React from "react";

 type CodeEditFormProps = {
  height?: string;
  width?: string;
  language?: string;
  options?: any;
  onChange: (val: string) => void;
  code: string;
  readOnly?: boolean;
  lineNumbers?: string;
  theme?: string;
  autoWrap?: string;
};

const CodeEdit = (props: CodeEditFormProps) => {


  /**
   * 1. height: edit height
   * 2. width: edit width
   * 3. language: edit language
   * 4. options: edit options
   * 5. onChange: edit change callback
   * 6. code: content
   * 7. readOnly: is readOnly, value: true | false
   * 8. lineNumbers: is show lineNumbers, value: on | off | relative | interval
   * 9. theme: edit theme , value: vs-dark | vs | hc-black
   * 10. autoWrap: is auto wrap, value: on | off | wordWrapColumn | bounded
   */
  const {
    height = "100%", // if null or undefined, set default value
    width = "100%", // if null or undefined, set default value
    language, // edit language
    options = {
      ...MonacoEditorOptions, // set default options
    },
    onChange, // edit change callback
    code, // content
    readOnly = false, // is readOnly
    lineNumbers, // show lineNumbers
    theme, // edit theme
    autoWrap = "on", // auto wrap
  } = props;


  // register TypeScript language service
  monaco.languages.register({
    id: language || "typescript",
  });

  // get monaco editor model
  const getModel = () => monaco.editor.getModels()[0];


  return (<>
    <MonacoEditor
      width={width}
      height={height}
      value={code}
      language={language}
      options={{
        ...options,
        readOnly,
        wordWrap: autoWrap,
        autoDetectHighContrast: true,
        lineNumbers,
      }}
      editorDidMount={(editor) => {
        // 在编辑器加载完成后，设置自动布局和自动高亮显示
        editor.layout();
        editor.focus();
        getModel().updateOptions({
          tabSize: 2,
        });
      }}
      onChange={onChange}
      theme={theme ? theme : convertCodeEditTheme()}
    />
  </>);
};

export default CodeEdit;
