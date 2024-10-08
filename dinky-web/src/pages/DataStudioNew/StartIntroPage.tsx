import KeyBoard from "@/pages/DataStudio/MiddleContainer/KeyBoard";
import {Divider} from "antd";
import QuickGuide from "@/pages/DataStudio/MiddleContainer/QuickGuide";
import React from "react";

export default () => {
  return (
    <div style={{userSelect:"none"}}>
      <KeyBoard/>
      <Divider/>
      <br/>
      <br/>
      <br/>
      <QuickGuide/>
    </div>
  )
}
