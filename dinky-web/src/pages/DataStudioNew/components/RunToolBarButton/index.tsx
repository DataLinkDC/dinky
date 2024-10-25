import React, {ReactNode, useCallback, useEffect, useState} from "react";
import {Button, Tooltip} from "antd";
import {sleep} from "@antfu/utils";

// 快捷键属性
export type HotKeyProps = {
  enable: boolean;
  hotKeyDesc: string;
  hotKeyHandle: (e: KeyboardEvent) => boolean;

}
export type RunToolBarButtonProps = {
  showDesc: boolean;
  desc: string,
  icon: ReactNode,
  onClick?: () => Promise<void>;
  color?: string;
  sleepTime?: number;
  hotKey?: HotKeyProps;
  isShow?: boolean
  disabled?: boolean
}

export default (props: RunToolBarButtonProps) => {
  const {showDesc, desc, icon, onClick, color, sleepTime, hotKey, isShow = true,disabled=false} = props;
  const [loading, setLoading] = useState(false)
  const style = color ? {color: color} : {};

  const onClickHandle = useCallback(async () => {
    setLoading(true)
    if (onClick) {
      try {
        await onClick()
      } catch (e) {
      }
    }
    await sleep(sleepTime ?? 500)
    setLoading(false)
  }, [onClick, sleepTime])
  useEffect(() => {
    const hotKeyFuncHandle = (e: KeyboardEvent) => {
      if (hotKey?.hotKeyHandle(e)) {
        e.preventDefault()
        onClickHandle()
      }
    };
    if (hotKey?.enable) {
      document.addEventListener('keydown', hotKeyFuncHandle)
    }
    return () => {
      if (hotKey?.enable) {
        document.removeEventListener('keydown', hotKeyFuncHandle)
      }
    }
  }, [hotKey?.enable]);


  const tooltipDesc = hotKey?.enable ? `${desc} : (${hotKey.hotKeyDesc})` : desc;
  return (isShow && <Tooltip title={tooltipDesc}>
    <Button disabled={disabled} loading={loading} htmlType={'submit'} type="text" icon={icon} onClick={onClickHandle}
            style={{...style, padding: '1px 6px'}}>{showDesc ? desc : ""}</Button>
  </Tooltip>)
}


