import React, {useEffect} from "react";
import JobTree from "@/pages/DataStudio/LeftContainer/Project/JobTree";
import CodeShow from "@/components/CustomEditor/CodeShow";
import {connect} from "@@/exports";
import {StateType, VIEW} from "@/pages/DataStudio/model";

const Console: React.FC= (props:any) => {


  useEffect(() => {

  },[])

  console.log(props.height-53)


  return <>
    {<CodeShow code={""} height={(props.height-53).toString()}/>}

  </>;
};

export default connect(({Studio}: { Studio: StateType }) => ({
  height: Studio.bottomContainer.height,
}))(Console);
