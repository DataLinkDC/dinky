import React from "react";
import MovableSidebar from "@/components/Sidebar/MovableSidebar";
import {l} from "@/utils/intl";
import {StateType, VIEW} from "@/pages/DataStudio/model";
import {ConfigProvider, Space, Tabs} from "antd";
import {LeftBottomMoreTabs, LeftBottomSide} from "@/pages/DataStudio/route";
import {connect} from "@@/exports";
import {Title} from "@/components/StyledComponents";
import ContentScroll from "@/components/Scroll/ContentScroll";

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

    const updateSelectBottomSubKey = (key: string) => {
        dispatch({
            type: "Studio/updateSelectBottomSubKey",
            payload: key,
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
    const getSubTabs = () => {
      // @ts-ignore
      return Object.values(Object.keys(LeftBottomMoreTabs).map(x=>LeftBottomMoreTabs[x].map(y=>{return{...y,key:x+"/"+y.key}}))).flatMap(x=>x)
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

    const renderTabPane = () => {
      // @ts-ignore
      const leftBottomMoreTab = LeftBottomMoreTabs[bottomContainer.selectKey];
      if (leftBottomMoreTab) {
         const items=leftBottomMoreTab.map((item: any) => {
           return {
             key: bottomContainer.selectKey+"/"+item.key,
             label: <span>{item.icon}{item.label}</span>
           }
        })
        // updateSelectBottomSubKey(items[0].key.split("/")[1])
        return (<Tabs
            style={{height:"32px",display:"-webkit-box"}}
            items={items}
            type="card"
            onChange={(key: string) => {
              updateSelectBottomSubKey(key.split("/")[1])
            }}
            activeKey={bottomContainer.selectKey+"/"+bottomContainer.selectSubKey[bottomContainer.selectKey]}
            size="small" tabPosition="top"  />
        )
      }
      return <></>
    }
    const renderItems = () => {
     return [...LeftBottomSide.map(x=>{return{...x,key:x.key+"/"}})
        ,...getSubTabs()].map((item) =>{
          return {...item,children:
              <ContentScroll height={props.bottomContainer.height - 53} >
                 {item.children}
              </ContentScroll>
        }
      })
    }

    // @ts-ignore
  return (
        <MovableSidebar
          title={
            <ConfigProvider
              theme={{
                components:{
                  Tabs:{
                    horizontalMargin:"0",
                    cardPaddingSM:"6px",
                    horizontalItemPadding:"0",
                  }
                }
              }}
            >
              <Space>
                <Title >{l(bottomContainer.selectKey)}</Title>
                {renderTabPane()}
              </Space>
            </ConfigProvider>
          }
            visible={bottomContainer.selectKey !== ""}
            style={{zIndex: 999 ,height: bottomContainer.height ,marginTop: 0,backgroundColor:"#fff"}}
            defaultSize={{width: "100%", height: bottomContainer.height}}
            minHeight={VIEW.midMargin}
            maxHeight={size.contentHeight - 40}
            onResize={(event: any, direction: any, elementRef: { offsetHeight: any; }) => resizeCallback(event, direction, elementRef)}
            enable={{top: true}}
            handlerMinimize={handleMinimize}
        >
            <Tabs activeKey={bottomContainer.selectKey+"/"+(bottomContainer.selectSubKey[bottomContainer.selectKey]?bottomContainer.selectSubKey[bottomContainer.selectKey]:"")}
                  items={renderItems()}
                  tabBarStyle={{display: "none"}}/>
        </MovableSidebar>
    )
}

export default connect(({Studio}: { Studio: StateType }) => ({
    leftContainer: Studio.leftContainer,
    bottomContainer: Studio.bottomContainer,
    centerContentHeight: Studio.centerContentHeight,
}))(BottomContainer);
