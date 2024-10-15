import React, {ReactNode} from "react";
import {Button, Tooltip} from "antd";

export type RunToolBarButtonProps = {
  showDesc: boolean;
  desc: string,
  icon: ReactNode,
  onClick?: () => void;
  color?: string;
}

 export default (props: RunToolBarButtonProps) => {
  const {showDesc, desc, icon, onClick,color} = props;
  const style = color?{color: color }:{};
  return (<Tooltip title={desc}>
    <Button type="text" icon={icon} onClick={onClick} style={{...style,padding:'1px 6px'}}>{showDesc ? desc : ""}</Button>
  </Tooltip>)
}


