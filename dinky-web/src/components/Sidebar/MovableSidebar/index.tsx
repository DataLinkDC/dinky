import React from "react";
import { MinusSquareOutlined, PlusSquareOutlined} from "@ant-design/icons";
import {Enable, Resizable, ResizeCallback, Size} from "re-resizable";
import {PageContainer} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import { TabPaneProps } from "antd";
import {MaxIcon, MinIcon} from "@/components/Icons/StudioIcon";

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
  handlerMaxsize?: () => void;
  onResize?: ResizeCallback;
  style?: React.CSSProperties;
  tagList?: TabPaneProps[];
};

const MovableSidebar: React.FC<MovableSidebarProps> = (props) => {

    const {style, visible,onResize,defaultSize,
        minWidth,maxWidth,minHeight,maxHeight,enable
        ,children, title,contentHeight,handlerMinimize, handlerMaxsize,tagList
    } = props;

    return (
        <Resizable
            style={{...style, display: visible ? 'block' : 'none', boxShadow:"0 0 6px gray",borderRadius: 5}}
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
                tabProps={{
                    type: "editable-card",
                    hideAdd: true,
                    size: "small",
                    animated: true,
                    tabBarStyle: {margin: 0, padding: 0},
                }}
                tabList={tagList}
                extra={[
                    <MinusSquareOutlined size={15}  title={l('global.mini')} key={"minimize"} onClick={handlerMinimize}/>,
                    <PlusSquareOutlined size={15} title={l('global.max')} key={"maximize"} onClick={handlerMaxsize}/>
                ]}
                fixedHeader
            >
                <div style={{height: contentHeight}}>{children}</div>
            </PageContainer>
        </Resizable>
    );
};
export default MovableSidebar;
