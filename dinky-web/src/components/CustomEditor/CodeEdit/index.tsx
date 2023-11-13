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
import { editor } from 'monaco-editor';

import { buildAllSuggestionsToEditor } from '@/components/CustomEditor/CodeEdit/function';
import EditorFloatBtn from '@/components/CustomEditor/EditorFloatBtn';
import { StateType } from '@/pages/DataStudio/model';
import { MonacoEditorOptions } from '@/types/Public/data';
import { convertCodeEditTheme } from '@/utils/function';
import { Editor, Monaco, OnChange } from '@monaco-editor/react';
import { connect } from '@umijs/max';
import { memo, useCallback, useRef } from 'react';
import ITextModel = editor.ITextModel;
import useMemoCallback from "rc-menu/es/hooks/useMemoCallback";
import {useAsyncEffect, useUpdateEffect} from "ahooks";


let provider = {
  dispose: () => {},
};


export type CodeEditFormProps = {
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
  editorDidMount?: (editor: editor.IStandaloneCodeEditor, monaco: Monaco) => void;
  enableSuggestions?: boolean;
  monacoRef?: any;
  editorRef?: any;
};

const CodeEdit = (props: CodeEditFormProps & connect) => {
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
    height = '100%', // if null or undefined, set default value
    width = '100%', // if null or undefined, set default value
    language, // edit language
    options = {
      ...MonacoEditorOptions // set default options
    },
    onChange, // edit change callback
    code, // content
    readOnly = false, // is readOnly
    lineNumbers, // show lineNumbers
    enableSuggestions = false, // enable suggestions
    suggestionsData, // suggestions data
    autoWrap = 'on', // auto wrap
    showFloatButton = false,
    editorDidMount,
    editorRef,
    monacoRef
  } = props;

  const editorInstance = useRef<editor.IStandaloneCodeEditor | any>(editorRef);
  const monacoInstance = useRef<Monaco | any>(monacoRef);

  const { ScrollType } = editor;

  /**
   * build all suggestions
   */
  const buildAllSuggestions = useCallback(async (model: ITextModel, position: monaco.Position) => {
    return buildAllSuggestionsToEditor(model, position, suggestionsData);
  }, [code]);


  /**
   * 当code变化时，重新注册provider 以及 dispose
   */
  useUpdateEffect(() => {
    provider.dispose();
  } , [code])

  /**
   *  editorDidMount
   * @param {editor.IStandaloneCodeEditor} editor
   * @param monaco
   */
  const editorDidMountChange = (editor: editor.IStandaloneCodeEditor, monaco: Monaco) => {
    if (editorRef.current || monacoRef.current || editorDidMount) {
      editorDidMount(editor, monaco);
    } else {
      editorInstance.current = editor;
      monacoInstance.current = monaco;
    }
    if (enableSuggestions) {
      provider= monaco.languages.registerCompletionItemProvider(language || 'sql', {
         provideCompletionItems: (model: ITextModel, position: monaco.Position) => {
          return buildAllSuggestions(model, position);
        }
      });
    }
    // register TypeScript language service
    monaco.languages.register({
      id: language || 'typescript'
    });

    editor.layout();
    editor.focus();
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
    editorInstance?.current?.revealLine(editorInstance?.current?.getModel()?.().getLineCount());
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

  // todo: 标记错误信息



  const restEditBtnProps = {
    handleBackTop,
    handleBackBottom,
    handleUpScroll,
    handleDownScroll
  };

  const finalEditorOptions = {
    ...options,
    tabCompletion: 'on', // tab 补全
    cursorSmoothCaretAnimation: true, // 光标动画
    screenReaderAnnounceInlineSuggestion: true, // 屏幕阅读器提示
    formatOnPaste: true, // 粘贴时格式化
    mouseWheelZoom: true, // 鼠标滚轮缩放
    autoClosingBrackets: 'always', // 自动闭合括号
    autoClosingOvertype: 'always', // 用于在右引号或括号上键入的选项
    autoClosingQuotes: 'always', // 自动闭合引号
    showUnused: true, // 显示未使用的代码
    unfoldOnClickAfterEndOfLine: true, // 控制在折叠线之后单击空内容是否会展开该线
    showFoldingControls: 'always', // 代码折叠控件 'always' | 'mouseover' | 'never'
    automaticLayout: true, // 自动布局
    readOnly, // 是否只读
    wrappingIndent:
      language === 'yaml' || language === 'yml' || language === 'json' ? 'indent' : 'none',
    inlineSuggest: {
      enabled: true,
      showToolbar: 'always',
      keepOnBlur: false
    },
    suggest: {
      quickSuggestions: true,
      showStatusBar: true,
      preview: true,
      previewMode: 'preview',
      showVariables: true,
      showFields: true,
      showKeywords: true,
      showWords: true
    },
    scrollbar: {
      useShadows: false,
      vertical: 'visible',
      horizontal: 'visible',
      verticalScrollbarSize: 8,
      horizontalScrollbarSize: 8,
      arrowSize: 30
    },
    wordWrap: autoWrap,
    autoDetectHighContrast: true,
    lineNumbers
  };

  return (
    <>
      <div className={'monaco-float'}>
        <Editor
          width={width}
          height={height}
          value={code}
          language={language}
          options={finalEditorOptions}
          className={'editor-develop'}
          onMount={editorDidMountChange}
          onChange={onChange}
          theme={convertCodeEditTheme()}
        />
        {showFloatButton && <EditorFloatBtn {...restEditBtnProps} />}
      </div>
    </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  suggestionsData: Studio.suggestions
}))(memo(CodeEdit));
