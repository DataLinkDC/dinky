import React, {useEffect, useImperativeHandle, useRef} from 'react';
import * as _monaco from "monaco-editor";
import MonacoEditor from "react-monaco-editor";
import {BaseDataSourceField, BaseDataSourceHeader, CompletionItem} from "./data";
import styles from './index.less';
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";
import {DocumentStateType} from "@/pages/Document/model";
import {DocumentTableListItem} from "@/pages/Document/data";
import {parseSqlMetaData} from "@/components/Studio/StudioEvent/Utils";
import {Column, MetaData} from "@/components/Studio/StudioEvent/data";

let provider = {
  dispose: () => {},
};

interface ISuggestions {
  label: string;
  kind: string;
  insertText: string;
  detail?: string;
}

const FlinkSqlEditor = (props:any) => {
  const {
      tabsKey,
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
    } = props;

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
    let sqlMetaData = parseSqlMetaData(val);
    dispatch({
      type: "Studio/saveSqlMetaData",
      payload: {
        activeKey:tabs.panes[tabIndex].key,
        sqlMetaData,
        isModified: true,
      },
    });
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

  const buildSuggestions = () => {
    let suggestions: ISuggestions[] = [];
    tabs.panes[tabIndex].sqlMetaData?.metaData?.forEach((item:MetaData) => {
      suggestions.push({
        label: item.table,
        kind: _monaco.languages.CompletionItemKind.Constant,
        insertText: item.table,
        insertTextRules: _monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        detail: 'FlinkSQL Connector => '+item.connector
      });
      item.columns.forEach((column:Column) => {
        suggestions.push({
          label: column.name,
          kind: _monaco.languages.CompletionItemKind.Field,
          insertText: column.name,
          insertTextRules: _monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
          detail: 'Column => '+column.type +' from '+item.table
        });
      })
    });
    fillDocuments.forEach((item:DocumentTableListItem) => {
      if(_monaco.languages.CompletionItemKind[item.category]) {
        suggestions.push({
          label: item.name,
          kind: _monaco.languages.CompletionItemKind[item.category],
          insertText: item.fillValue,
          insertTextRules: _monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
          detail: item.description
        });
      }else {
        suggestions.push({
          label: item.name,
          kind: _monaco.languages.CompletionItemKind.Text,
          insertText: item.fillValue,
          insertTextRules: _monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
          detail: item.description
        });
      }
    });
    return suggestions;
  };

  const editorDidMountHandle = (editor: any, monaco: any) => {
    monacoInstance.current = monaco;
    editorInstance.current = editor;
    provider.dispose();// 清空提示项
    // 提示项设值
    provider = monaco.languages.registerCompletionItemProvider('sql', {
      provideCompletionItems() {
        return {
          suggestions:buildSuggestions(),
        };
      },
      // quickSuggestions: false,
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
