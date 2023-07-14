import {Card, ConfigProvider, Menu, Space, Tabs, TabsProps} from "antd";
import React from "react";
import {connect} from "@@/exports";
import {MetadataParams, StateType, TabsItemType} from "@/pages/DataStudio/model";
import RightTagsRouter from "@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter";
import {renderDBIcon} from "@/pages/RegCenter/DataSource/components/function";
import KeyBoard from "@/pages/DataStudio/MiddleContainer/KeyBoard";
import {ProCard} from "@ant-design/pro-components";
import {l} from "@/utils/intl";
import { MenuItemType } from "rc-menu/lib/interface";


type TargetKey = React.MouseEvent | React.KeyboardEvent | string;

const MiddleContainer = (props:any) => {
  const {tabs:{panes,activeKey},dispatch}= props;


  const handleClickMenu = (e: any, current: any) => {
    props.closeTabs(current, e.key);
  };

  const renderRightMenu = (pane: any) => {

      const menuItems: MenuItemType[] = [
              {
                  key: "CLOSE_ALL",
                  label: l('right.menu.closeAll'),
                  onClick: (info) => {
                      dispatch({
                          type: 'Studio/closeAllTabs',
                          payload: info,
                      });
                  },
              },
              {
                  key: "CLOSE_OTHER",
                  label: l('right.menu.closeOther'),
                  onClick: (info) => {
                      dispatch({
                          type: 'Studio/closeOtherTabs',
                          payload: info,
                      });
                  },
              }
          ]

     return <>
         <Menu onClick={(e) => handleClickMenu(e, pane)} items={menuItems}/>
     </>
};


  const tabItems = (panes).map((item: TabsItemType)=>{
    const children = () => {
      switch (item.type) {
        case "metadata":
          const params = item.params as MetadataParams;
          return <RightTagsRouter
            tableInfo={params.tableInfo}
            queryParams={params.queryParams} rightButtons={<></>} tagDisabled={false}/>
        default:
          return <></>
      }
    }
//  <TabPane tab={Tab(pane)} key={pane.key} closable={pane.closable}>
    return {key:item.key,label:<span >{renderDBIcon(item.icon,20)}{item.label}</span>
        ,children: <div style={{height:activeKey === item.key ? props.centerContentHeight - 40 : 0,overflow:"auto"}}>{children()}</div>}

  })

  const updateActiveKey = (key: string) => {
    dispatch({
      type: 'Studio/updateTabsActiveKey',
      payload: key,
    })
  };


  const remove = (targetKey: TargetKey) => {
    dispatch({
      type: 'Studio/closeTab',
      payload: targetKey,
    })
  };

  const onEdit = (targetKey: TargetKey, action: 'add' | 'remove') => {
    if (action === 'add') {
    } else {
      // remove(targetKey);
    }
  };
  return (
    <ConfigProvider
      theme={{
        components:{
          Tabs:{
            margin:0
          }
        }
    }}
    >
      {
        (tabItems?.length === 0) ?
            <ProCard ghost style={{height:props.centerContentHeight,overflow:"auto"}}><KeyBoard/></ProCard>
            :
            <ProCard ghost style={{height:props.centerContentHeight}}>
              <Tabs
                  hideAdd
                  onChange={updateActiveKey}
                  activeKey={activeKey}
                  type="editable-card"
                  onEdit={remove}
                  items={tabItems}
              />
            </ProCard>
      }
    </ConfigProvider>
  )
}
export default connect(({Studio}: { Studio: StateType }) => ({
  tabs: Studio.tabs,
  centerContentHeight:Studio.centerContentHeight
}))(MiddleContainer);
