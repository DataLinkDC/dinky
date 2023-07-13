import React from "react";
import {MinusOutlined} from "@ant-design/icons";
import {Enable, Resizable, ResizeCallback, Size} from "re-resizable";
import {PageContainer} from "@ant-design/pro-components";

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

    const {style, visible,onResize,defaultSize,
        minWidth,maxWidth,minHeight,maxHeight,enable
        ,children, title,contentHeight,handlerMinimize
    } = props;

    return (
        <Resizable
            style={{...style, display: visible ? 'block' : 'none'}}
            onResize={onResize}
            defaultSize={defaultSize}
            minWidth={minWidth}
            maxWidth={maxWidth}
            minHeight={minHeight}
            maxHeight={maxHeight}
            enable={enable}
        >
            <PageContainer
                style={{backgroundColor: '#fff', border: "1px solid rgb(240, 240, 240)"}}
                title={title}
                extra={<MinusOutlined key={"minimize"} onClick={handlerMinimize}/>}
                fixedHeader
                header={{
                    style: {borderBottom: '1px solid rgb(240, 240, 240)'}
                }}
            >
                <div style={{height: contentHeight}}>{children}</div>
            </PageContainer>
        </Resizable>
    );
};
export default MovableSidebar;
