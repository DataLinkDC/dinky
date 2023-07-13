import {AndroidOutlined, AppleOutlined, BackwardOutlined, ReloadOutlined} from "@ant-design/icons";
import {Button, ConfigProvider, Space, Tabs, TabsProps} from "antd";
import React, {useRef, useState} from "react";
import {connect} from "@@/exports";
import {StateType, TabsItemType} from "@/pages/DataStudio/model";
import {children} from "@umijs/utils/compiled/cheerio/lib/api/traversing";
import DataSourceDetail from "@/pages/RegCenter/DataSource/components/DataSourceDetail";
import {DataSourceDetailBackButton, TestDiv} from "@/components/StyledComponents";
import RightTagsRouter from "@/pages/RegCenter/DataSource/components/DataSourceDetail/RightTagsRouter";
import {l} from "@/utils/intl";


type TargetKey = React.MouseEvent | React.KeyboardEvent | string;

const MiddleContainer = (props:any) => {
  const {tabs:{panes,activeKey},dispatch}= props;
  const tabItems:TabsProps['items']= (panes as TabsItemType[]).map(item=>{
    const children = () => {
      switch (item.type) {
        case "metadata":
          return <RightTagsRouter
            tableInfo={item.params.tableInfo}
            queryParams={item.params.queryParams} rightButtons={<DataSourceDetailBackButton>
            <Space size={'middle'}>
              <Button size={'middle'} icon={<ReloadOutlined spin={loading}/>} type="primary"
                      onClick={() => querySchemaTree()}>{l('button.refresh')}</Button>
            </Space>
          </DataSourceDetailBackButton>} tagDisabled={false}/>
        default:
          return <></>
      }
    }

    return {key:item.key,label:item.label,children:<div style={{height:activeKey===item.key?props.centerContentHeight-40:0,overflow:"auto"}}>{children()}</div>}
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
    <Tabs
      hideAdd
      onChange={updateActiveKey}
      activeKey={activeKey}
      type="editable-card"
      onEdit={remove}
      items={tabItems}
    />
    </ConfigProvider>
  )
}
export default connect(({Studio}: { Studio: StateType }) => ({
  tabs: Studio.tabs,
  centerContentHeight:Studio.centerContentHeight
}))(MiddleContainer);
