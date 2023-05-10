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
import {convertCodeEditTheme} from "@/utils/function";
import {MonacoEditorOptions} from "@/types/Public/data";
import React, {useState} from "react";
import {editor} from "monaco-editor";
import EditorFloatBtn from "@/components/CustomEditor/EditorFloatBtn";

/**
 * props
 * todo:
 *  1. Realize full screen/exit full screen in the upper right corner of the editor (Visible after opening)
 *  2. Callback for right-clicking to clear logs (optional, not required)
 */
export type CodeShowFormProps = {
  height?: string;
  width?: string;
  language?: string;
  options?: any;
  code: string;
  lineNumbers?: string;
  theme?: string;
  autoWrap?: string;
  showFloatButton?: boolean;
  refreshLogCallback?: () => void;
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
    height = "30vh", // if null or undefined, set default value
    width = "100%", // if null or undefined, set default value
    language,
    options = {
      ...MonacoEditorOptions, // set default options
    },
    code, // content
    lineNumbers, // show lineNumbers
    theme, // edit theme
    autoWrap = "on", //  auto wrap
    showFloatButton = false,
    refreshLogCallback,
  } = props;

  const {ScrollType} = editor;


  const [loading, setLoading] = useState<boolean>(false);
  const [stopping, setStopping] = useState<boolean>(false);
  const [autoRefresh, setAutoRefresh] = useState<boolean>(false);
  const [editorRef, setEditorRef] = useState<any>();
  const [timer, setTimer] = useState<NodeJS.Timer>();

  // register TypeScript language service
  monaco.languages.register({
    id: language || "typescript",
  });

  /**
   *  handle sync log
   * @returns {Promise<void>}
   */
  const handleSyncLog = async () => {
    setLoading(true);
    setTimeout(() => {
      refreshLogCallback?.();
      setLoading(false);
    }, 1000);
  };


  /**
   *  handle stop auto refresh log
   */
  const handleStopAutoRefresh = () => {
    setStopping(true);
    setTimeout(() => {
      clearInterval(timer);
      setStopping(false);
      setAutoRefresh(false);
    }, 1000);
  };

  /**
   *  handle stop auto refresh log
   */
  const handleStartAutoRefresh = () => {
    setAutoRefresh(true);
    const timerSync = setInterval(() => {
      handleSyncLog();
    }, 5000);
    setTimer(timerSync);
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
    // @ts-ignore
    editorRef?.setScrollPosition({scrollTop: editorRef.getScrollTop() - 500}, ScrollType.Smooth);
  };


  /**
   *  editorDidMount
   * @param {editor.IStandaloneCodeEditor} editor
   */
  const editorDidMount = (editor: editor.IStandaloneCodeEditor) => {
    setEditorRef(editor);
    // 在编辑器加载完成后，设置自动布局和自动高亮显示
    editor.layout();
    editor.focus();
  };


  const restEditBtnProps = {
    refreshLogCallback,
    autoRefresh,
    stopping,
    loading,
    handleSyncLog,
    handleStopAutoRefresh,
    handleStartAutoRefresh,
    handleBackTop,
    handleBackBottom,
    handleUpScroll,
    handleDownScroll,
  }



  /**
   *  render
   */
  return (<>
    <div className={"monaco-float"}>
      <MonacoEditor
        width={width}
        height={height}
        value={loading ? "loading..." : code}
        language={language}
        options={{
          ...options,
          readOnly: true,
          wordWrap: autoWrap,
          autoDetectHighContrast: true,
          selectOnLineNumbers: true,
          fixedOverflowWidgets: true,
          autoClosingDelete: "always",
          lineNumbers,
        }}
        editorDidMount={editorDidMount}
        theme={theme ? theme : convertCodeEditTheme()}
      />
      {showFloatButton && <EditorFloatBtn {...restEditBtnProps}/>}
    </div>
  </>);
};

export default CodeShow;
