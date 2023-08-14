import React from "react";
import CodeEdit from "@/components/CustomEditor/CodeEdit";
import {connect} from "@@/exports";
import {StateType} from "@/pages/DataStudio/model";
import {getCurrentData} from "@/pages/DataStudio/function";
import {convertCodeEditTheme} from "@/utils/function";
import {editor} from "monaco-editor";
import {Editor} from "@monaco-editor/react";

export type EditorProps = {
  statement: string
}
const {ScrollType} = editor;

const CodeEditor: React.FC<EditorProps & any > = (props) => {
  const {
    statement,
    tabs: {panes, activeKey},
    dispatch
  } = props
  const current = getCurrentData(panes, activeKey);


  return <>
    <Editor
      width={"100%"}
      height={"100%"}
      value={statement}
      language={"sql"}
      options={{
        scrollBeyondLastLine: false,
        wordWrap: "on",
        autoDetectHighContrast: true,
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
          vertical: "visible",
          // Render horizontal scrollbar.
          // Accepted values: 'auto', 'visible', 'hidden'.
          // Defaults to 'auto'
          horizontal: "visible",
          verticalScrollbarSize: 8,
          horizontalScrollbarSize: 8,
          arrowSize: 30,
        }
      }}
      className={"editor-develop"}
      onMount={(editor: editor.IStandaloneCodeEditor)=>{
        editor.layout();
        editor.focus();

        editor.onDidChangeCursorPosition((e) => {
          props.footContainer.codePosition=[e.position.lineNumber,e.position.column]
          dispatch({
            type: "Studio/saveFooterValue",
            payload: {...props.footContainer},
          });
        });
      }}
      onChange={(v,d) => {
        current.statement = v;
        dispatch({
          type: "Studio/saveTabs",
          payload: {...props.tabs},
        });
      }}
      theme={"vs-dark"}
    />

    {/*<CodeEdit code={statement} language={"sql"}*/}
    {/*          onChange={(v,d) => {*/}
    {/*            current.statement = v;*/}
    {/*            dispatch({*/}
    {/*              type: "Studio/saveTabs",*/}
    {/*              payload: {...props.tabs},*/}
    {/*            });*/}

    {/*          }}*/}
    {/*/>*/}
  </>
};


export default connect(({Studio}: { Studio: StateType }) => ({
  tabs: Studio.tabs,
  footContainer: Studio.footContainer
}))(CodeEditor);
