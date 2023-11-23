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
import { editor, languages, Position } from 'monaco-editor';

import { buildAllSuggestionsToEditor } from '@/components/CustomEditor/CodeEdit/function';
import { LoadCustomEditorLanguageWithCompletion } from '@/components/CustomEditor/languages';
import { StateType } from '@/pages/DataStudio/model';
import { MonacoEditorOptions } from '@/types/Public/data';
import { convertCodeEditTheme } from '@/utils/function';
import { Editor, Monaco, OnChange } from '@monaco-editor/react';
import { connect } from '@umijs/max';
import useMemoCallback from 'rc-menu/es/hooks/useMemoCallback';
import { memo, useCallback, useRef } from 'react';
import ITextModel = editor.ITextModel;
import CompletionItem = languages.CompletionItem;
import CompletionContext = languages.CompletionContext;

let provider = {
  dispose: () => {}
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
  autoWrap?: string;
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
    options,
    onChange, // edit change callback
    code, // content
    readOnly = false, // is readOnly
    lineNumbers, // show lineNumbers
    enableSuggestions = false, // enable suggestions
    suggestionsData, // suggestions data
    autoWrap = 'on', // auto wrap
    editorDidMount,
    editorRef,
    monacoRef,
    tabs: { activeKey }
  } = props;

  const editorInstance = useRef<editor.IStandaloneCodeEditor | undefined>(editorRef);
  const monacoInstance = useRef<Monaco | undefined>(monacoRef);

  /**
   * build all suggestions
   */
  const buildAllSuggestions = (model: ITextModel, position: monaco.Position) => {
    return buildAllSuggestionsToEditor(model, position, suggestionsData);
  };

  const buildAllSuggestionsCallback = useCallback(
    async (model: ITextModel, position: monaco.Position) => {
      return buildAllSuggestions(model, position);
    },
    [code, activeKey]
  );

  // memo
  const memoizedBuildAllSuggestionsCallback = useMemoCallback(buildAllSuggestionsCallback);

  function reloadCompilation(monacoIns: Monaco) {
    provider.dispose();
    provider = monacoIns.languages.registerCompletionItemProvider(language, {
      provideCompletionItems: (
        model: editor.ITextModel,
        position: Position,
        context: CompletionContext
      ) => {
        const allSuggestions = memoizedBuildAllSuggestionsCallback(model, position);
        context.triggerKind =
          monacoIns.languages.CompletionTriggerKind.TriggerForIncompleteCompletions;
        return allSuggestions;
      },
      resolveCompletionItem: (item: CompletionItem) => {
        return {
          ...item,
          detail: item.detail
        };
      }
    });
  }

  /**
   *  editorDidMount
   * @param {editor.IStandaloneCodeEditor} editor
   * @param monacoIns
   */
  const editorDidMountChange = (editor: editor.IStandaloneCodeEditor, monacoIns: Monaco) => {
    if (editorDidMount) {
      editorDidMount(editor, monacoIns);
    }
    editorInstance.current = editor;
    monacoInstance.current = monacoIns;
    if (enableSuggestions) {
      reloadCompilation(monacoInstance.current);
    }
    editor.layout();
    editor.focus();
  };

  // todo: 标记错误信息

  const finalEditorOptions = {
    ...MonacoEditorOptions, // set default options
    tabCompletion: 'on', // tab 补全
    cursorSmoothCaretAnimation: false, // 光标动画
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
    glyphMargin: true, // 字形边缘
    formatOnType: true, // 代码格式化
    wrappingIndent:
      language === 'yaml' || language === 'yml' || language === 'json' ? 'indent' : 'none',
    inlineSuggest: {
      enabled: true,
      showToolbar: 'always',
      keepOnBlur: false,
      allowQuickSuggestions: true,
      showOnAllSymbols: true
    },
    // inlineSuggestionVisible: true,
    quickSuggestions: enableSuggestions,
    guides: {
      bracketPairs: true
    },
    bracketPairColorization: {
      enabled: true,
      independentColorPoolPerBracketType: true
    },
    foldingRanges: true,
    inlineCompletionsAccessibilityVerbose: true,
    smartSelect: {
      selectLeadingAndTrailingWhitespace: true,
      selectSubwords: true
    },
    suggest: {
      quickSuggestions: enableSuggestions,
      showStatusBar: true,
      preview: true,
      previewMode: 'subword',
      showInlineDetails: true,
      showMethods: true,
      showFunctions: true,
      showConstructors: true,
      showFields: true,
      showEvents: true,
      showOperators: true,
      showClasses: true,
      showModules: true,
      showStructs: true,
      showInterfaces: true,
      showProperties: true,
      showUnits: true,
      showValues: true,
      showConstants: true,
      showEnums: true,
      showEnumMembers: true,
      showKeywords: true,
      showWords: true,
      showFolders: true,
      showReferences: true,
      showSnippets: true
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
    lineNumbers,
    ...options
  };
  return (
    <>
      <div className={'monaco-float'}>
        <Editor
          beforeMount={(monaco) => {
            // 挂载前加载语言 | before mount load language
            monacoInstance.current = monaco;
            LoadCustomEditorLanguageWithCompletion(monaco);
          }}
          width={width}
          height={height}
          value={code}
          language={language}
          options={finalEditorOptions}
          className={'editor-develop'}
          onMount={editorDidMountChange}
          onChange={onChange}
          theme={convertCodeEditTheme(editorInstance?.current)}
        />
      </div>
    </>
  );
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  suggestionsData: Studio.suggestions,
  tabs: Studio.tabs
}))(memo(CodeEdit));
