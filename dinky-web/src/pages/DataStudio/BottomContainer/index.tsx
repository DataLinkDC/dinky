import React from "react";
import MovableSidebar from "@/components/Sidebar/MovableSidebar";
import {l} from "@/utils/intl";
import {StateType, VIEW} from "@/pages/DataStudio/model";
import {ConfigProvider, Space, Tabs} from "antd";
import {LeftBottomSide} from "@/pages/DataStudio/route";
import {connect} from "@@/exports";
import {Title} from "@/components/StyledComponents";

export type BottomContainerProps = {
  size:number
}
const BottomContainer:React.FC<BottomContainerProps> = (props:any) => {
  const {dispatch,size} = props;


  const {bottomContainer} = props


  /**
   * 侧边栏最小化
   */
  const handleMinimize = () => {
    dispatch({
      type: 'Studio/updateSelectBottomKey',
      payload: "",
    })
  }
  const updateBottomHeight= (height: number) =>{
    dispatch({
    type: "Studio/updateBottomHeight",
    payload: height,
  })
  }
  const updateCenterContentHeight= (height: number) =>{
    dispatch({
    type: "Studio/updateCenterContentHeight",
    payload: height,
  })
  }
  const updateToolContentHeight= (height: number) =>{
    dispatch({
    type: "Studio/updateToolContentHeight",
    payload: height,
  })
  }



  return(
    <MovableSidebar
      title={
      <ConfigProvider
        theme={{
          components:{
            Tabs:{
              horizontalMargin:"0"
            }
          }
        }}
      >

      <Space>
     <Title >{(bottomContainer.selectKey===''?"":l(bottomContainer.selectKey))}</Title>
      <Tabs
        style={{height:"43px",display:"-webkit-box"}}
        items={[{
          key: "test",
          label: "123"
        },{
          key: "tes2",
          label: "123"
        }]}
        type="editable-card"
        hideAdd
        defaultActiveKey="StudioMsg" size="small" tabPosition="top"  />
      </Space>
      </ConfigProvider>
      }
      visible={props.bottomContainer.selectKey !== ""}
      style={{
        border: "solid 1px #ddd",
        background: "#f0f0f0",
        zIndex: 999
      }}
      defaultSize={{
        width: "100%",
        height: props.bottomContainer.height
      }}
      minHeight={VIEW.midMargin+10}
      maxHeight={size.contentHeight - 40}
      onResize={(event: any, direction: any, elementRef: { offsetHeight: any; }) => {
        updateBottomHeight(elementRef.offsetHeight)
        const centerContentHeight = document.documentElement.clientHeight - VIEW.headerHeight - VIEW.headerNavHeight - VIEW.footerHeight - VIEW.otherHeight - props.bottomContainer.height;
        updateCenterContentHeight(centerContentHeight)
        updateToolContentHeight(centerContentHeight-VIEW.midMargin)
      }}
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
