import {Space, Tabs} from "antd";
import MovableSidebar, {MovableSidebarProps} from "@/components/Sidebar/MovableSidebar";
import React, {useState} from "react";
import {connect} from "@@/exports";
import {StateType, VIEW} from "@/pages/DataStudio/model";
import {BtnRoute, LeftSide} from "@/pages/DataStudio/route";
import {l} from "@/utils/intl";
import Title from "@/components/Front/Title";
import useThemeValue from "@/hooks/useThemeValue";
import {PlusOutlined} from "@ant-design/icons";
import {CircleBtn, CircleButtonProps} from "@/components/CallBackButton/CircleBtn";

export type LeftContainerProps = {
  size: number
}
const LeftContainer: React.FC<LeftContainerProps> = (props: any) => {
  const {dispatch, size,toolContentHeight, leftContainer, rightContainer} = props;
  const themeValue = useThemeValue();

  const MAX_WIDTH = size.width - 2 * VIEW.leftToolWidth - rightContainer.width - 700
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
  const handleMaxsize = () => {
    handleReSizeChange(MAX_WIDTH)
  }

  const content = (
    <Tabs activeKey={leftContainer.selectKey} items={LeftSide} tabBarStyle={{display: "none"}}/>
  )
  /**
   * 侧边栏属性
   * @type {{onResize: (event: any, direction: any, elementRef: {offsetWidth: any}) => void, visible: boolean, defaultSize: {width: any, height: any}, enable: {right: boolean}, minWidth: number, title: string, handlerMinimize: () => void, contentHeight: any, maxWidth: number}}
   */
  const restMovableSidebarProps: MovableSidebarProps = {
    contentHeight: toolContentHeight,
    onResize: (event: any, direction: any, elementRef: {
      offsetWidth: any;
    }) => handleReSizeChange(elementRef.offsetWidth),
    title: <Title>{l(leftContainer.selectKey)}</Title>,
    handlerMinimize: () => handleMinimize(),
    handlerMaxsize: handleMaxsize,
    visible: leftContainer.selectKey !== "",
    defaultSize: {width: leftContainer.width, height: leftContainer.height},
    minWidth: 260,
    maxWidth: MAX_WIDTH, //
    enable: {right: true},
    btnGroup: BtnRoute[leftContainer.selectKey] ? BtnRoute[leftContainer.selectKey].map((item: CircleButtonProps) => {
      return <CircleBtn title={item.title} icon={item.icon} onClick={item.onClick} key={item.title}/>
    }) : [],
    style: {borderInlineEnd: "1px solid " + themeValue.borderColor}
  }


  return (
    <MovableSidebar {...restMovableSidebarProps}>
      {/*<Tabs activeKey={leftContainer.selectKey} items={LeftSide} tabBarStyle={{display: "none"}}/>*/}
      {content}
    </MovableSidebar>
  )
}

export default connect(({Studio}: { Studio: StateType }) => ({
  leftContainer: Studio.leftContainer,
  rightContainer: Studio.rightContainer,
  toolContentHeight: Studio.toolContentHeight,
}))(LeftContainer);

