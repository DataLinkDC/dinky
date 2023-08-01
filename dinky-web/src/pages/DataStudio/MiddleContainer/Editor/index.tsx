import React from "react";
import CodeEdit from "@/components/CustomEditor/CodeEdit";

export type EditorProps = {
  statement: string
}
const Editor: React.FC<EditorProps> = (props) => {
  const {statement} = props

  return <>
    <CodeEdit code={statement} language={"sql"} />
  </>
};


export default Editor;
