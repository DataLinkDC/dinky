import React, {ReactNode, useState} from "react";
import {Button, Tooltip} from "antd";
import {sleep} from "@antfu/utils";

export type RunToolBarButtonProps = {
  showDesc: boolean;
  desc: string,
  icon: ReactNode,
  onClick?: () => Promise<void>;
  color?: string;
  sleepTime?: number
}

export default (props: RunToolBarButtonProps) => {
  const {showDesc, desc, icon, onClick, color, sleepTime} = props;
  const [loading, setLoading] = useState(false)
  const style = color ? {color: color} : {};
  return (<Tooltip title={desc}>
    <Button loading={loading} htmlType={'submit'} type="text" icon={icon} onClick={async () => {
      setLoading(true)
      if (onClick) {
        try {
          await onClick()
        } catch (e) {
        }
      }
      await sleep(sleepTime ?? 500)
      setLoading(false)
    }} style={{...style, padding: '1px 6px'}}>{showDesc ? desc : ""}</Button>
  </Tooltip>)
}


