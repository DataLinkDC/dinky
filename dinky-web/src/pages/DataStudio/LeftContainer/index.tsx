import {Tabs} from "antd";
import MovableSidebar, {MovableSidebarProps} from "@/components/Sidebar/MovableSidebar";
import React from "react";
import {connect} from "@@/exports";
import {StateType, VIEW} from "@/pages/DataStudio/model";
import {LeftSide} from "@/pages/DataStudio/route";
import {l} from "@/utils/intl";

export type LeftContainerProps = {
  size:number
}
const LeftContainer:React.FC<LeftContainerProps> = (props:any) => {
  const {dispatch,size} = props;


  const {toolContentHeight ,leftContainer , rightContainer} = props

    /**
     * 侧边栏大小变化
     * @param width
     */
  const handleReSizeChange = (width: any) => {
      dispatch({
          type: 'Studio/updateLeftWidth',
          payload: width,
      })
  }

    /**
     * 侧边栏最小化
     */
    const handleMinimize = () => {
        dispatch({
            type: 'Studio/updateSelectLeftKey',
            payload: "",
        })
    }

    /**
     * 侧边栏最大化
     */
    const handleMaxsize = (widthChangeValue: number) => {
        handleReSizeChange(widthChangeValue)
    }

    /**
     * 侧边栏属性
     * @type {{onResize: (event: any, direction: any, elementRef: {offsetWidth: any}) => void, visible: boolean, defaultSize: {width: any, height: any}, enable: {right: boolean}, minWidth: number, title: string, handlerMinimize: () => void, contentHeight: any, maxWidth: number}}
     */
    const restMovableSidebarProps : MovableSidebarProps  = {
        contentHeight: toolContentHeight,
        onResize: (event: any, direction: any, elementRef: { offsetWidth: any; }) => handleReSizeChange(elementRef.offsetWidth),
        title: <h5>{l(leftContainer.selectKey)}</h5>,
        handlerMinimize: ()=> handleMinimize(),
        handlerMaxsize: () => handleMaxsize(size.width - 2 * VIEW.leftToolWidth - rightContainer.width - 700),
        visible: leftContainer.selectKey !== "",
        defaultSize: {width: leftContainer.width, height: leftContainer.height},
        minWidth: 260,
        maxWidth: size.width - 2 * VIEW.leftToolWidth - rightContainer.width - 700, //
        enable: {right: true},
        style:{borderInlineEnd: "1px solid #E0E2E5"}
    }


  return(
    <MovableSidebar {...restMovableSidebarProps}>
      <Tabs activeKey={leftContainer.selectKey} items={LeftSide} tabBarStyle={{display: "none"}}/>
    </MovableSidebar>
  )
}

export default connect(({Studio}: { Studio: StateType }) => ({
  leftContainer: Studio.leftContainer,
  rightContainer: Studio.rightContainer,
  toolContentHeight: Studio.toolContentHeight,
}))(LeftContainer);

