import React, {useEffect} from "react";
import CodeShow from "@/components/CustomEditor/CodeShow";
import {connect} from "@@/exports";
import {StateType} from "@/pages/DataStudio/model";

const Console: React.FC= (props:any) => {

  useEffect(() => {

  },[])

  return <CodeShow code={props.console} height={(props.height-53)} theme={"vs-dark"} language={"kotlin"}/>
  // return <MonacoEditor width={"100%"} height={props.height-53} theme={"dark"} value={props.console} />
};

export default connect(({Studio}: { Studio: StateType }) => ({
  height: Studio.bottomContainer.height,
  console: Studio.bottomContainerContent.console,
}))(Console);
