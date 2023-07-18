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

import * as monaco from 'monaco-editor';

import {MonacoEditorOptions} from "@/types/Public/data";
import {convertCodeEditTheme} from "@/utils/function";
import React, {useState} from "react";
import EditorFloatBtn from "@/components/CustomEditor/EditorFloatBtn";
import {editor} from "monaco-editor";
import {Editor, OnChange} from "@monaco-editor/react";

type CodeEditFormProps = {
  height?: string;
  width?: string;
  language?: string;
  options?: any;
  onChange?: OnChange;
  code: string;
  readOnly?: boolean;
  lineNumbers?: string;
  theme?: string;
  autoWrap?: string;
  showFloatButton?: boolean;
};

const CodeEdit = (props: CodeEditFormProps) => {

  const [editorRef, setEditorRef] = useState<any>();

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
   * 11. showFloatButton: is show float button, value: true | false (default: false)
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
    showFloatButton = false,
  } = props;

  const {ScrollType} = editor;

  /**
   *  editorDidMount
   * @param {editor.IStandaloneCodeEditor} editor
   */
  const editorDidMount = (editor: editor.IStandaloneCodeEditor) => {
    setEditorRef(editor);
    editor.layout();
    editor.focus();
  };
  /**
   *  handle scroll to top
   */
  const handleBackTop = () => {
    editorRef.revealLine(1);
  };

  /**
   *  handle scroll to bottom
   */
  const handleBackBottom = () => {
    editorRef.revealLine(editorRef.getModel().getLineCount());
  };

  /**
   *  handle scroll to down
   */
  const handleDownScroll = () => {
    editorRef.setScrollPosition({scrollTop: editorRef.getScrollTop() + 500}, ScrollType.Smooth);
  };

  /**
   *  handle scroll to up
   */
  const handleUpScroll = () => {
    editorRef?.setScrollPosition({scrollTop: editorRef.getScrollTop() - 500}, ScrollType.Smooth);
  };


  // register TypeScript language service
  monaco.languages.register({
    id: language || "typescript",
  });

  const restEditBtnProps = {
    handleBackTop,
    handleBackBottom,
    handleUpScroll,
    handleDownScroll,
  };


  return (<>
    <div className={"monaco-float"}>
      <Editor
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
        onMount={editorDidMount}
        onChange={onChange}
        theme={theme ? theme : convertCodeEditTheme()}
      />
      {showFloatButton && <EditorFloatBtn {...restEditBtnProps}/>}
    </div>
  </>);
};

export default CodeEdit;
