import React from "react";
import {Button, ConfigProvider} from "antd";
import {MinusOutlined} from "@ant-design/icons";
import {Enable, Resizable, ResizeCallback, Size} from "re-resizable";
import { PageContainer } from "@ant-design/pro-layout/es/components/PageContainer";
import {ProLayout} from "@ant-design/pro-layout";

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
  children?: React.ReactNode;
  handlerMinimize?: () => void;
  onResize?: ResizeCallback;
  style?: React.CSSProperties;
};

const MovableSidebar: React.FC<MovableSidebarProps> = (props) => {
  return (
    <Resizable
      style={{...props.style,display:props.visible?'block':'none'}}
      onResize={props.onResize}
      defaultSize={props.defaultSize}
      minWidth={props.minWidth}
      maxWidth={props.maxWidth}
      minHeight={props.minHeight}
      maxHeight={props.maxHeight}
      enable={props.enable}
    >
      <PageContainer
        style={{backgroundColor:'#fff',border:"1px solid rgb(240, 240, 240)"}}
        header={{
          title: props.title,
          extra: [
            <Button key={"minimize"} icon={<MinusOutlined/>} block type={"text"} shape={"circle"}
                    onClick={props.handlerMinimize}/>
          ],
          style: {borderBottom: '1px solid rgb(240, 240, 240)'}
        }}
      >
          <div style={{height: props.contentHeight}}>
            {props.children}
          </div>
      </PageContainer>
    </Resizable>
  );
};
export default MovableSidebar;
