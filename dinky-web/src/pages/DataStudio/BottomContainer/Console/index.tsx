import React, {useEffect} from "react";
import JobTree from "@/pages/DataStudio/LeftContainer/Project/JobTree";
import CodeShow from "@/components/CustomEditor/CodeShow";
import {connect} from "@@/exports";
import {StateType, VIEW} from "@/pages/DataStudio/model";

const Console: React.FC= (props:any) => {


  useEffect(() => {

  },[])

  console.log(props.height-51)


  return <>
    {props.height-51>0 && <CodeShow code={""} height={(props.height-51).toString()}/>}

  </>;
};

export default connect(({Studio}: { Studio: StateType }) => ({
  height: Studio.bottomContainer.height,
}))(Console);
