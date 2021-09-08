import React, {useEffect, useImperativeHandle, useRef} from 'react';
import * as _monaco from "monaco-editor";
import MonacoEditor from "react-monaco-editor";
import {BaseDataSourceField, BaseDataSourceHeader, CompletionItem} from "./data";
import Completion from "./completion";
import styles from './index.less';

import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import {DocumentStateType} from "@/pages/Document/model";
import {DocumentTableListItem} from "@/pages/Document/data";

let provider = {
  dispose: () => {},
};

interface IRightContent {
  key: string;
  value: any;
  handleCheck: () => Promise<boolean>;
  secondRightData: (BaseDataSourceField|BaseDataSourceHeader)[];
}

interface ISuggestions {
  label: string;
  kind: string;
  insertText: string;
  detail?: string;
}

const FlinkSqlEditor = (props:any) => {
  const {
      height = '100%',
      width = '100%',
      language = 'sql',
      onChange=(val: string, event: { changes: { text: any }[] })=>{},
      options = {
        selectOnLineNumbers: true,
        renderSideBySide: false,
      },
    tabs,
    fillDocuments,
    dispatch,
    } = props
  ;

  const { tabsKey, value, handleCheck, secondRightData = [] }: IRightContent = props;

  const editorInstance:any = useRef<any>();

  const monacoInstance: any = useRef();

  const getTabIndex = ():number=>{
    for(let i=0;i<tabs.panes.length;i++){
      if(tabs.panes[i].key==tabsKey){
        return i;
      }
    }
    return 0;
  };
  const tabIndex = getTabIndex();
  const code: any = useRef(tabs.panes[tabIndex].value ? tabs.panes[tabIndex].value : '');
  const cache: any = useRef(code.current);

  useEffect(
    () => () => {
      provider.dispose();
    },
    []
  );

  useImperativeHandle(editorInstance, () => ({
    handleSetEditorVal,
    getEditorData: () => cache.current,
  }));

  const handleSetEditorVal = (value: string): void => {
    if (!value) return;
    // 为所选取的值赋值到编辑器中
    if (editorInstance.current && value) {
      const selection = editorInstance?.current?.getSelection?.();
      const range = new _monaco.Range(
        selection.startLineNumber,
        selection.startColumn,
        selection.endLineNumber,
        selection.endColumn
      );
      const id = { major: 1, minor: 1 };
      const op = { identifier: id, range, text: value, forceMoveMarkers: true };
      editorInstance.current.executeEdits('', [op]);
      editorInstance.current.focus();
    }
  };

  const onChangeHandle = (val: string, event: { changes: { text: any }[] }) => {
    onChange(val,event);
    /*const curWord = event.changes[0].text;
    if (curWord === ';') {
      cache.current = val +'\r\n';
      setRefresh(!refresh); // 刷新页面
      return;
    }
    cache.current = val;*/
    dispatch({
      type: "Studio/saveSql",
      payload: val,
    });
  };

  const editorDidMountHandle = (editor: any, monaco: any) => {
    monacoInstance.current = monaco;
    editorInstance.current = editor;
    const newSecondRightFields: BaseDataSourceHeader[] = [];
    (secondRightData as BaseDataSourceHeader[]).forEach((record) => {
      if (record.fields && Array.isArray(record.fields)) {
        record.fields.forEach((item: any) => {
          newSecondRightFields.push(item);
        });
      }
    });
    code.current = newSecondRightFields; // 数组长度永远为1
    // 提示项设值
    provider = monaco.languages.registerCompletionItemProvider('sql', {
      provideCompletionItems() {
        let suggestions: ISuggestions[] = [];
        if (code && code.current) {
          code.current.forEach((record:any) => {
            suggestions.push({
              // label未写错 中间加空格为了隐藏大写字段名称 大写字段名称用于规避自定义提示不匹配小写的bug
              label:
              record.label ||
              `${record.displayName} (${
                record.aliasName
                })                        ${''}(${record.aliasName.toUpperCase()})`, // 显示名称
              kind: record.kind || monaco.languages.CompletionItemKind.Field, // 这里Function也可以是别的值，主要用来显示不同的图标
              insertText: record.insertText || record.aliasName, // 实际粘贴上的值
              detail: record.detail || `(property) ${record.aliasName}: String`,
            });
          });
        }
        fillDocuments.forEach((item:DocumentTableListItem) => {
          if(monaco.languages.CompletionItemKind[item.category]) {
            suggestions.push({
              label: item.name,
              kind: monaco.languages.CompletionItemKind[item.category],
              insertText: item.fillValue,
              insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
              detail: item.description
            });
          }else {
            suggestions.push({
              label: item.name,
              kind: monaco.languages.CompletionItemKind.Text,
              insertText: item.fillValue,
              insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
              detail: item.description
            });
          }
        });
        return {
          suggestions,
        };
      },
      quickSuggestions: false,
      // triggerCharacters: ['$', '.', '='],
    });
    editor.focus();
  };

return (
  <React.Fragment>
    <MonacoEditor
      ref={tabs.panes[tabIndex].monaco}
      width={width}
      height={height}
      language={language}
      value={tabs.panes[tabIndex].value}
      options={options}
      onChange={onChangeHandle}
      theme="vs-dark"
      editorDidMount={editorDidMountHandle}
    />
  </React.Fragment>
);
};

export default connect(({ Studio,Document }: { Studio: StateType,Document: DocumentStateType }) => ({
  current: Studio.current,
  sql: Studio.sql,
  tabs: Studio.tabs,
  monaco: Studio.monaco,
  fillDocuments: Document.fillDocuments,
}))(FlinkSqlEditor);
