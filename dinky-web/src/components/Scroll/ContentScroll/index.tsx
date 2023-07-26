import React from "react";
import {theme} from "antd";

export type ContentScrollProps = {
  height:number|string
  children?: React.ReactNode;
}
const { useToken } = theme;

const ContentScroll: React.FC<ContentScrollProps>  = (props) => {
  const {height,children}=props
  const {token} = useToken();

  return (
    <div className="content-scroll" style={{height:height,display:height<1?"none":"block",backgroundColor:token.colorBgBase}}>
      {children}
    </div>
  )
}
export default ContentScroll;
