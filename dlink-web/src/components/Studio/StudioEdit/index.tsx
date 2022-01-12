import React, {useEffect, useImperativeHandle, useRef,useState} from 'react';
import * as _monaco from "monaco-editor";
import MonacoEditor from "react-monaco-editor";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect,Dispatch} from "umi";
import {DocumentStateType} from "@/pages/Document/model";
import {DocumentTableListItem} from "@/pages/Document/data";
import {parseSqlMetaData} from "@/components/Studio/StudioEvent/Utils";
import {Column, MetaData} from "@/components/Studio/StudioEvent/data";
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
      onChange=(val: string, event: { changes: { text: any }[] })=>{},
      options = {
        selectOnLineNumbers: true,
        renderSideBySide: false,
      },
    tabs,
    fillDocuments,
    } = props;

  const editorInstance:any = useRef<any>();
  const monacoInstance: any = useRef();
  const [modalVisible, handleModalVisible] = useState<boolean>(false);

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
    props.saveMetaData(sqlMetaData,tabs,tabIndex);
    onChange(val,event);
    /*const curWord = event.changes[0].text;
    if (curWord === ';') {
      cache.current = val +'\r\n';
      setRefresh(!refresh); // 刷新页面
      return;
    }
    cache.current = val;*/
    props.saveSql(val);
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
    editor.addCommand(monaco.KeyMod.CtrlCmd|monaco.KeyCode.KEY_1,function (){
      props.saveText(tabs,tabIndex);
    })

    editor.addCommand(monaco.KeyMod.CtrlCmd|monaco.KeyCode.KEY_2,function (){
      handleModalVisible(true);
    })
    editor.addCommand(monaco.KeyMod.CtrlCmd|monaco.KeyCode.KEY_3,function (){
      editor.getAction(['editor.action.formatDocument'])._run();
    })
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
    monaco.languages.registerDocumentRangeFormattingEditProvider('sql', {
      provideDocumentRangeFormattingEdits(model, range, options) {
        var formatted = format(model.getValueInRange(range), {
          indent: ' '.repeat(options.tabSize)
        });
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
      <StudioExplain
        modalVisible={modalVisible}
        onClose={()=>{handleModalVisible(false)}}
        visible={modalVisible}
      />
    </React.Fragment>
  )
}

const mapDispatchToProps = (dispatch:Dispatch)=>({
  saveText:(tabs:any,tabIndex:any)=>dispatch({
    type: "Studio/saveTask",
    payload: tabs.panes[tabIndex].task,
  }),
  saveSql:(val:any)=>dispatch({
    type: "Studio/saveSql",
    payload: val,
  }),
  saveMetaData:(sqlMetaData:any,tabs:any,tabIndex:any)=>dispatch({
             type: "Studio/saveSqlMetaData",
             payload: {
               activeKey:tabs.panes[tabIndex].key,
               sqlMetaData,
               isModified: true,
             }
           })
})

export default connect(({ Studio,Document }: { Studio: StateType,Document: DocumentStateType }) => ({
  current: Studio.current,
  sql: Studio.sql,
  tabs: Studio.tabs,
  monaco: Studio.monaco,
  fillDocuments: Document.fillDocuments,
}),mapDispatchToProps)(FlinkSqlEditor);
