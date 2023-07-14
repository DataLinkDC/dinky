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


    /**
     * 渲染标题
     * @returns {JSX.Element}
     */
    const renderTitle = () => {
        return <>
            <Space align={'baseline'} size={'small'}>
                <Title>{(bottomContainer.selectKey === '' ? "" : l(bottomContainer.selectKey))}</Title>
                <Tabs
                    type="editable-card" hideAdd
                    defaultActiveKey="StudioMsg" size="small" tabPosition="top"
                    items={[
                        {
                            key: "test",
                            label: "123456"
                        }, {
                            key: "tes2",
                            label: "123"
                        }
                    ]}
                />
            </Space>
        </>
    }


    return (
        <MovableSidebar
            title={renderTitle()}
            visible={bottomContainer.selectKey !== ""}
            style={{zIndex: 999}}
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
