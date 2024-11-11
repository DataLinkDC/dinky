import {Space} from "antd";
import React from "react";


interface ContextMenuSpaceProps {
  onContextMenu: (e: React.MouseEvent<HTMLElement>) => void;
  children: React.ReactNode| JSX.Element | string;
}

export const ContextMenuSpace = (props: ContextMenuSpaceProps) => {

  const {onContextMenu, children} = props;
  return <>
    <Space onContextMenu={onContextMenu} size={0}>
      {children}
    </Space>
  </>
}
