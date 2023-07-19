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

import {Editor }  from "@monaco-editor/react";
import {convertCodeEditTheme} from "@/utils/function";
import {MonacoEditorOptions} from "@/types/Public/data";
import React, {useState} from "react";
import {editor} from "monaco-editor";
import EditorFloatBtn from "@/components/CustomEditor/EditorFloatBtn";
import FullscreenBtn from "../FullscreenBtn";
import * as monaco from 'monaco-editor';
import { loader } from '@monaco-editor/react';
import {EditorLanguage} from "monaco-editor/esm/metadata";

loader.config({ monaco });
/**
 * props
 * todo:
 *  1. Realize full screen/exit full screen in the upper right corner of the editor (Visible after opening)
 *    - The full screen button is done, but the full screen is not implemented
 *  2. Callback for right-clicking to clear logs (optional, not required)
 */
export type CodeShowFormProps = {
  height?: string|number;
  width?: string;
  language?: EditorLanguage;
  options?: any;
  code: string;
  lineNumbers?: string;
  theme?: string;
  autoWrap?: string;
  showFloatButton?: boolean;
  refreshLogCallback?: () => void;
  fullScreenBtn?: boolean;
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
    fullScreenBtn = false,
  } = props;

  const {ScrollType} = editor;

  const [scrollBeyondLastLine] = useState<boolean>(options.scrollBeyondLastLine);

  const [loading, setLoading] = useState<boolean>(false);
  const [stopping, setStopping] = useState<boolean>(false);
  const [autoRefresh, setAutoRefresh] = useState<boolean>(false);
  const [fullScreen, setFullScreen] = useState<boolean>(false);
  const [editorRef, setEditorRef] = useState<any>();
  const [timer, setTimer] = useState<NodeJS.Timer>();

  // // register TypeScript language service, if language is not set default value is typescript!
  // monaco.languages.register({
  //   id: language || "typescript",
  // });

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
  const handleStartAutoRefresh = async () => {
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
    editorRef?.setScrollPosition({scrollTop: editorRef.getScrollTop() - 500}, ScrollType.Smooth);
  };


  /**
   *  editorDidMount
   * @param {editor.IStandaloneCodeEditor} editor
   */
  const editorDidMount = (editor: editor.IStandaloneCodeEditor) => {
    setEditorRef(editor);
    editor.layout();
    editor.focus();
    if (scrollBeyondLastLine){
      editor.onDidChangeModelContent(()=>{
        const lineCount = editor.getModel()?.getLineCount() as number;
        if (lineCount>20){
          editor.revealLine(lineCount);
        }else {
          editor.revealLine(1);
        }
      });
    }
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
  };


  /**
   *  render
   */
  return (<>
    <div className={"monaco-float"}>
      {/* fullScreen button */}
      {fullScreenBtn && <FullscreenBtn isFullscreen={fullScreen} fullScreenCallBack={() => setFullScreen(!fullScreen)}/>}

      {/* editor */}
      <Editor
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
        onMount={editorDidMount}
        theme={theme ? theme : convertCodeEditTheme()}
      />

      {/* float button */}
      {showFloatButton && <EditorFloatBtn {...restEditBtnProps}/>}
    </div>
  </>);
};

export default CodeShow;
