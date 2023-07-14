import {Card, ConfigProvider, Tabs, TabsProps} from "antd";
import React from "react";
import {connect} from "@@/exports";
import {StateType, TabsItemType} from "@/pages/DataStudio/model";
import RightTagsRouter from "@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter";
import {renderDBIcon} from "@/pages/RegCenter/DataSource/components/function";
import KeyBoard from "@/pages/DataStudio/MiddleContainer/KeyBoard";
import {ProCard} from "@ant-design/pro-components";


type TargetKey = React.MouseEvent | React.KeyboardEvent | string;

const MiddleContainer = (props:any) => {
  const {tabs:{panes,activeKey},dispatch}= props;
  const tabItems:TabsProps['items']= (panes as TabsItemType[]).map(item=>{
    const children = () => {
      switch (item.type) {
        case "metadata":
          return <RightTagsRouter
            tableInfo={item.params.tableInfo}
            queryParams={item.params.queryParams} rightButtons={<></>} tagDisabled={false}/>
        default:
          return <></>
      }
    }

    return {key:item.key,label:<span>{renderDBIcon(item.icon,20)}{item.label}</span>
      ,children:<div style={{height:activeKey===item.key?props.centerContentHeight-40:0,overflow:"auto"}}>{children()}</div>}
  })

  const updateActiveKey = (key: string) => {
    dispatch({
      type: 'Studio/updateTabsActiveKey',
      payload: parseInt(key),
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
            <Card style={{height:props.centerContentHeight,overflow:"auto"}}>
              <KeyBoard  />
            </Card>
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
