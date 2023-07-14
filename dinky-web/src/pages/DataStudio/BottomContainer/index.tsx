import React from "react";
import MovableSidebar from "@/components/Sidebar/MovableSidebar";
import {l} from "@/utils/intl";
import {StateType, VIEW} from "@/pages/DataStudio/model";
import {Space, Tabs} from "antd";
import {LeftBottomSide} from "@/pages/DataStudio/route";
import {connect} from "@@/exports";
import {Title} from "@/components/StyledComponents";

export type BottomContainerProps = {
    size: number
}
const BottomContainer: React.FC<BottomContainerProps> = (props: any) => {
    const {dispatch, size,bottomContainer} = props;


    /**
     * 侧边栏最小化
     */
    const handleMinimize = () => {
        dispatch({
            type: 'Studio/updateSelectBottomKey',
            payload: "",
        })
    }

    /**
     * 更新底部高度
     * @param {number} height
     */
    const updateBottomHeight = (height: number) => {
        dispatch({
            type: "Studio/updateBottomHeight",
            payload: height,
        })
    }

    /**
     * 更新中间内容高度
     * @param {number} height
     */
    const updateCenterContentHeight = (height: number) => {
        dispatch({
            type: "Studio/updateCenterContentHeight",
            payload: height,
        })
    }

    /**
     * 更新工具栏内容高度
     * @param {number} height
     */
    const updateToolContentHeight = (height: number) => {
        dispatch({
            type: "Studio/updateToolContentHeight",
            payload: height,
        })
    }

    /**
     * 拖动回调
     * @param event
     * @param direction
     * @param {{offsetHeight: any}} elementRef
     */
    const resizeCallback = (event: any, direction: any, elementRef: { offsetHeight: any; }) => {
        updateBottomHeight(elementRef.offsetHeight)
        const centerContentHeight = document.documentElement.clientHeight - VIEW.headerHeight - VIEW.headerNavHeight - VIEW.footerHeight - VIEW.otherHeight - bottomContainer.height;
        updateCenterContentHeight(centerContentHeight)
        updateToolContentHeight(centerContentHeight - VIEW.midMargin)
    }


    // export interface TabPaneProps {
    //     tab?: React.ReactNode;
    //     className?: string;
    //     style?: React.CSSProperties;
    //     disabled?: boolean;
    //     children?: React.ReactNode;
    //     forceRender?: boolean;
    //     closable?: boolean;
    //     closeIcon?: React.ReactNode;
    //     prefixCls?: string;
    //     tabKey?: string;
    //     id?: string;
    //     animated?: boolean;
    //     active?: boolean;
    //     destroyInactiveTabPane?: boolean;
    // }

    const renderTabPane = [
        {
            tab: <Title>{(bottomContainer.selectKey === '' ? "" : l(bottomContainer.selectKey))}</Title>,
            closable: false,
            animated: false,
            active: false,
            disabled: true,
        },
        {
            tab: "kkkk",
            closable: true,
            children: <div>sasas</div>,
            tagKey: "llll",
            animated: true,
            active: true,
            destroyInactiveTabPane: true,
        },
        {
            tab: "444",
            closable: true,
            children: <div>444</div>,
            tagKey: "444",
            animated: true,
            active: false,
            destroyInactiveTabPane: true,
        }
    ]

    return (
        <MovableSidebar
            tagList={renderTabPane}
            visible={bottomContainer.selectKey !== ""}
            style={{zIndex: 999 ,height: bottomContainer.height}}
            defaultSize={{width: "100%", height: bottomContainer.height}}
            minHeight={VIEW.midMargin + 10}
            maxHeight={size.contentHeight - 40}
            onResize={(event: any, direction: any, elementRef: { offsetHeight: any; }) => resizeCallback(event, direction, elementRef)}
            enable={{top: true}}
            handlerMinimize={handleMinimize}
        >
            <Tabs activeKey={bottomContainer.selectKey} items={LeftBottomSide} tabBarStyle={{display: "none"}}/>
        </MovableSidebar>
    )
}

export default connect(({Studio}: { Studio: StateType }) => ({
    leftContainer: Studio.leftContainer,
    bottomContainer: Studio.bottomContainer,
    centerContentHeight: Studio.centerContentHeight,
}))(BottomContainer);
