import React from "react";
import { MinusSquareOutlined} from "@ant-design/icons";
import {Enable, Resizable, ResizeCallback, Size} from "re-resizable";
import {PageContainer} from "@ant-design/pro-components";
import {l} from "@/utils/intl";

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
                title={<h5>{title}</h5>}
                extra={<MinusSquareOutlined title={l('global.mini')} key={"minimize"} onClick={handlerMinimize}/>}
                fixedHeader
            >
                <div style={{height: contentHeight}}>{children}</div>
            </PageContainer>
        </Resizable>
    );
};
export default MovableSidebar;
