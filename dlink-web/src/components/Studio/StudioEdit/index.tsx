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


import React, {useEffect, useImperativeHandle, useRef, useState} from 'react';
import * as _monaco from "monaco-editor";
import MonacoEditor from "react-monaco-editor";
import {connect, Dispatch} from "umi";
import {DocumentStateType} from "@/pages/RegistrationCenter/Document/model";
import {DocumentTableListItem} from "@/pages/RegistrationCenter/data";
import {parseSqlMetaData} from "@/components/Studio/StudioEvent/Utils";
import {Column, MetaData, SqlMetaData} from "@/components/Studio/StudioEvent/data";
import StudioExplain from "@/components/Studio/StudioConsole/StudioExplain";
import {format} from "sql-formatter";

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
      onChange=(val: string, event: any)=>{},
      options = {
        selectOnLineNumbers: true,
        renderSideBySide: false,
        autoIndent:'None',
        automaticLayout: true,
      },
    sql,
    monaco,
    // sqlMetaData,
    fillDocuments,
    } = props;

  const editorInstance: any = useRef<any>();
  const monacoInstance: any = useRef();
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [metaData, setMetaData] = useState<SqlMetaData>({});
  const [code, setCode] = useState<string>(sql);

  useEffect(
    () => () => {
      reloadCompletion();
    }, [code]);

  useImperativeHandle(editorInstance, () => ({
    handleSetEditorVal,
    getEditorData: () => code,
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

  const onChangeHandle = (val: string, event: any) => {
    setCode(val);
    onChange(val,event);
    /*let newSqlMetaData = parseSqlMetaData(val);
    setMetaData(newSqlMetaData);
    props.saveSqlMetaData(newSqlMetaData,tabsKey);*/
    props.saveSql(val);
  };

  const reloadCompletion = () =>{
    let newSqlMetaData = parseSqlMetaData(code);
    setMetaData({...newSqlMetaData});
    provider.dispose();// 清空提示项
    provider = monacoInstance.current.languages.registerCompletionItemProvider('sql', {
      provideCompletionItems() {
        return {
          suggestions:buildSuggestions(),
        };
      },
      // quickSuggestions: false,
      // triggerCharacters: ['$', '.', '='],
    });
  };

  const buildSuggestions = () => {
    let suggestions: ISuggestions[] = [];
    console.log(metaData);
    metaData.metaData?.forEach((item: MetaData) => {
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

    editor.addCommand(monaco.KeyMod.Alt|monaco.KeyCode.KEY_2,function (){
      handleModalVisible(true);
    })
    editor.addCommand(monaco.KeyMod.Alt|monaco.KeyCode.KEY_3,function (){
      editor.getAction(['editor.action.formatDocument'])._run();
    })

    reloadCompletion();
    monaco.languages.registerDocumentRangeFormattingEditProvider('sql', {
      provideDocumentRangeFormattingEdits(model, range, options) {
        var formatted = format(model.getValueInRange(range), {
          indent: ' '.repeat(options.tabSize)
        });
        formatted = formatted.replaceAll(/` ([^`]*) `/g,function (){return '`'+arguments[1].trim()+'`'})
          .replaceAll(/\$ {([^}]*)}/g,function (){return '${'+arguments[1].trim()+'}'})
          .replaceAll(/\| ([^}]*)\|/g,function (){return '|'+arguments[1].trim()+'|'})
          .replaceAll(/ - /g,function (){return '-'});
        return [
          {
            range: range,
            text: formatted
          }
        ];
      }
    });
    editor.focus();
  };

  return (
    <React.Fragment>
      <MonacoEditor
        ref={monaco}
        width={width}
        height={height}
        language={language}
        value={code}
        options={options}
        onChange={onChangeHandle}
        theme="vs-dark"
        editorDidMount={editorDidMountHandle}
      />
      <StudioExplain
        modalVisible={modalVisible}
        onClose={()=>{handleModalVisible(false)}}
        visible={modalVisible}
      />
    </React.Fragment>
  )
}

const mapDispatchToProps = (dispatch:Dispatch)=>({
  /*saveText:(tabs:any,tabIndex:any)=>dispatch({
    type: "Studio/saveTask",
    payload: tabs.panes[tabIndex].task,
  }),*/
  saveSql:(val: any)=>dispatch({
    type: "Studio/saveSql",
    payload: val,
  }),saveSqlMetaData:(sqlMetaData: any,key: number)=>dispatch({
    type: "Studio/saveSqlMetaData",
    payload: {
      activeKey:key,
      sqlMetaData,
      isModified: true,
    }
  }),
})

export default connect(({ Document }: { Document: DocumentStateType }) => ({
  fillDocuments: Document.fillDocuments,
}),mapDispatchToProps)(FlinkSqlEditor);
