import React, {useEffect, useImperativeHandle, useRef} from 'react';
import * as _monaco from "monaco-editor";
// import MonacoEditor from "react-monaco-editor/lib/editor";
import MonacoEditor from "react-monaco-editor";
import {BaseDataSourceField, BaseDataSourceHeader, CompletionItem} from "./data";
import Completion from "./completion";

import {executeSql} from "@/pages/FlinkSqlStudio/service";
import {StateType} from "@/pages/FlinkSqlStudio/model";
import {connect} from "umi";

let provider = {
  dispose: () => {},
};

interface IRightContent {
  value: any;
  handleCheck: () => Promise<boolean>;
  secondRightData: (BaseDataSourceField|BaseDataSourceHeader)[];
}

const FlinkSqlEditor = (props:any) => {
  const {
      height = '300px',
      width = '100%',
      language = 'sql',
      onChange=(val: string, event: { changes: { text: any }[] })=>{},
      options = {
        selectOnLineNumbers: true,
        renderSideBySide: false,
      },
    sql=props.catalogue.sql,
    // sql,
    dispatch,
    } = props
  ;

  const { value, handleCheck, secondRightData = [] }: IRightContent = props;

  const editorInstance:any = useRef<any>();

  const monacoInstance: any = useRef();

  const code: any = useRef(sql ? sql : '');
  // const code: any = useRef(value ? value.formulaContent : '');

  const cache: any = useRef(code.current);

  const [refresh, setRefresh] = React.useState<boolean>(false);

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

  const submit = async () => {
    await executeSql({statement:cache.current});
  };

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

  interface ISuggestions {
    label: string;
    kind: string;
    insertText: string;
    detail?: string;
  }

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
        const suggestions: ISuggestions[] = [];
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
        Completion.forEach((item:CompletionItem) => {
          suggestions.push(item);
        });
        return {
          suggestions,
        };
      },
      quickSuggestions: true,
      triggerCharacters: ['$', '.', '='],
    });
    editor.focus();
  };

return (
  <React.Fragment>
    <MonacoEditor
      width={width}
      height={height}
      language={language}
      //value={cache.current}
      options={options}
      onChange={onChangeHandle}
      theme="vs-dark"
      editorDidMount={editorDidMountHandle}
    />
  </React.Fragment>
);
};

export default connect(({ Studio }: { Studio: StateType }) => ({
  current: Studio.current,
  catalogue: Studio.catalogue,
  sql: Studio.sql,
}))(FlinkSqlEditor);
