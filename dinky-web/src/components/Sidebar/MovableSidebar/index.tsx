import React from "react";
import {Button} from "antd";
import {PageContainer} from "@ant-design/pro-layout";
import {MinusOutlined} from "@ant-design/icons";
import {Enable, Resizable, ResizeCallback, Size} from "re-resizable";

export type MovableSidebarProps = {
  title?: React.ReactNode;
  enable?: Enable;
  minWidth?: string | number;
  minHeight?: string | number;
  maxWidth?: string | number;
  maxHeight?: string | number;
  contentHeight?: number;
  defaultSize?: Size;
  visible?: boolean;
  content?: React.ReactNode;
  handlerMinimize?: () => void;
  onResize?: ResizeCallback;
  style?: React.CSSProperties;
};

const MovableSidebar: React.FC<MovableSidebarProps> = (props) => {
  return (
    !props.visible ? <></> : <Resizable
      style={props.style}
      onResize={props.onResize}
      defaultSize={props.defaultSize}
      minWidth={props.minWidth}
      maxWidth={props.maxWidth}
      minHeight={props.minHeight}
      maxHeight={props.maxHeight}
      enable={props.enable}
    >

      <PageContainer
        header={{
          title: props.title,
          extra: [
            <Button key={"minimize"} icon={<MinusOutlined/>} block type={"text"} shape={"circle"}
                    onClick={props.handlerMinimize}/>
          ],
          style: {borderBottom: '1px solid black'}
        }}
      >
          <div style={{height: props.contentHeight}}>
            {props.content}
          </div>
      </PageContainer>

    </Resizable>
  );
};
export default MovableSidebar;
