import {QUICK_GUIDE} from "@/pages/DataStudio/MiddleContainer/QuickGuide/constant";
import {Divider, Typography} from "antd";
import {Link} from "@@/exports";
import React, {useEffect, useState} from "react";
import CodeEdit from "@/components/CustomEditor/CodeEdit";
import {getTaskDetails} from "@/pages/DataStudio/LeftContainer/Project/service";

export type EditorProps = {
  id: number
}
const Editor: React.FC<EditorProps> = (props) => {
  const [data, setData] = useState({});
  useEffect(() => {
    getTaskDetails(props.id).then(setData)}
  ,[])
  return <>
    <CodeEdit code={data.statement} language={"sql"} />
  </>
};


export default Editor;
