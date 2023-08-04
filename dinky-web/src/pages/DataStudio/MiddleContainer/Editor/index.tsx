import React from "react";
import CodeEdit from "@/components/CustomEditor/CodeEdit";
import {connect} from "@@/exports";
import {StateType} from "@/pages/DataStudio/model";
import {getCurrentData} from "@/pages/DataStudio/function";

export type EditorProps = {
  statement: string
}
const Editor: React.FC<EditorProps & any > = (props) => {
  const {
    statement,
    tabs: {panes, activeKey},
    dispatch
  } = props
  const current = getCurrentData(panes, activeKey);

  return <>
    <CodeEdit code={statement} language={"sql"}
              onChange={(v,d) => {
                current.statement = v;
                dispatch({
                  type: "Studio/saveTabs",
                  payload: {...props.tabs},
                });

              }}
    />
  </>
};


export default connect(({Studio}: { Studio: StateType }) => ({
  tabs: Studio.tabs
}))(Editor);
