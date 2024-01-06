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

import EditorFloatBtn from '@/components/CustomEditor/EditorFloatBtn';
import { Loading } from '@/pages/Other/Loading';
import { MonacoEditorOptions } from '@/types/Public/data';
import { convertCodeEditTheme } from '@/utils/function';

import { Col, Row } from 'antd';
import { editor } from 'monaco-editor';
import { EditorLanguage } from 'monaco-editor/esm/metadata';

import FullscreenBtn from '@/components/CustomEditor/FullscreenBtn';
import { handleInitEditorAndLanguageOnBeforeMount } from '@/components/CustomEditor/function';
import { Editor, loader, Monaco } from '@monaco-editor/react';
import * as monaco from 'monaco-editor';
import { CSSProperties, useRef, useState } from 'react';

loader.config({ monaco });

export type CodeShowFormProps = {
  height?: string | number;
  width?: string;
  language?: EditorLanguage | string;
  options?: any;
  code: string;
  lineNumbers?: string;
  enableMiniMap?: boolean;
  autoWrap?: string;
  showFloatButton?: boolean;
  refreshLogCallback?: () => void;
  fullScreenBtn?: boolean;
  style?: CSSProperties;
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
      ...MonacoEditorOptions // set default options
    },
    code, // content
    lineNumbers, // show lineNumbers
    autoWrap = 'on', //  auto wrap
    showFloatButton = false,
    refreshLogCallback,
    fullScreenBtn = false,
    enableMiniMap = false
  } = props;

  const { ScrollType } = editor;

  const [scrollBeyondLastLine] = useState<boolean>(options.scrollBeyondLastLine);

  const [loading, setLoading] = useState<boolean>(false);
  const [stopping, setStopping] = useState<boolean>(false);
  const [autoRefresh, setAutoRefresh] = useState<boolean>(false);
  const [fullScreen, setFullScreen] = useState<boolean>(false);
  const editorInstance = useRef<editor.IStandaloneCodeEditor | undefined>();
  const monacoInstance = useRef<Monaco | undefined>();
  const [timer, setTimer] = useState<NodeJS.Timer>();

  /**
   *  handle sync log
   */
  const handleSyncLog = async () => {
    setLoading(true);
    setInterval(() => {
      refreshLogCallback?.();
    }, 1000);
    setLoading(false);
  };

  /**
   *  handle stop auto refresh log
   */
  const handleStopAutoRefresh = () => {
    setStopping(true);
    setInterval(() => {
      clearInterval(timer as any);
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
    editorInstance?.current?.revealLine(1);
  };

  /**
   *  handle scroll to bottom
   */
  const handleBackBottom = () => {
    // @ts-ignore
    editorInstance?.current?.revealLine(editorInstance?.current?.getModel().getLineCount());
  };

  /**
   *  handle scroll to down
   */
  const handleDownScroll = () => {
    editorInstance?.current?.setScrollPosition(
      { scrollTop: editorInstance?.current?.getScrollTop() + 500 },
      ScrollType.Smooth
    );
  };

  /**
   *  handle scroll to up
   */
  const handleUpScroll = () => {
    editorInstance?.current?.setScrollPosition(
      { scrollTop: editorInstance?.current?.getScrollTop() - 500 },
      ScrollType.Smooth
    );
  };

  /**
   *  handle get downoad url
   */
  const handleDownloadLog = () => {
    const blob = new Blob([code ?? 'not get content'], { type: 'text/plain' });
    return URL.createObjectURL(blob);
  };

  const handleWrap = () => {
    editorInstance?.current?.updateOptions({ wordWrap: 'on' });
  };

  /**
   *  editorDidMount
   * @param {editor.IStandaloneCodeEditor} editor
   * @param monaco {Monaco}
   */
  const editorDidMount = (editor: editor.IStandaloneCodeEditor, monaco: Monaco) => {
    editorInstance.current = editor;
    monacoInstance.current = monaco;
    editor.layout();
    editor.focus();
    if (scrollBeyondLastLine) {
      editor.onDidChangeModelContent(() => {
        const lineCount = editor.getModel()?.getLineCount() as number;
        if (lineCount > 20) {
          editor.revealLine(lineCount);
        } else {
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
    handleDownloadLog,
    handleWrap
  };

  /**
   *  render
   */
  return (
    <>
      <Row wrap={false}>
        <Col flex='auto'>
          {/* fullScreen button */}
          {fullScreenBtn && (
            <FullscreenBtn
              isFullscreen={fullScreen}
              fullScreenCallBack={() => setFullScreen(!fullScreen)}
            />
          )}

          {/* editor */}
          <Editor
            beforeMount={(monaco) => handleInitEditorAndLanguageOnBeforeMount(monaco)}
            width={width}
            height={height}
            loading={<Loading loading={loading} />}
            value={code ?? ''}
            language={language}
            options={{
              scrollBeyondLastLine: false,
              readOnly: true,
              glyphMargin: false,
              wordWrap: autoWrap,
              autoDetectHighContrast: true,
              selectOnLineNumbers: true,
              fixedOverflowWidgets: true,
              autoClosingDelete: 'always',
              lineNumbers,
              minimap: { enabled: enableMiniMap },
              scrollbar: {
                // Subtle shadows to the left & top. Defaults to true.
                useShadows: false,

                // Render vertical arrows. Defaults to false.
                // verticalHasArrows: true,
                // Render horizontal arrows. Defaults to false.
                // horizontalHasArrows: true,

                // Render vertical scrollbar.
                // Accepted values: 'auto', 'visible', 'hidden'.
                // Defaults to 'auto'
                vertical: 'visible',
                // Render horizontal scrollbar.
                // Accepted values: 'auto', 'visible', 'hidden'.
                // Defaults to 'auto'
                horizontal: 'visible',
                verticalScrollbarSize: 8,
                horizontalScrollbarSize: 8,
                arrowSize: 30
              },
              ...options
            }}
            onMount={editorDidMount}
            //zh-CN: 因为在 handleInitEditorAndLanguageOnBeforeMount 中已经注册了自定义语言，所以这里的作用仅仅是用来切换主题 不需要重新加载自定义语言的 token 样式 , 所以这里入参需要为空, 否则每次任意的 props 改变时(包括高度等),会出现编辑器闪烁的问题
            //en-US: because the custom language has been registered in handleInitEditorAndLanguageOnBeforeMount, so the only purpose here is to switch the theme, and there is no need to reload the token style of the custom language, so the incoming parameters here need to be empty, otherwise any props change (including height, etc.) will cause the editor to flash
            theme={convertCodeEditTheme()}
          />
        </Col>
        {showFloatButton && (
          <Col flex='none'>
            <EditorFloatBtn {...restEditBtnProps} />
          </Col>
        )}
      </Row>
    </>
  );
};

export default CodeShow;
